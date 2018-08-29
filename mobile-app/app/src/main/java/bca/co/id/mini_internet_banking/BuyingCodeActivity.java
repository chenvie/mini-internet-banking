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

public class BuyingCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView txtNoHpBuying, txtProviderBuying, txtNominalBuying;
    private EditText txtCodeBuying;
    private Button btnBuyingWithCode;
    private SharedPreferences sp;
    private String noHp, provider, nominal;
    private Context mContext;
    private String TAG = BuyingCodeActivity.class.getSimpleName();

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
            Log.e(TAG, "Hashing password failed: " + e.getMessage());
            e.printStackTrace();
        }

        if(hashCode.equals(Nasabah.code)) {
            if (!noHp.equals("")) {
                if (temp > 0) {
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String url = "http://10.0.2.2/mini-internet-banking/API/pulsa/create.php";

                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("username", Nasabah.username);
                        jsonParams.put("no_hp_tujuan", noHp);
                        jsonParams.put("id_nasabah", Nasabah.id);
                        jsonParams.put("provider", provider);
                        jsonParams.put("kode_rahasia", hashCode);
                        jsonParams.put("nominal", nominal);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to create JSONObject for post param: " + e.getMessage());
                        e.printStackTrace();
                    }

                    final RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "error in getting respose from async okhttp call");
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
                                String status = jsonObject.getString("pulsa");
                                final String message = jsonObject.getString("message");

                                if (status.equalsIgnoreCase("true")){
                                    Log.i(TAG, "Buying Success, sending uname, noHp, nominal, provider, nasabah id, and secret code as parameter");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    intent.putExtra("noHp", noHp);
                                    intent.putExtra("nominal", nominal);
                                    intent.putExtra("provider", provider);
                                    intent.putExtra("status", true);

                                    Nasabah.saldo = temp;
                                    SharedPreferences.Editor spEdit = sp.edit();
                                    spEdit.putFloat("saldo", Nasabah.saldo);
                                    spEdit.commit();

                                    startActivity(intent);
                                    finish();
                                } else{
                                    Log.e(TAG, "Buying Failed with message: " + message);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Balance not enough");
                    intent.putExtra("noHp", noHp);
                    intent.putExtra("nominal", nominal);
                    intent.putExtra("provider", provider);
                    intent.putExtra("status", false);
                    startActivity(intent);
                }
            } else {
                Log.e(TAG, "Handphone number empty");
                Toast.makeText(this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
            }
        } else{
            Log.e(TAG, "Secret code wrong");
            Toast.makeText(this, "Kode Rahasia salah!" + Nasabah.code, Toast.LENGTH_LONG).show();
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
        Log.i(TAG, "Logout, remove session from app");
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
