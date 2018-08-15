package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private String id, username, name, password, code, rekeningNum, saldo;

    private SharedPreferences sp;
    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        sp = getSharedPreferences("login_ibank", MODE_PRIVATE);

        if (sp.getBoolean("isLogin", false)){
            readLocalData();
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

                    id = jsonObject.getString("id");
                    username = jsonObject.getString("username");
                    password = jsonObject.getString("password");
                    name = jsonObject.getString("nama_lengkap");
                    code = jsonObject.getString("kode_rahasia");

                    Nasabah.id = id;
                    Nasabah.name = name;
                    Nasabah.username = username;
                    Nasabah.password = password;
                    Nasabah.code = code;

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

                    rekeningNum = jsonObject.getString("no rekening");
                    saldo = jsonObject.getString("saldo");
                    //String kode_cabang = jsonObject.getString("kode_cabang");

                    Nasabah.rekeningNum = rekeningNum;
                    Nasabah.saldo = Integer.parseInt(saldo);
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

    private void readLocalData(){
        Cursor cursor = db.rawQuery("SELECT id, name, username, password, rekeningNum, saldo, code FROM nasabah", null);
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            Nasabah.id = cursor.getString(0);
            Nasabah.name = cursor.getString(1);
            Nasabah.username = cursor.getString(2);
            Nasabah.password = cursor.getString(3);
            Nasabah.rekeningNum = cursor.getString(4);
            Nasabah.saldo = Double.parseDouble(cursor.getString(5));
            Nasabah.code = cursor.getString(6);
        }
    }
}
