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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransferCodeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Button btnTransferWithCode;
    private TextView txtNorekTransfer, txtNominalTransfer, txtKetTransfer;
    private EditText txtCodeTransfer;
    private String noRek, ket, nominal, rekTujuan;
    private Context mContext;
    private SharedPreferences sp;
    private String TAG = TransferCodeActivity.class.getSimpleName();
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_code);

        sp = getSharedPreferences("ibank", MODE_PRIVATE);
        mContext = this;

        btnTransferWithCode = findViewById(R.id.btnTransferWithCode);
        txtNorekTransfer = findViewById(R.id.txtNoRekTransfer);
        txtNominalTransfer = findViewById(R.id.txtNominalTransfer);
        txtKetTransfer = findViewById(R.id.txtKetTransfer);
        txtCodeTransfer = findViewById(R.id.txtCodeTransfer);

        NumberFormat formatter = new DecimalFormat("#,###");

        Intent intent = getIntent();
        rekTujuan = intent.getStringExtra("noRek");
        noRek = intent.getStringExtra("rekKirim");
        nominal = intent.getStringExtra("nominal");
        ket = intent.getStringExtra("ket");

        //set nominal, rekening number and info from data send by previous page (intent)
        txtNorekTransfer.setText(noRek);
        txtNominalTransfer.setText("Rp " + (formatter.format(Float.parseFloat(nominal))).toString() + ",-");
        txtKetTransfer.setText(ket);

        //setting toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.transfer_code_toolbar);
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

        btnTransferWithCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTransfer();
            }
        });
    }

    //send transfer data to server after encrypt secret code
    private void submitTransfer(){
        String code = txtCodeTransfer.getText().toString();

        String hashCode = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(code.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashCode = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashCode.length() < 32 ){
                hashCode = "0" + hashCode;
            }
        } catch (NoSuchAlgorithmException e) {
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing password failed");
            Log.e(TAG, "Hashing password failed: " + e.getMessage());
            e.printStackTrace();
        }

        final Intent intent = new Intent(this, TransferStatusActivity.class);

        String checkCode = "";
        for (Rekening rek:Nasabah.rekenings){
            if(rek.getRekeningNum().equals(noRek)){
                checkCode = rek.getSecretCode();
                break;
            }
        }

        if (hashCode.equals(checkCode)) {
            //final float temp = Nasabah.saldo - Float.parseFloat(nominal);
            //if (temp > 0) {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String url = HttpClientURL.urlTransfer;

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("norek_kirim", noRek);
                    jsonParams.put("norek_terima", rekTujuan);
                    jsonParams.put("kode_rhs", hashCode);
                    jsonParams.put("nominal", nominal);
                    jsonParams.put("ket", ket);
                } catch (JSONException e) {
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error create JSONObejct for post param");
                    Log.e(TAG, "Error create JSONObject for post param: " + e.getMessage());
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                final String finalHashCode = hashCode;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response from async okhttp call");
                        Log.e(TAG, "error getting response from async okhttp call");
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

                            String status = jsonObject.getString("status");
                            final String message = jsonObject.getString("message");

                            if (status.equalsIgnoreCase("berhasil")){
                                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Transfer success, [" +
                                        "Username = " + Nasabah.username +
                                        ", Nasabah id = " + Nasabah.id +
                                        ", Nomor Rekening Tujuan = " + noRek +
                                        ", Nominal = " + nominal +
                                        ", Keterangan = " + ket +
                                        ", Kode Rahasia = " + finalHashCode);
                                Log.i(TAG, "Transfer success, [" +
                                        "Username = " + Nasabah.username +
                                        ", Nasabah id = " + Nasabah.id +
                                        ", Nomor Rekening Tujuan = " + noRek +
                                        ", Nominal = " + nominal +
                                        ", Keterangan = " + ket +
                                        ", Kode Rahasia = " + finalHashCode);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                    }
                                });
                                //Nasabah.saldo = temp;
                                //SharedPreferences.Editor spEdit = sp.edit();
                                //spEdit.putFloat("saldo", Nasabah.saldo);
                                //spEdit.commit();

                                if (getNasabahData()){
                                    intent.putExtra("noRek", noRek);
                                    intent.putExtra("nominal", nominal);
                                    intent.putExtra("ket", ket);
                                    intent.putExtra("status", true);
                                    writeLogs();
                                    startActivity(intent);
                                }
                            } else{
                                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Transfer failed with message: " + message);
                                Log.e(TAG, "Tranfer failed with message: " + message);
                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                        }
                    }
                });
            /*} else {
                Log.e(TAG, "Balance is not enough");
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Balance is not enough");
                intent.putExtra("noRek", noRek);
                intent.putExtra("nominal", nominal);
                intent.putExtra("ket", ket);
                intent.putExtra("status", false);
                writeLogs();
                startActivity(intent);
            }*/
        } else{
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Secret code wrong");
            Log.e(TAG, "Secret code wrong");
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
        Intent intent = new Intent(this, MutationRekeningActivity.class);
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
        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + " Logout, remove session from app");
        Log.i(TAG, "Logout, remove session from app");
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putBoolean("isLogin", false);
        spEdit.putString("id", "");
        spEdit.putString("email", "");
        spEdit.putString("username", "");
        spEdit.putString("name", "");
        spEdit.putString("password", "");
        spEdit.putString("ktpNum", "");
        spEdit.putString("birthday", "");
        spEdit.putString("address", "");
        spEdit.putString("rekenings", "");
        spEdit.commit();

        writeLogs();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //request data to server using http GET for nasabah's data
    private boolean getNasabahData() throws JSONException {
        final OkHttpClient client = new OkHttpClient();

        //HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpClientURL.urlReadOne).newBuilder();
        //urlBuilder.addQueryParameter("unm", Nasabah.username);
        //urlBuilder.addQueryParameter("id", Nasabah.id);

        //String url = urlBuilder.build().toString();

        String url = HttpClientURL.urlReadOne + "/" + Nasabah.id;

        Log.e(TAG, "URL = " + url);

        final Request request = new Request.Builder()
                .url(url)
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
                try {
                    Log.i(TAG, "Get Nasabah data on progrees, [Username = " + Nasabah.username + "]");
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Get nasabah data on progress, [Username = " + Nasabah.username + "]");

                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject jsonRespon = jsonObject.getJSONObject("respon");
                    Nasabah.id = jsonRespon.getString("id_nasabah");
                    Nasabah.email = jsonRespon.getString("email");
                    Nasabah.username = jsonRespon.getString("username");
                    Nasabah.name = jsonRespon.getString("nama_lengkap");
                    Nasabah.password = jsonRespon.getString("password");
                    Nasabah.ktpNum = jsonRespon.getString("no_ktp");
                    Nasabah.birthday = jsonRespon.getString("tgl_lahir");
                    Nasabah.address = jsonRespon.getString("alamat");

                    String tempRekening = "";
                    Nasabah.rekenings = new ArrayList<Rekening>();

                    JSONArray jsonRekening = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonRekening.length(); i++){
                        String rekNum = jsonRekening.getJSONObject(i).getString("no_rek");
                        String code = jsonRekening.getJSONObject(i).getString("kode_rahasia");
                        String saldo = jsonRekening.getJSONObject(i).getString("jml_saldo");
                        String cabang = jsonRekening.getJSONObject(i).getString("kode_cabang");

                        float n_saldo = 0;
                        if (saldo != null && saldo != ""){
                            n_saldo = Float.parseFloat(saldo);
                        }

                        Nasabah.rekenings.add(new Rekening(rekNum, code, n_saldo, cabang));

                        tempRekening += "[rekNum = " + rekNum + ", code = " + code + ", saldo = " + n_saldo + ", cabang = " + cabang + "]";
                    }

                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Get Nasabah data success, data = [" +
                            "Nasabah id = " + Nasabah.id +
                            ", Email =  " + Nasabah.email +
                            ", Username = " + Nasabah.username +
                            ", Name = " + Nasabah.name +
                            ", Password = " + Nasabah.password +
                            ", KTP Num = " + Nasabah.ktpNum +
                            ", Birthday = " + Nasabah.birthday +
                            ", Address = " + Nasabah.address +
                            ", Rekenings = " + tempRekening +
                            "]"
                    );

                    Log.i(TAG, "Get Nasabah data success, data = [" +
                            "Nasabah id = " + Nasabah.id +
                            ", Email =  " + Nasabah.email +
                            ", Username = " + Nasabah.username +
                            ", Name = " + Nasabah.name +
                            ", Password = " + Nasabah.password +
                            ", KTP Num = " + Nasabah.ktpNum +
                            ", Birthday = " + Nasabah.birthday +
                            ", Address = " + Nasabah.address +
                            ", Rekenings = " + tempRekening +
                            "]"
                    );

                    SharedPreferences.Editor spEdit = sp.edit();
                    spEdit.putBoolean("isLogin", true);
                    spEdit.putString("id", Nasabah.id);
                    spEdit.putString("email", Nasabah.email);
                    spEdit.putString("username", Nasabah.username);
                    spEdit.putString("name", Nasabah.name);
                    spEdit.putString("password", Nasabah.password);
                    spEdit.putString("ktpNum", Nasabah.ktpNum);
                    spEdit.putString("birthday", Nasabah.birthday);
                    spEdit.putString("address", Nasabah.address);
                    spEdit.putString("rekenings", gson.toJson(Nasabah.rekenings));
                    spEdit.commit();
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                }
            }
        });

        if (Nasabah.id != null && Nasabah.id != "") {
            return true;
        }
        return false;
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
