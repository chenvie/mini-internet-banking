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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2/mini-internet-banking/API/transaksi/read-history.php").newBuilder();
        urlBuilder.addQueryParameter("id", Nasabah.id);
        urlBuilder.addQueryParameter("tgl_awal", from);
        urlBuilder.addQueryParameter("tgl_akhir", to);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error in getting response get request with query string okhttp");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string().toString();

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonRecords = jsonObject.getJSONArray("records");

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
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error login: " + e.getMessage());
                }
            }
        });

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

//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams rp = new RequestParams();
//        rp.add("id", Nasabah.id);
//        rp.add("tgl_awal", from);
//        rp.add("tgl_akhir", to);
//
//        client.get(this, "http://10.0.2.2/mini-internet-banking/API/transaksi/read-history.php", rp, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                setContentView(R.layout.activity_history_detail);
//                txtHistoryRange = findViewById(R.id.txtHistoryRange);
//                rcyHistory = findViewById(R.id.rcyHistory);
//
//                txtHistoryRange.setText(from + " - " + to);
//
//                List<Transaction> listTrans = new ArrayList<Transaction>();
//                historyAdapter = new HistoryAdapter(listTrans, mContext);
//
//                RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext);
//                rcyHistory.setLayoutManager(lm);
//                rcyHistory.setItemAnimator(new DefaultItemAnimator());
//                rcyHistory.setAdapter(historyAdapter);
//
//                String json = new String(responseBody);
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    JSONArray jsonRecords = jsonObject.getJSONArray("records");
//
//                    for (int i = 0; i < jsonRecords.length(); i++){
//                        String tgl_trans = jsonRecords.getJSONObject(i).getString("tgl_trans");
//                        String tujuan = jsonRecords.getJSONObject(i).getString("tujuan");
//                        String ket = jsonRecords.getJSONObject(i).getString("keterangan");
//                        String nominal = jsonRecords.getJSONObject(i).getString("nominal");
//                        String status = jsonRecords.getJSONObject(i).getString("status");
//
//                        listTrans.add(new Transaction(tgl_trans, tujuan, ket, Float.parseFloat(nominal), status));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                historyAdapter.notifyDataSetChanged();
//
//                Toolbar toolbar = findViewById(R.id.history_detail_toolbar);
//                setSupportActionBar(toolbar);
//                ActionBar actionbar = getSupportActionBar();
//                actionbar.setDisplayHomeAsUpEnabled(true);
//                actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
//
//                mDrawerLayout = findViewById(R.id.drawer_layout);
//
//                NavigationView navigationView = findViewById(R.id.nav_view);
//                navigationView.setNavigationItemSelectedListener(
//                        new NavigationView.OnNavigationItemSelectedListener() {
//                            @Override
//                            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                                int id = menuItem.getItemId();
//                                if (id == R.id.nav_home){
//                                    loadHomeView();
//                                } else if (id == R.id.nav_balance) {
//                                    loadBalanceInfoView();
//                                }else if (id == R.id.nav_mutation){
//                                    loadMutationView();
//                                } else if (id == R.id.nav_transfer) {
//                                    loadTransferView();
//                                } else if (id == R.id.nav_buying){
//                                    loadBuyingView();
//                                } else if (id == R.id.nav_history){
//                                    loadHistoryView();
//                                } else if (id == R.id.nav_setting){
//                                    loadSettingView();
//                                } else{
//                                    loadLoginView();
//                                }
//                                return true;
//                            }
//                        });
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//            }
//        });
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
