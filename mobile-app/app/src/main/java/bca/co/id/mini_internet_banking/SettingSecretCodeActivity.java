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
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingSecretCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private EditText txtOldCode, txtNewCode, txtReCode;
    private Button btnChangeCode;
    private Spinner txtSettingNoRek;
    private Context mContext;
    private String TAG = SettingSecretCodeActivity.class.getSimpleName();
    private SharedPreferences sp;
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
    private final List<String> listRekeningNum = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_secret_code);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        txtSettingNoRek = findViewById(R.id.txtSettingNoRek);
        txtOldCode = findViewById(R.id.txtOldCode);
        txtNewCode = findViewById(R.id.txtNewCode);
        txtReCode = findViewById(R.id.txtReCode);
        btnChangeCode = findViewById(R.id.btnChangeCode);

        for (Rekening rek: Nasabah.rekenings){
            listRekeningNum.add(rek.getRekeningNum());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingSecretCodeActivity.this,
                android.R.layout.simple_spinner_item, listRekeningNum);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSettingNoRek.setAdapter(adapter);

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.setting_code_toolbar);
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

        btnChangeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCode();
            }
        });
    }

    //encrypt old, new and retype secret code, checking secret code strength, field empty, nCode = rCode, and send data to server
    private void changeCode(){
        String oCode = txtOldCode.getText().toString();
        final String nCode = txtNewCode.getText().toString();
        final String rCode = txtReCode.getText().toString();

        final Intent intent = new Intent(this, SettingActivity.class);

        String hashOCode = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(oCode.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashOCode = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashOCode.length() < 32 ){
                hashOCode = "0" + hashOCode;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Hashing Old Code failed: " + e.getMessage());
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing Old code failed: " + e.getMessage());
            e.printStackTrace();
        }

        String checkCode = "";
        final String rekNum = txtSettingNoRek.getSelectedItem().toString();

        for (Rekening rek: Nasabah.rekenings){
            if (rek.getRekeningNum().equals(rekNum)){
                checkCode = rek.getSecretCode();
                break;
            }
        }

        if (!oCode.equals("") && !nCode.equals("") && !rCode.equals("")){
            if (hashOCode.equals(checkCode)){
                if (nCode.equals(rCode)){
                    if(CodeStrength.calculateStrength(nCode).getValue() > CodeStrength.MEDIUM.getValue()) {
                        String hashNCode = "";
                        try {
                            MessageDigest m = MessageDigest.getInstance("MD5");
                            m.reset();
                            m.update(nCode.getBytes());
                            byte[] digest = m.digest();
                            BigInteger bigInt = new BigInteger(1,digest);
                            hashNCode = bigInt.toString(16);
                            // Now we need to zero pad it if you actually want the full 32 chars.
                            while(hashNCode.length() < 32 ){
                                hashNCode = "0" + hashNCode;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.e(TAG, "Hashing new Code Failed: " + e.getMessage());
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing new code failed: " + e.getMessage());
                            e.printStackTrace();
                        }

                        String hashRCode = "";
                        try {
                            MessageDigest m = MessageDigest.getInstance("MD5");
                            m.reset();
                            m.update(rCode.getBytes());
                            byte[] digest = m.digest();
                            BigInteger bigInt = new BigInteger(1,digest);
                            hashRCode = bigInt.toString(16);
                            // Now we need to zero pad it if you actually want the full 32 chars.
                            while(hashRCode.length() < 32 ){
                                hashRCode = "0" + hashRCode;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.e(TAG, "Hahing retype code failed: " + e.getMessage());
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing retype code failed: " + e.getMessage());
                            e.printStackTrace();
                        }

                        OkHttpClient client = new OkHttpClient();

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        String url = HttpClientURL.urlUpdateCode;

                        JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("no_rek", rekNum);
                            jsonParams.put("kode_rahasiaL", hashOCode);
                            jsonParams.put("krb1", hashNCode);
                            jsonParams.put("krb2", hashRCode);
                        } catch (JSONException e) {
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Failed to create JSONObject for post setting code param: " + e.getMessage());
                            Log.e(TAG, "Failed to create JSONObject for post setting code param: " + e.getMessage());
                            e.printStackTrace();
                        }

                        RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        final String finalHashNCode = hashNCode;
                        final String finalHashOCode = hashOCode;
                        final String finalHashRCode = hashRCode;
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response from async okhttp call");
                                Log.e(TAG, "error getting response from async okhttp call");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string().toString();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    final String result = jsonObject.getString("status");

                                    if (result.equalsIgnoreCase("berhasil")) {
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Update secret code success, [" +
                                                "Kode Rahasia Lama = " + finalHashOCode +
                                                ", Kode Rahasia Baru = " + finalHashNCode +
                                                ", Retype Kode Rahasia = " + finalHashRCode +
                                                ", Rekening Num = " + rekNum);

                                        Log.i(TAG, "Update secret code success, [" +
                                                "Kode Rahasia Lama = " + finalHashOCode +
                                                ", Kode Rahasia Baru = " + finalHashNCode +
                                                ", Retype Kode Rahasia = " + finalHashRCode +
                                                ", Rekening Num = " + rekNum);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Kode Rahasia Berhasil!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        SharedPreferences.Editor spEdit = sp.edit();
                                        spEdit.putString("code", finalHashNCode);
                                        spEdit.commit();
                                        for (Rekening rek: Nasabah.rekenings){
                                            if (rek.getRekeningNum().equals(rekNum)){
                                                rek.setSecretCode(finalHashNCode);
                                                break;
                                            }
                                        }
                                        writeLogs();
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Update ocde failed with message: " + result);
                                        Log.e(TAG, "Update code failed with message: " + result);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Kode Rahasia Gagal: " + result, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        writeLogs();
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
                                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });
                    } else{
                        Log.e(TAG, "Secret code didn't fulfill minimum requirement");
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Secret code didn't fulfill minimum requirement");
                        Toast.makeText(
                                this,
                                "Kode Rahasia harus terdiri 6 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Log.e(TAG, "New Code and Retype Code not same");
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "New Code and Retype Code not same");
                    Toast.makeText(this, "Kode baru dan Re-type kode tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Log.e(TAG, "New secret code wrong");
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "New secret code wrong");
                Toast.makeText(this, "Kode sekarang salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Log.e(TAG, "There is an empty field");
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "There is an empty field");
            Toast.makeText(this, "Semua kolom harus terisi!", Toast.LENGTH_LONG).show();
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
        writeLogs();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadBalanceInfoView(){
        writeLogs();
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    private void loadMutationView(){
        writeLogs();
        Intent intent = new Intent(this, MutationRekeningActivity.class);
        startActivity(intent);
    }

    private void loadTransferView(){
        writeLogs();
        Intent intent = new Intent(this, TransferActivity.class);
        startActivity(intent);
    }

    private void loadBuyingView(){
        writeLogs();
        Intent intent = new Intent(this, BuyingActivity.class);
        startActivity(intent);
    }

    private void loadHistoryView(){
        writeLogs();
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void loadSettingView(){
        writeLogs();
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void loadLoginView(){
        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + " Logout, remove session from app");
        Log.i(TAG, "Logout, remove session from app");
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putBoolean("isLogin", false);
        spEdit.putString("id", "");
        spEdit.putString("email", "");
        spEdit.putString("username", "");
        spEdit.putString("name", "");
        spEdit.putString("password", "");
        spEdit.putString("ktpNum", "");
        spEdit.putString("birthday", "");
        spEdit.putString("address", "");
        spEdit.putString("rekenings", "");
        spEdit.commit();

        writeLogs();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //send log to server
    private void writeLogs(){
        OkHttpClient client = new OkHttpClient();
        String url = HttpClientURL.urlWriteLog;
        MediaType JSON = MediaType.parse("application/json' charset=utf-8");

        JSONArray arrLog = new JSONArray(listLog);

        JSONObject jsonLogs = new JSONObject();
        try {
            jsonLogs.put("logs", arrLog);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, jsonLogs.toString());

        RequestBody body = RequestBody.create(JSON, jsonLogs.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error in getting response from async okhttp call");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    Log.i(TAG, "Write log success");
                } else{
                    Log.i(TAG, "Write log failed");
                }

                //String responseBody = response.body().string().toString();
                //Log.e(TAG, responseBody);
            }
        });
    }
}
