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
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MutationActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private MutationAdapter mutationAdapter;
    private RecyclerView rcyMutation;
    private Context mContext;
    private SharedPreferences sp;
    private TextView txtMutationDate, txtNorekMutasi;
    private final String TAG = MutationActivity.class.getSimpleName();
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutation);
        rcyMutation = findViewById(R.id.rcyMutation);
        txtMutationDate = findViewById(R.id.txtMutationDate);
        txtNorekMutasi = findViewById(R.id.txtNorekMutasi);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        final OkHttpClient client = new OkHttpClient();

        final SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpClientURL.urlReadMutation).newBuilder();
        urlBuilder.addQueryParameter("id", Nasabah.id);
        urlBuilder.addQueryParameter("tgl", s.format(new Date()));

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        //get mutation data to server using http GET, sending nasabah id as parameter
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
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonTanggal = jsonObject.getJSONArray("tanggal");
                    String no_rek_pengirim = jsonTanggal.getJSONObject(0).getString("no_rek_pengirim");
                    String tgl_awal = jsonTanggal.getJSONObject(0).getString("tgl_awal");
                    String tgl_akhir = jsonTanggal.getJSONObject(0).getString("tgl_akhir");
                    txtMutationDate.setText(tgl_awal + " - " + tgl_akhir);
                    txtNorekMutasi.setText(no_rek_pengirim);

                    JSONArray jsonRecords = jsonObject.getJSONArray("records");

                    List<Transaction> listTrans = new ArrayList<Transaction>();
                    mutationAdapter = new MutationAdapter(listTrans, mContext);

                    RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext);
                    rcyMutation.setLayoutManager(lm);
                    rcyMutation.setItemAnimator(new DefaultItemAnimator());
                    rcyMutation.setAdapter(mutationAdapter);

                    mutationAdapter.notifyDataSetChanged();

                    for (int i = 0; i < jsonRecords.length(); i++) {
                        String tgl_trans = jsonRecords.getJSONObject(i).getString("tgl_trans");
                        String tujuan = jsonRecords.getJSONObject(i).getString("tujuan");
                        String info = jsonRecords.getJSONObject(i).getString("jenis") + jsonRecords.getJSONObject(i).getString("keterangan");
                        String nominal = jsonRecords.getJSONObject(i).getString("nominal");

                        listTrans.add(new Transaction(tgl_trans, tujuan, info, Float.parseFloat(nominal)));
                    }

                    mutationAdapter.notifyDataSetChanged();
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Getting Mutation Data Success, sending nasabag id as parameter");
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Nasabah id = " + Nasabah.id);
                    Log.i(TAG, "Getting Mutation Data Success, sending nasabah id as parameter");
                } catch (JSONException e) {
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.mutation_toolbar);
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
