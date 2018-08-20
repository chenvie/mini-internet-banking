package bca.co.id.mini_internet_banking;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TransferStatusActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView txtNorekStatus, txtNominalStatus, txtKetStatus, txtTransStatus, txtFailedTrans;

    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_status);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);

        txtNorekStatus = findViewById(R.id.txtNoRekStatus);
        txtNominalStatus = findViewById(R.id.txtNominalStatus);
        txtKetStatus = findViewById(R.id.txtKetStatus);
        txtTransStatus = findViewById(R.id.txtTransStatus);
        txtFailedTrans = findViewById(R.id.txtTransFailed);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("status", true)){
            txtTransStatus.setText("Transaksi berhasil!");
            txtFailedTrans.setVisibility(View.INVISIBLE);
        } else{
            txtTransStatus.setText("Transaksi gagal!");
            txtFailedTrans.setVisibility(View.VISIBLE);
        }

        NumberFormat formatter = new DecimalFormat("#,###");

        txtNorekStatus.setText(intent.getStringExtra("noRek"));
        if (intent.getStringExtra("nominal") != null || intent.getStringExtra("nominal") != ""){
            txtNominalStatus.setText("Rp " + (formatter.format(Float.parseFloat(intent.getStringExtra("nominal")))).toString() + ",-");
        }
        txtKetStatus.setText(intent.getStringExtra("ket"));

        Toolbar toolbar = findViewById(R.id.transfer_status_toolbar);
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
