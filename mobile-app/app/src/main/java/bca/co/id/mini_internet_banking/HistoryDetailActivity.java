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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoryDetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private HistoryAdapter historyAdapter;
    private RecyclerView rcyHistory;
    private TextView txtHistoryRange;
    private Context mContext;
    private SharedPreferences sp;
    private String TAG = HistoryDetailActivity.class.getSimpleName();
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        txtHistoryRange = findViewById(R.id.txtHistoryRange);
        rcyHistory = findViewById(R.id.rcyHistory);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        Intent intent = getIntent();
        final String from = intent.getStringExtra("dateFrom");
        final String to = intent.getStringExtra("dateTo");

        txtHistoryRange.setText(from + " - " + to);

        final OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpClientURL.urlReadHistory).newBuilder();
        urlBuilder.addQueryParameter("id", Nasabah.id);
        urlBuilder.addQueryParameter("tgl_awal", from);
        urlBuilder.addQueryParameter("tgl_akhir", to);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        //send http GET data to server, and binding data to adapter
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response from async okhttp call");
                Log.e(TAG, "Error in getting response from async okhttp call");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string().toString();

                try {
                    JSONArray jsonRecords = new JSONArray(responseBody);

                    List<Transaction> listTrans = new ArrayList<Transaction>();
                    historyAdapter = new HistoryAdapter(listTrans, mContext);

                    RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext);
                    rcyHistory.setLayoutManager(lm);
                    rcyHistory.setItemAnimator(new DefaultItemAnimator());
                    rcyHistory.setAdapter(historyAdapter);

                    for (int i = 0; i < jsonRecords.length(); i++){
                        String tgl_trans = jsonRecords.getJSONObject(i).getString("tgl_trans");
                        String tujuan = jsonRecords.getJSONObject(i).getString("tujuan");
                        String ket = jsonRecords.getJSONObject(i).getString("keterangan");
                        String nominal = jsonRecords.getJSONObject(i).getString("nominal");
                        String status = jsonRecords.getJSONObject(i).getString("status");

                        listTrans.add(new Transaction(tgl_trans, tujuan, ket, Float.parseFloat(nominal), status));
                    }
                    historyAdapter.notifyDataSetChanged();
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Getting history data success, [" +
                                "Nasabah id = " + Nasabah.id +
                                ", Date from = " + from +
                                ", Date to = " + to + "]");
                    Log.i(TAG, "Getting history data success, [" +
                                "Nasabah id = " + Nasabah.id +
                                ", Date from = " + from +
                                ", Date to = " + to + "]");
                } catch (JSONException e) {
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }
        });

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.history_detail_toolbar);
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
        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Logout, remove session from app");
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
