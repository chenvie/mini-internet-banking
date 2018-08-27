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
import android.util.Log;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnTransferWithCode;
    private TextView txtNorekTransfer, txtNominalTransfer, txtKetTransfer;
    private EditText txtCodeTransfer;
    private String noRek, ket, nominal;
    private Context mContext;
    private SharedPreferences sp;
    private String TAG = TransferCodeActivity.class.getSimpleName();

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

        String hashCode = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(code.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashCode = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashCode.length() < 32 ){
                hashCode = "0" + hashCode;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        final Intent intent = new Intent(this, TransferStatusActivity.class);

        if (hashCode.equals(Nasabah.code)) {
            final float temp = Nasabah.saldo - Float.parseFloat(nominal);
            if (temp > 0) {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String url = "http://10.0.2.2/mini-internet-banking/API/transfer/create.php";

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("username", Nasabah.username);
                    jsonParams.put("no_rek_tujuan", noRek);
                    jsonParams.put("id_nasabah", Nasabah.id);
                    jsonParams.put("kode_rahasia", hashCode);
                    jsonParams.put("nominal", nominal);
                    jsonParams.put("keterangan", ket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "error getting response from async okhttp call");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string().toString();

                        int jsonStart = responseBody.indexOf("{");
                        int jsonEnd = responseBody.indexOf("}");

                        if (jsonStart > 0 && jsonEnd > 0 && jsonEnd > jsonStart){
                            responseBody = responseBody.substring(jsonStart, jsonEnd+1);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);

                            String status = jsonObject.getString("transfer");
                            final String message = jsonObject.getString("message");

                            if (status.equalsIgnoreCase("true")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                    }
                                });
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
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "Json error parsing: " + e.getMessage());
                                }
                            });
                        }
                    }
                });
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
