package bca.co.id.mini_internet_banking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private LinearLayout menu_balance, menu_mutation, menu_transfer,
                         menu_buying, menu_history, menu_setting;
    private TextView txtNasabahName;

    private SharedPreferences sp;
    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        saveDataToLocal();

        menu_balance = findViewById(R.id.menu_balance);
        menu_mutation = findViewById(R.id.menu_mutation);
        menu_transfer = findViewById(R.id.menu_transfer);
        menu_buying = findViewById(R.id.menu_buying);
        menu_history = findViewById(R.id.menu_history);
        menu_setting = findViewById(R.id.menu_setting);
        txtNasabahName = findViewById(R.id.txtNasabahName);

        txtNasabahName.setText(Nasabah.name);

        menu_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBalanceInfoView();
            }
        });

        menu_mutation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMutationView();
            }
        });

        menu_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTransferView();
            }
        });

        menu_buying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBuyingView();
            }
        });

        menu_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHistoryView();
            }
        });

        menu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettingView();
            }
        });

        Toolbar toolbar = findViewById(R.id.home_toolbar);
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

    private void saveDataToLocal(){
        if (Nasabah.id != null && Nasabah.id != "") {
            SQLiteStatement stmt = db.compileStatement(
                    "INSERT INTO nasabah(id_nasabah, name, username, password, rekeningNum, saldo, code) VALUES(?, ?, ?, ?, ?, ?, ?)");
            stmt.bindString(1, Nasabah.id);
            stmt.bindString(2, Nasabah.name);
            stmt.bindString(3, Nasabah.username);
            stmt.bindString(4, Nasabah.password);
            stmt.bindString(5, Nasabah.rekeningNum);
            stmt.bindDouble(6, Nasabah.saldo);
            stmt.bindString(7, Nasabah.code);
            stmt.execute();
            /*db.execSQL(
                    "INSERT INTO nasabah(id, name, username, password, rekeningNum, saldo, code) VALUES ('" +
                            Nasabah.id + "', '" +
                            Nasabah.name + "', '" +
                            Nasabah.username + "', '" +
                            Nasabah.password + "', '" +
                            Nasabah.rekeningNum + "', '" +
                            Nasabah.saldo + "', '" +
                            Nasabah.code + "')"
            );*/
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
