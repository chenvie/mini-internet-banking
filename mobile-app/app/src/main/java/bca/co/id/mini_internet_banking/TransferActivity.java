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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class TransferActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnSubmitTransfer;
    private LinearLayout inputCode;
    private EditText inputNorekTransfer, inputNominalTransfer, inputKetTransfer;
    private Context mContext;
    private SharedPreferences sp;
    private String TAG = TransferActivity.class.getSimpleName();
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

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
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String url = HttpClientURL.urlCheckRekNum;

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("no_rek_tujuan", noRek);
                jsonParams.put("id_nasabah", Nasabah.id);
                jsonParams.put("nominal", nominal);
                jsonParams.put("keterangan", ket);
            } catch (JSONException e) {
                Log.e(TAG, "Error create JSONObject for post param: " + e.getMessage());
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error create JSONObejct for post param: " + e.getMessage());
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
                    Log.e(TAG, "error in getting response from async okhttp call");
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response from async okhttp call");
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
                        String check = jsonObject.getString("check");
                        final String message = jsonObject.getString("message");

                        if (check.equalsIgnoreCase("true")){
                            Log.i(TAG, "checking receiver rekening num success, sending receiver rekNum, nominal, info, and nasabah id as parameter");
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Checking receiver rekening num success, sending receiver rekNum, nominal, info, and nasabah id as parameter");
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Receiver RekNum = " + noRek);
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Nominal = " + nominal);
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Info = " + ket);
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Nasabah id = " + Nasabah.id);
                            intent.putExtra("noRek", message);
                            intent.putExtra("nominal", nominal);
                            intent.putExtra("ket", ket);
                            writeLogs();
                            startActivity(intent);
                        } else{
                            Log.e(TAG, "Checking receiver rekening num failed: " + message);
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Checking receiver num failed: " + message);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else{
            Log.e(TAG, "Secret code wrong");
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Secret code wrong");
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
        Log.i(TAG, "Logout, remove session from app");
        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Logout, remove session from app");
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
