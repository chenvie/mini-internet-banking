package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class BuyingCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView txtNoHpBuying, txtProviderBuying, txtNominalBuying;
    private EditText txtCodeBuying;
    private Button btnBuyingWithCode;
    private SharedPreferences sp;
    private String noHp, provider, nominal;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buying_code);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        txtProviderBuying = findViewById(R.id.txtProviderBuying);
        txtNominalBuying = findViewById(R.id.txtNominalBuying);
        txtNoHpBuying = findViewById(R.id.txtNoHpBuying);
        txtCodeBuying = findViewById(R.id.txtCodeBuying);
        btnBuyingWithCode = findViewById(R.id.btnBuyingWithCode);

        NumberFormat formatter = new DecimalFormat("#,###");

        Intent intent = getIntent();
        noHp = intent.getStringExtra("noHp");
        nominal = intent.getStringExtra("nominal");
        provider = intent.getStringExtra("provider");

        txtNoHpBuying.setText(noHp);
        txtNominalBuying.setText("Rp " + (formatter.format(Float.parseFloat(nominal))).toString() + ",-");
        txtProviderBuying.setText(provider);

        Toolbar toolbar = findViewById(R.id.buying_code_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.nav_home){
                            loadHomeView();
                        } else if (id == R.id.nav_balance) {
                            loadBalanceInfoView();
                        }else if (id == R.id.nav_mutation){
                            loadMutationView();
                        } else if (id == R.id.nav_transfer) {
                            loadTransferView();
                        } else if (id == R.id.nav_buying){
                            loadBuyingView();
                        } else if (id == R.id.nav_history){
                            loadHistoryView();
                        } else if (id == R.id.nav_setting){
                            loadSettingView();
                        } else{
                            loadLoginView();
                        }
                        return true;
                    }
                });

        btnBuyingWithCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBuying();
            }
        });
    }

    private void submitBuying(){
        String code = txtCodeBuying.getText().toString();
        final float temp = Nasabah.saldo - Float.parseFloat(nominal);
        final Intent intent = new Intent(this, BuyingStatusActivity.class);

        if(code.equals(Nasabah.code)) {
            if (!noHp.equals("")) {
                if (temp > 0) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("username", Nasabah.username);
                        jsonParams.put("no_hp_tujuan", noHp);
                        jsonParams.put("id_nasabah", Nasabah.id);
                        jsonParams.put("provider", provider);
                        jsonParams.put("kode_rahasia", code);
                        jsonParams.put("nominal", nominal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        StringEntity entity = new StringEntity(jsonParams.toString());

                        client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/pulsa/create.php", entity, "application/json", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String json = new String(responseBody);
                                int jsonStart = json.indexOf("{");
                                int jsonEnd = json.indexOf("}");

                                if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart){
                                    json = json.substring(jsonStart, jsonEnd+1);
                                }

                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    String status = jsonObject.getString("pulsa");
                                    String message = jsonObject.getString("message");

                                    if (status.equalsIgnoreCase("true")){
                                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                        intent.putExtra("noHp", noHp);
                                        intent.putExtra("nominal", nominal);
                                        intent.putExtra("provider", provider);
                                        intent.putExtra("status", true);
                                        Nasabah.saldo = temp;
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    intent.putExtra("noHp", noHp);
                    intent.putExtra("nominal", nominal);
                    intent.putExtra("provider", provider);
                    intent.putExtra("status", false);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
            }
        } else{
          Toast.makeText(this, "Kode Rahasia salah!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadHomeView() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadBalanceInfoView(){
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    private void loadMutationView(){
        Intent intent = new Intent(this, MutationActivity.class);
        startActivity(intent);
    }

    private void loadTransferView(){
        Intent intent = new Intent(this, TransferActivity.class);
        startActivity(intent);
    }

    private void loadBuyingView(){
        Intent intent = new Intent(this, BuyingActivity.class);
        startActivity(intent);
    }

    private void loadHistoryView(){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void loadSettingView(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void loadLoginView(){
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putBoolean("isLogin", false);
        spEdit.putString("id", "");
        spEdit.putString("name", "");
        spEdit.putString("username", "");
        spEdit.putString("password", "");
        spEdit.putString("code", "");
        spEdit.putString("birthday", "");
        spEdit.putString("rekeningNum", "");
        spEdit.putFloat("saldo", 0);
        spEdit.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
