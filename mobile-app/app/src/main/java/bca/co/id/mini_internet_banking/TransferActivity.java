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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TransferActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnSubmitTransfer;
    private LinearLayout inputCode;
    private EditText txtNorekTransfer, txtNominalTransfer, txtKetTransfer, txtCodeTransfer;

    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

        btnSubmitTransfer = findViewById(R.id.btnSumbitTransfer);
        inputCode = findViewById(R.id.inputCode);
        txtNorekTransfer = findViewById(R.id.txtNoRekTransfer);
        txtNominalTransfer = findViewById(R.id.txtNominalTransfer);
        txtKetTransfer = findViewById(R.id.txtKetTransfer);
        txtCodeTransfer = findViewById(R.id.txtCodeTransfer);

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

        txtNominalTransfer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCode.setVisibility(View.VISIBLE);
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
        String noRek = txtNorekTransfer.getText().toString();
        String nominal = txtNominalTransfer.getText().toString();
        String ket = txtKetTransfer.getText().toString();
        String code = txtCodeTransfer.getText().toString();

        Intent intent = new Intent(this, TransferStatusActivity.class);

        if (!noRek.equals("") && !nominal.equals("") && !ket.equals("") && !(code.equals(""))){
            if (code.equals(Nasabah.code)) {
                int temp = Nasabah.saldo - Integer.parseInt(nominal);
                if (temp > 0) {
                    Nasabah.saldo = temp;

                    intent.putExtra("noRek", noRek);
                    intent.putExtra("nominal", nominal);
                    intent.putExtra("ket", ket);
                    intent.putExtra("status", true);
                    startActivity(intent);
                } else {
                    intent.putExtra("status", false);
                    startActivity(intent);
                }
            } else{
                Toast.makeText(this, "Kode Rahasia salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_LONG).show();
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
