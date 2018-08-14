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

import cz.msebera.android.httpclient.Header;

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

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);
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
        String oPass = txtOldPass.getText().toString();
        final String nPass = txtNewPass.getText().toString();
        String rPass = txtRePass.getText().toString();

        final Intent intent = new Intent(this, HomeActivity.class);

        if (!oPass.equals("") && !nPass.equals("") && !rPass.equals("")){
            if (oPass.equals(Nasabah.password)){
                if (nPass.equals(rPass)){
                    if(PasswordStrength.calculateStrength(nPass).getValue() > PasswordStrength.MEDIUM.getValue()){
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams rp = new RequestParams();
                        rp.add("id", Nasabah.id);
                        rp.add("password", nPass);

                        client.post(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/update_password.php", rp, new AsyncHttpResponseHandler() {
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
                                    String result = jsonObject.getString("result");

                                    if (result.equalsIgnoreCase("true")){
                                        Toast.makeText(mContext, "Ubah Password Berhasil!", Toast.LENGTH_LONG).show();
                                        Nasabah.password = nPass;
                                        startActivity(intent);
                                    } else{
                                        Toast.makeText(mContext, "Ubah Password Gagal!", Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
                                    Log.e(TAG, "Json parsing error change password: " + e.getMessage());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Json parsing error change password: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    } else{
                        Toast.makeText(
                                this,
                                "Password harus terdiri min 8 karakter dan alfanumerik!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(this, "Password baru dan Re-type password tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(this, "Password lama salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Semua kolom harus terisi!", Toast.LENGTH_LONG).show();
        }
    }

    private void changeCode(){
        String oCOde = txtOldCode.getText().toString();
        final String nCode = txtNewCode.getText().toString();
        String rCode = txtReCode.getText().toString();

        final Intent intent = new Intent(this, HomeActivity.class);

        if (!oCOde.equals("") && !nCode.equals("") && !rCode.equals("")){
            if (oCOde.equals(Nasabah.code)){
                if (nCode.equals(rCode)){
                    if(CodeStrength.calculateStrength(nCode).getValue() > CodeStrength.MEDIUM.getValue()) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams rp = new RequestParams();
                        rp.add("id", Nasabah.id);
                        rp.add("kode_rahasia", nCode);

                        client.post(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/update_kode_rahasia.php", rp, new AsyncHttpResponseHandler() {
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
                                    String result = jsonObject.getString("result");

                                    if (result.equalsIgnoreCase("true")) {
                                        Toast.makeText(mContext, "Ubah Kode Rahasia Berhasil!", Toast.LENGTH_LONG).show();
                                        Nasabah.code = nCode;
                                        startActivity(intent);
                                    } else{
                                        Toast.makeText(mContext, "Ubah Kode Rahasia Gagal!", Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                    }
                                } catch (final JSONException e) {
                                    Log.e(TAG, "Json parsing error change code: " + e.getMessage());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"Json parsing error change code: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    } else{
                        Toast.makeText(
                                this,
                                "Kode Rahasia harus terdiri min 6 karakter dan alfanumerik!",
                                Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(this, "Kode baru dan Re-type kode tidak sama!", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(this, "Kode lama salah!", Toast.LENGTH_LONG).show();
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
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putBoolean("isLogin", false);
        spEdit.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
