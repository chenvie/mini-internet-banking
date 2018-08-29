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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
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
                            e.printStackTrace();
                        }

                        OkHttpClient client = new OkHttpClient();

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        String url = "http://10.0.2.2/mini-internet-banking/API/nasabah/update_password.php";

                        JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("id_nasabah", Nasabah.id);
                            jsonParams.put("passwordl", hashOPassword);
                            jsonParams.put("passwordb1", hashNPassword);
                            jsonParams.put("passwordb2", hashRPassword);
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to create JSONOBject for post setting password param: " + e.getMessage());
                            e.printStackTrace();
                        }

                        RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        final String finalHashNPassword = hashNPassword;

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "error getting response from async okhttp call");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string().toString();

                                try {
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    final String result = jsonObject.getString("message");

                                    if (result.equalsIgnoreCase("update password berhasil")) {
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
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "Update password failed");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Password Gagal: " + result, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
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


//                        AsyncHttpClient client = new AsyncHttpClient();
//
//                        JSONObject jsonParams = new JSONObject();
//                        try {
//                            jsonParams.put("id_nasabah", Nasabah.id);
//                            jsonParams.put("passwordl", oPass);
//                            jsonParams.put("passwordb1", nPass);
//                            jsonParams.put("passwordb2", rPass);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        /*RequestParams rp = new RequestParams();
//                        rp.add("id_nasabah", Nasabah.id);
//                        rp.add("passwordl", oPass);
//                        rp.add("passwordb1", nPass);
//                        rp.add("passwordb2", rPass);*/
//
//                        try {
//                            StringEntity entity = new StringEntity(jsonParams.toString());
//
//                            client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/nasabah/update_password.php", entity, "application/json", new AsyncHttpResponseHandler() {
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                    String json = new String(responseBody);
//
//                                    int jsonStart = json.indexOf("{");
//                                    int jsonEnd = json.indexOf("}");
//
//                                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
//                                        json = json.substring(jsonStart, jsonEnd + 1);
//                                    }
//
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(json);
//                                        String result = jsonObject.getString("message");
//
//                                        if (result.equalsIgnoreCase("update password berhasil")) {
//                                            Toast.makeText(mContext, "Ubah Password Berhasil!", Toast.LENGTH_LONG).show();
//                                            SharedPreferences.Editor spEdit = sp.edit();
//                                            spEdit.putString("password", nPass);
//                                            spEdit.commit();
//                                            Nasabah.password = nPass;
//                                            startActivity(intent);
//                                        } else {
//                                            Toast.makeText(mContext, "Ubah Password Gagal: " + result, Toast.LENGTH_LONG).show();
//                                            startActivity(intent);
//                                        }
//                                    } catch (final JSONException e) {
//                                        Log.e(TAG, "Json parsing error change password: " + e.getMessage());
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getApplicationContext(), "Json parsing error change password: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                                }
//                            });
//                        }catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    } else{
                        Log.e(TAG, "Password didn't fulfill minimum requirement");
                        Toast.makeText(
                                this,
                                "Password harus terdiri min 8 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Log.e(TAG, "New Password and Retype Password not same");
                    Toast.makeText(this, "Password baru dan Re-type password tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Log.e(TAG, "Old Password wrong");
                Toast.makeText(this, "Password sekarang salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Log.e(TAG, "There is an empty field");
            Toast.makeText(this, "Semua kolom harus terisi!", Toast.LENGTH_LONG).show();
        }
    }

    private void changeCode(){
        String oCode = txtOldCode.getText().toString();
        final String nCode = txtNewCode.getText().toString();
        String rCode = txtReCode.getText().toString();

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
                            e.printStackTrace();
                        }

                        OkHttpClient client = new OkHttpClient();

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        String url = "http://10.0.2.2/mini-internet-banking/API/nasabah/update_kode_rahasia.php";

                        JSONObject jsonParams = new JSONObject();
                        try {
                            jsonParams.put("id_nasabah", Nasabah.id);
                            jsonParams.put("kode_rahasiaL", hashOCode);
                            jsonParams.put("krb1", hashNCode);
                            jsonParams.put("krb2", hashRCode);
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to create JSONObject for post setting code param: " + e.getMessage());
                            e.printStackTrace();
                        }

                        RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        final String finalHashNCode = hashNCode;
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "error getting response from async okhttp call");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseBody = response.body().string().toString();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    final String result = jsonObject.getString("message");

                                    if (result.equalsIgnoreCase("update kode rahasia berhasil")) {
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
                                        startActivity(intent);
                                    } else{
                                        Log.e(TAG, "Update password failed with message: " + result);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "Ubah Kode Rahasia Gagal: " + result, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
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

//                        AsyncHttpClient client = new AsyncHttpClient();
//
//                        JSONObject jsonParams = new JSONObject();
//                        try {
//                            jsonParams.put("id_nasabah", Nasabah.id);
//                            jsonParams.put("kode_rahasiaL", oCode);
//                            jsonParams.put("krb1", nCode);
//                            jsonParams.put("krb2", rCode);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        /*RequestParams rp = new RequestParams();
//                        rp.add("id_nasabah", Nasabah.id);
//                        rp.add("kode_rahasiaL", oCOde);
//                        rp.add("krb1", nCode);
//                        rp.add("krb2", rCode);*/
//
//                        try {
//                            StringEntity entity = new StringEntity(jsonParams.toString());
//
//                            client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/nasabah/update_kode_rahasia.php", entity, "application/json", new AsyncHttpResponseHandler() {
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                    String json = new String(responseBody);
//
//                                    int jsonStart = json.indexOf("{");
//                                    int jsonEnd = json.indexOf("}");
//
//                                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart){
//                                        json = json.substring(jsonStart, jsonEnd+1);
//                                    }
//
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(json);
//                                        String result = jsonObject.getString("message");
//
//                                        if (result.equalsIgnoreCase("update kode rahasia berhasil")) {
//                                            Toast.makeText(mContext, "Ubah Kode Rahasia Berhasil!", Toast.LENGTH_LONG).show();
//                                            SharedPreferences.Editor spEdit = sp.edit();
//                                            spEdit.putString("code", nCode);
//                                            spEdit.commit();
//                                            Nasabah.code = nCode;
//                                            startActivity(intent);
//                                        } else{
//                                            Toast.makeText(mContext, "Ubah Kode Rahasia Gagal: " + result, Toast.LENGTH_LONG).show();
//                                            startActivity(intent);
//                                        }
//                                    } catch (final JSONException e) {
//                                        Log.e(TAG, "Json parsing error change code: " + e.getMessage());
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getApplicationContext(),"Json parsing error change code: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                                }
//                            });
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    } else{
                        Toast.makeText(
                                this,
                                "Kode Rahasia harus terdiri 6 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(this, "Kode baru dan Re-type kode tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(this, "Kode sekarang salah!", Toast.LENGTH_LONG).show();
            }
        } else{
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
