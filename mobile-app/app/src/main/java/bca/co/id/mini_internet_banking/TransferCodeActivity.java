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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class TransferCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnTransferWithCode;
    private TextView txtNorekTransfer, txtNominalTransfer, txtKetTransfer;
    private EditText txtCodeTransfer;
    private String noRek, ket, nominal;
    private Context mContext;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_code);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        btnTransferWithCode = findViewById(R.id.btnTransferWithCode);
        txtNorekTransfer = findViewById(R.id.txtNoRekTransfer);
        txtNominalTransfer = findViewById(R.id.txtNominalTransfer);
        txtKetTransfer = findViewById(R.id.txtKetTransfer);
        txtCodeTransfer = findViewById(R.id.txtCodeTransfer);

        NumberFormat formatter = new DecimalFormat("#,###");

        Intent intent = getIntent();
        noRek = intent.getStringExtra("noRek");
        nominal = intent.getStringExtra("nominal");
        ket = intent.getStringExtra("ket");

        txtNorekTransfer.setText(noRek);
        txtNominalTransfer.setText("Rp " + (formatter.format(Float.parseFloat(nominal))).toString() + ",-");
        txtKetTransfer.setText(ket);

        Toolbar toolbar = findViewById(R.id.transfer_code_toolbar);
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

        btnTransferWithCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTransfer();
            }
        });
    }

    private void submitTransfer(){
        String code = txtCodeTransfer.getText().toString();

        final Intent intent = new Intent(this, TransferStatusActivity.class);

        if (code.equals(Nasabah.code)) {
            final float temp = Nasabah.saldo - Float.parseFloat(nominal);
            if (temp > 0) {
                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("username", Nasabah.username);
                    jsonParams.put("no_rek_tujuan", noRek);
                    jsonParams.put("id_nasabah", Nasabah.id);
                    jsonParams.put("kode_rahasia", code);
                    jsonParams.put("nominal", nominal);
                    jsonParams.put("keterangan", ket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    StringEntity entity = new StringEntity(jsonParams.toString());

                    client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/transfer/create.php", entity, "application/json", new AsyncHttpResponseHandler() {
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

                                String status = jsonObject.getString("transfer");
                                String message = jsonObject.getString("message");

                                if (status.equalsIgnoreCase("true")){
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                    Nasabah.saldo = temp;
                                    SharedPreferences.Editor spEdit = sp.edit();
                                    spEdit.putFloat("saldo", Nasabah.saldo);
                                    spEdit.commit();

                                    intent.putExtra("noRek", noRek);
                                    intent.putExtra("nominal", nominal);
                                    intent.putExtra("ket", ket);
                                    intent.putExtra("status", true);
                                    startActivity(intent);
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
                intent.putExtra("noRek", noRek);
                intent.putExtra("nominal", nominal);
                intent.putExtra("ket", ket);
                intent.putExtra("status", false);
                startActivity(intent);
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
