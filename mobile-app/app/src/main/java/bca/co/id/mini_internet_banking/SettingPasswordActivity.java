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
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingPasswordActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private EditText txtOldPass, txtNewPass, txtRePass;
    private Button btnChangePass;
    private Context mContext;
    private String TAG = SettingPasswordActivity.class.getSimpleName();
    private SharedPreferences sp;
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        txtOldPass = findViewById(R.id.txtOldPass);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtRePass = findViewById(R.id.txtRePass);
        btnChangePass = findViewById(R.id.btnChangePass);

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.setting_password_toolbar);
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
    }

    //encrypt old, new and retype password, checking password strength, field empty, nPass = rPass, and send data to server
    private void changePassword(){
        final String oPass = txtOldPass.getText().toString();
        final String nPass = txtNewPass.getText().toString();
        final String rPass = txtRePass.getText().toString();

        final Intent intent = new Intent(this, SettingActivity.class);

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
                                    final String result = jsonObject.getString("status");

                                    if (result.equalsIgnoreCase("berhasil")) {
                                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Update password success, [" +
                                                "Password Lama = " + finalHashOPassword +
                                                ", Password Baru = " + finalHashNPassword +
                                                ", Retype Password = " + finalHashRPassword +
                                                ", Nasabah id = " + Nasabah.id + "]");
                                        Log.i(TAG, "Update password success, [" +
                                                "Password Lama = " + finalHashOPassword +
                                                ", Password Baru = " + finalHashNPassword +
                                                ", Retype Password = " + finalHashRPassword +
                                                ", Nasabah id = " + Nasabah.id + "]");
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
                                        finish();
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
