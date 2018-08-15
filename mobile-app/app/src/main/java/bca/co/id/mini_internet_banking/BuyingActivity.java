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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BuyingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView txtNoHpBuying, txtCodeBuying;
    private Spinner txtProviderBuying, txtNominalBuying;
    private LinearLayout inputCodeBuying;
    private Button btnSubmitBuying;
    private SharedPreferences sp;
    private static final String[]provider = {"Telkomsel", "Indosat", "XL", "Smartfren"};
    private static final String[]nominal = {"50000", "100000", "150000"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buying);

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

        txtProviderBuying = findViewById(R.id.txtProviderBuying);
        txtNominalBuying = findViewById(R.id.txtNominalBuying);
        txtNoHpBuying = findViewById(R.id.txtNoHpBuying);
        txtCodeBuying = findViewById(R.id.txtCodeBuying);
        btnSubmitBuying = findViewById(R.id.btnSubmitBuying);
        inputCodeBuying = findViewById(R.id.inputCodeBuying);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(BuyingActivity.this,
                android.R.layout.simple_spinner_item,provider);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtProviderBuying.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(BuyingActivity.this,
                android.R.layout.simple_spinner_item,nominal);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtNominalBuying.setAdapter(adapter2);

        Toolbar toolbar = findViewById(R.id.buying_toolbar);
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

        txtNoHpBuying.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCodeBuying.setVisibility(View.VISIBLE);
            }
        });

        btnSubmitBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBuying();
            }
        });
    }

    private void submitBuying(){
        String noHp = txtNoHpBuying.getText().toString();
        String provider = txtProviderBuying.getSelectedItem().toString();
        String nominal = txtNominalBuying.getSelectedItem().toString();
        String code = txtCodeBuying.getText().toString();

        double temp = Nasabah.saldo - Double.parseDouble(nominal);
        Intent intent = new Intent(this, BuyingStatusActivity.class);

        if (!noHp.equals("")){
            if (code.equals(Nasabah.code)) {
                if (temp > 0) {
                    intent.putExtra("noHp", noHp);
                    intent.putExtra("pulsa", nominal);
                    intent.putExtra("provider", provider);
                    intent.putExtra("status", true);
                    Nasabah.saldo = temp;
                    startActivity(intent);
                } else {
                    intent.putExtra("status", false);
                    startActivity(intent);
                }
            } else{
                Toast.makeText(this, "Kode Rahasia salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
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
