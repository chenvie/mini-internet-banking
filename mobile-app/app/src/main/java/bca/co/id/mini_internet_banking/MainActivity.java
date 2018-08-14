package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private EditText txtUname_login, txtPwd_login;
    private Button btnLogin;
    private TextView txtNewRekening;
    private Context mContext;
    private String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

        if (sp.getBoolean("isLogin", false)){ //param : (key, default value)
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else{
            setContentView(R.layout.activity_main);

            txtUname_login = findViewById(R.id.txtUname_login);
            txtPwd_login = findViewById(R.id.txtPwd_login);
            btnLogin = findViewById(R.id.btnLogin);
            txtNewRekening = findViewById(R.id.txtNewRekening);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });

            txtNewRekening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNewRekeningView();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void login(){
        String username = txtUname_login.getText().toString();
        String password = txtPwd_login.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("username", txtUname_login.getText().toString());
        rp.add("password", txtPwd_login.getText().toString());
        client.post(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/login.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                int jsonStart = json.indexOf("{");
                int jsonEnd = json.indexOf("}");

                if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart){
                    json = json.substring(jsonStart, jsonEnd+1);
                }

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String login = jsonObject.getString("login");
                    if (login.equalsIgnoreCase("true")){
                        SharedPreferences.Editor spEdit = sp.edit();
                        spEdit.putBoolean("isLogin", true);
                        spEdit.commit();

                        //Toast.makeText(mContext, "id = " + Nasabah.id, Toast.LENGTH_LONG).show();

                        try {
                            if (getNasabahData()){
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Get Nasabah data error", Toast.LENGTH_LONG).show();
                        }
                    } else{
                        Toast.makeText(mContext, "Username atau password Salah!", Toast.LENGTH_LONG).show();
                    }
                } catch(final JSONException e){
                    Log.e(TAG, "Json parsing error login: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void loadNewRekeningView(){
        Intent intent = new Intent(this, NewRekeningActivity.class);
        startActivity(intent);
    }

    private boolean getNasabahData() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("id", "4");
        client.get(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/read-one.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                try{
                    JSONObject jsonObject = new JSONObject(json);

                    String id = jsonObject.getString("id");
                    //String email = jsonObject.getString("email");
                    String username = jsonObject.getString("username");
                    String password = jsonObject.getString("password");
                    String name = jsonObject.getString("nama_lengkap");
                    //String ktp = jsonObject.getString("no_ktp");
                    //String birthday = jsonObject.getString("tgl_lahir");
                    //String address = jsonObject.getString("alamat");
                    String code = jsonObject.getString("kode_rahasia");
                    //String created = jsonObject.getString("created");

                    Nasabah.id = id;
                    Nasabah.name = name;
                    Nasabah.username = username;
                    //Nasabah.email = email;
                    Nasabah.password = password;
                    //Nasabah.ktpNum = ktp;
                    //Nasabah.birthday = birthday;
                    //Nasabah.address = address;
                    Nasabah.code = code;
                    //Nasabah.created = created;

                    //Toast.makeText(mContext, "get data success", Toast.LENGTH_LONG).show();
                } catch(final JSONException e){
                    Log.e(TAG, "Json parsing error get data: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error get data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/read-one-saldo.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    String no_rek = jsonObject.getString("no rekening");
                    String jml_saldo = jsonObject.getString("saldo");
                    //String kode_cabang = jsonObject.getString("kode_cabang");

                    Nasabah.rekeningNum = no_rek;
                    Nasabah.saldo = Integer.parseInt(jml_saldo);
                    //Nasabah.kode_cabang = kode_cabang;
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error get saldo: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error get saldo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return true;
    }
}
