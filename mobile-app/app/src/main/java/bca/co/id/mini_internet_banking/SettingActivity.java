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
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private EditText txtOldPass, txtNewPass, txtRePass, txtOldCode, txtNewCode, txtReCode;
    private Button btnChangePass, btnChangeCode;

    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

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
        String nPass = txtNewPass.getText().toString();
        String rPass = txtRePass.getText().toString();

        Intent intent = new Intent(this, HomeActivity.class);

        if (!oPass.equals("") && !nPass.equals("") && !rPass.equals("")){
            if (oPass.equals(Nasabah.password)){
                if (nPass.equals(rPass)){
                    if(PasswordStrength.calculateStrength(nPass).getValue() > PasswordStrength.MEDIUM.getValue()){
                        Toast.makeText(this, "Ubah Password Berhasil!", Toast.LENGTH_LONG).show();
                        Nasabah.password = nPass;
                        startActivity(intent);
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
                Toast.makeText(this, "Password sekarang salah!", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Semua kolom harus terisi!", Toast.LENGTH_LONG).show();
        }
    }

    private void changeCode(){
        String oCOde = txtOldCode.getText().toString();
        String nCode = txtNewCode.getText().toString();
        String rCode = txtReCode.getText().toString();

        Intent intent = new Intent(this, HomeActivity.class);

        if (!oCOde.equals("") && !nCode.equals("") && !rCode.equals("")){
            if (oCOde.equals(Nasabah.code)){
                if (nCode.equals(rCode)){
                    if(CodeStrength.calculateStrength(nCode).getValue() > CodeStrength.MEDIUM.getValue()) {
                        Toast.makeText(this, "Ubah Kode Rahasia Berhasil!", Toast.LENGTH_LONG).show();
                        Nasabah.code = nCode;
                        startActivity(intent);
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
                Toast.makeText(this, "Kode lama sekarang!", Toast.LENGTH_LONG).show();
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
