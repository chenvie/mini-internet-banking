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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MutationActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private MutationAdapter mutationAdapter;
    private RecyclerView rcyMutation;
    private Context mContext;
    private SharedPreferences sp;
    private TextView txtMutationDate, txtNorekMutasi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("id", Nasabah.id);

        client.get(this, "http://10.0.2.2/mini-internet-banking/API/transaksi/read-mutasi.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setContentView(R.layout.activity_mutation);
                rcyMutation = findViewById(R.id.rcyMutation);
                txtMutationDate = findViewById(R.id.txtMutationDate);
                txtNorekMutasi = findViewById(R.id.txtNorekMutasi);

                String json = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonTanggal = jsonObject.getJSONArray("tanggal");
                    JSONArray jsonRecords = jsonObject.getJSONArray("records");

                    String tgl_awal = jsonTanggal.getJSONObject(0).getString("tgl_awal");
                    String tgl_akhir = jsonTanggal.getJSONObject(0).getString("tgl_akhir");
                    String norek = jsonTanggal.getJSONObject(0).getString("no_rek_pengirim");

                    Log.e(MutationActivity.class.getSimpleName(), "Tanggal awal = " + tgl_awal);
                    txtMutationDate.setText(tgl_awal + " - " + tgl_akhir);
                    txtNorekMutasi.setText(norek);

                    /*String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                    if (tgl_awal != null && tgl_awal != "" && tgl_akhir != null && tgl_akhir != "") {
                        String[] fDate = {};
                        fDate[0] = tgl_awal.substring(8, 9);
                        fDate[1] = months[Integer.parseInt(tgl_awal.substring(5, 6)) - 1];
                        fDate[2] = tgl_awal.substring(0, 3);

                        String[] lDate = {};
                        lDate[0] = tgl_akhir.substring(8, 9);
                        lDate[1] = months[Integer.parseInt(tgl_akhir.substring(5, 6)) - 1];
                        lDate[2] = tgl_akhir.substring(0, 3);

                        String mutationDate = fDate[0] + fDate[1] + fDate[2] + " - " + lDate[0] + lDate[1] + lDate[2];

                        txtMutationDate.setText(mutationDate);
                        txtNorekMutasi.setText(norek);
                    }*/

                    List<Transaction> listTrans = new ArrayList<Transaction>();
                    mutationAdapter = new MutationAdapter(listTrans, mContext);

                    RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext);
                    rcyMutation.setLayoutManager(lm);
                    rcyMutation.setItemAnimator(new DefaultItemAnimator());
                    rcyMutation.setAdapter(mutationAdapter);

                    for (int i = 0; i < jsonRecords.length(); i++) {
                        String tgl_trans = jsonRecords.getJSONObject(i).getString("tgl_trans");
                        String tujuan = jsonRecords.getJSONObject(i).getString("tujuan");
                        String info = jsonRecords.getJSONObject(i).getString("jenis") + jsonRecords.getJSONObject(i).getString("keterangan");
                        String nominal = jsonRecords.getJSONObject(i).getString("nominal");

                        listTrans.add(new Transaction(tgl_trans, tujuan, info, Float.parseFloat(nominal)));
                    }

                    mutationAdapter.notifyDataSetChanged();

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

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
