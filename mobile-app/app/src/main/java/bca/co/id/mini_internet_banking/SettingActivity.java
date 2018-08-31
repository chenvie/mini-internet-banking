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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private EditText txtOldPass, txtNewPass, txtRePass, txtOldCode, txtNewCode, txtReCode;
    private Button btnChangePass, btnChangeCode;
    private Context mContext;
    private String TAG = SettingActivity.class.getSimpleName();
    private SharedPreferences sp;
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        txtOldPass = findViewById(R.id.txtOldPass);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtRePass = findViewById(R.id.txtRePass);
        txtOldCode = findViewById(R.id.txtOldCode);
        txtNewCode = findViewById(R.id.txtNewCode);
        txtReCode = findViewById(R.id.txtReCode);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnChangeCode = findViewById(R.id.btnChangeCode);

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
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

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnChangeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCode();
            }
        });
    }

    //encrypt old, new and retype password, checking password strength, field empty, nPass = rPass, and send data to server
    private void changePassword(){
        final String oPass = txtOldPass.getText().toString();
        final String nPass = txtNewPass.getText().toString();
        final String rPass = txtRePass.getText().toString();

        final Intent intent = new Intent(this, HomeActivity.class);

        String hashOPassword = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(oPass.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashOPassword = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashOPassword.length() < 32 ){
                hashOPassword = "0" + hashOPassword;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Hashing old password failed: " + e.getMessage());
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing old password failed: " + e.getMessage());
            e.printStackTrace();
        }

        if (!oPass.equals("") && !nPass.equals("") && !rPass.equals("")){
            if (hashOPassword.equals(Nasabah.password)){
                if (nPass.equals(rPass)){
                    if(PasswordStrength.calculateStrength(nPass).getValue() > PasswordStrength.MEDIUM.getValue()){
                        String hashNPassword = "";
                        try {
                            MessageDigest m = MessageDigest.getInstance("MD5");
                            m.reset();
                            m.update(nPass.getBytes());
                            byte[] digest = m.digest();
                            BigInteger bigInt = new BigInteger(1,digest);
                            hashNPassword = bigInt.toString(16);
                            // Now we need to zero pad it if you actually want the full 32 chars.
                            while(hashNPassword.length() < 32 ){
                                hashNPassword = "0" + hashNPassword;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.e(TAG, "Hashing new password failed: " + e.getMessage());
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing new password error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        String hashRPassword = "";
                        try {
                            MessageDigest m = MessageDigest.getInstance("MD5");
                            m.reset();
                            m.update(rPass.getBytes());
                            byte[] digest = m.digest();
                            BigInteger bigInt = new BigInteger(1,digest);
                            hashRPassword = bigInt.toString(16);
                            // Now we need to zero pad it if you actually want the full 32 chars.
                            while(hashRPassword.length() < 32 ){
                                hashRPassword = "0" + hashRPassword;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.e(TAG, "Hashing retype password failed: " + e.getMessage());
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing retype password failed: " + e.getMessage());
                            e.printStackTrace();
                        }

                        OkHttpClient client = new OkHttpClient();

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        String url = HttpClientURL.urlUpdatePassword;

                        JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("id_nasabah", Nasabah.id);
                            jsonParams.put("passwordl", hashOPassword);
                            jsonParams.put("passwordb1", hashNPassword);
                            jsonParams.put("passwordb2", hashRPassword);
                        } catch (JSONException e) {
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Failed to create JSONObject for post setting password param: " + e.getMessage());
                            Log.e(TAG, "Failed to create JSONOBject for post setting password param: " + e.getMessage());
                            e.printStackTrace();
                        }

                        RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        final String finalHashOPassword = hashOPassword;
                        final String finalHashNPassword = hashNPassword;
                        final String finalHashRPassword = hashRPassword;
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
                                    final String result = jsonObject.getString("message");

                                    if (result.equalsIgnoreCase("update password berhasil")) {
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Update password success, sending oPass, nPass, rPass, and nasabah id as parameter");
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Old Password (oPass) = " + finalHashOPassword);
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "New Password (nPass) = " + finalHashNPassword);
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Retype Password (rPass = " + finalHashRPassword);
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Nasabah id = " + Nasabah.id);
                                        Log.i(TAG, "Update password success, sending oPass, nPass, rPass, and nasabah id as parameter");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Password Berhasil!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        SharedPreferences.Editor spEdit = sp.edit();
                                        spEdit.putString("password", finalHashNPassword);
                                        spEdit.commit();
                                        Nasabah.password = finalHashNPassword;
                                        writeLogs();
                                        startActivity(intent);
                                    } else {
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Update password failed: " + result);
                                        Log.e(TAG, "Update password failed: " + result);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Password Gagal: " + result, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        writeLogs();
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
                                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error");
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
                        Log.e(TAG, "Password didn't fulfill minimum requirement");
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Password didn;t fulfill minimum requirement");
                        Toast.makeText(
                                this,
                                "Password harus terdiri min 8 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Log.e(TAG, "New Password and Retype Password not same");
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "New password dan retype password not same");
                    Toast.makeText(this, "Password baru dan Re-type password tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Log.e(TAG, "Old Password wrong");
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Old password wrong");
                Toast.makeText(this, "Password sekarang salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Log.e(TAG, "There is an empty field");
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "There is an empty field");
            Toast.makeText(this, "Semua kolom harus terisi!", Toast.LENGTH_LONG).show();
        }
    }

    //encrypt old, new and retype secret code, checking secret code strength, field empty, nCode = rCode, and send data to server
    private void changeCode(){
        String oCode = txtOldCode.getText().toString();
        final String nCode = txtNewCode.getText().toString();
        final String rCode = txtReCode.getText().toString();

        final Intent intent = new Intent(this, HomeActivity.class);

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

        if (!oCode.equals("") && !nCode.equals("") && !rCode.equals("")){
            if (hashOCode.equals(Nasabah.code)){
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
                            jsonParams.put("id_nasabah", Nasabah.id);
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
                                    final String result = jsonObject.getString("message");

                                    if (result.equalsIgnoreCase("update kode rahasia berhasil")) {
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Update secret code sucess, sending oCode, nCode, rCode, and nasabah id as parameter");
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Old Secret Code (oCode) = " + finalHashOCode);
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "New Secret Code (nCode) = " + finalHashNCode);
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Retype Secret Code (rCode) = " + finalHashRCode);

                                        Log.i(TAG, "Update secret code success, sending oCode, nCode, rCode, and nasabah id as parameter");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Kode Rahasia Berhasil!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        SharedPreferences.Editor spEdit = sp.edit();
                                        spEdit.putString("code", finalHashNCode);
                                        spEdit.commit();
                                        Nasabah.code = finalHashNCode;
                                        writeLogs();
                                        startActivity(intent);
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
        Intent intent = new Intent(this, MutationActivity.class);
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
        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Logout, remove session from app");
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
