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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TransferActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnSubmitTransfer;
    private LinearLayout inputCode;
    private EditText inputNorekTransfer, inputNominalTransfer, inputKetTransfer;
    private Context mContext;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        btnSubmitTransfer = findViewById(R.id.btnSubmitTransfer);
        inputCode = findViewById(R.id.inputCode);
        inputNorekTransfer = findViewById(R.id.inputNoRekTransfer);
        inputNominalTransfer = findViewById(R.id.inputNominalTransfer);
        inputKetTransfer = findViewById(R.id.inputKetTransfer);

        Toolbar toolbar = findViewById(R.id.transfer_toolbar);
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

        btnSubmitTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTransfer();
            }
        });
    }

    private void submitTransfer(){
        final String noRek = inputNorekTransfer.getText().toString();
        final String nominal = inputNominalTransfer.getText().toString();
        final String ket = inputKetTransfer.getText().toString();

        final Intent intent = new Intent(this, TransferCodeActivity.class);

        if (!noRek.equals("") && !nominal.equals("") && !ket.equals("")){
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();

            try {
                jsonParams.put("no_rek_tujuan", noRek);
                jsonParams.put("id_nasabah", Nasabah.id);
                jsonParams.put("nominal", nominal);
                jsonParams.put("keterangan", ket);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                StringEntity entity = new StringEntity(jsonParams.toString());

                client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/transfer/cek-no-rek.php", entity, "application/json", new AsyncHttpResponseHandler() {
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

                            String check = jsonObject.getString("check");
                            String message = jsonObject.getString("message");

                            if (check.equalsIgnoreCase("true")){
                                intent.putExtra("noRek", message);
                                intent.putExtra("nominal", nominal);
                                intent.putExtra("ket", ket);
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
