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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText txtUname_login, txtPwd_login;
    private Button btnLogin;
    private TextView txtNewRekening;
    private Context mContext;
    private String TAG = MainActivity.class.getSimpleName();
    private String id, username, name, password, code, birthday, saldo;
    private List<String> rekeningNum = new ArrayList<String>();
    private SharedPreferences sp;
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        sp = getSharedPreferences("ibank", MODE_PRIVATE);

        //get session data from SharedPreferences
        if (sp.getBoolean("isLogin", false)){
            try {
                Nasabah.id = sp.getString("id", "");
                Nasabah.email = sp.getString("email", "");
                Nasabah.username = sp.getString("username", "");
                Nasabah.name = sp.getString("name", "");
                Nasabah.password = sp.getString("password", "");
                Nasabah.ktpNum = sp.getString("ktpNum", "");
                Nasabah.birthday = sp.getString("birthday", "");
                Nasabah.address = sp.getString("address", "");

                Type type = new TypeToken<List<Rekening>>(){}.getType();
                Nasabah.rekenings = gson.fromJson(sp.getString("rekenings", ""), type);
                Log.i(TAG, "Get Nasabah data from session");
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Get Nasabah data from session");

                if (getNasabahData()){
                    writeLogs();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        Log.e(TAG, "Button Login Clicked");
        final String username = txtUname_login.getText().toString();
        final String password = txtPwd_login.getText().toString();

        //encrypt password using MD5 algorithm
        String hashPassword = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashPassword = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashPassword.length() < 32 ){
                hashPassword = "0" + hashPassword;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to hashing password: " + e.getMessage());
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Failed to hashing password: " + e.getMessage());
        }

        //send username and password to server using http POST for login
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        String url = HttpClientURL.urlLogin;

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", hashPassword);
        } catch(JSONException e){
            // TODO Auto-generated catch block
            Log.e(TAG, "Failed to create JSONObject for post param: " + e.getMessage());
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Failed to create JSONObject for post param: " + e.getMessage());
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final String finalHashPassword = hashPassword;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "error in getting response using async okhttp call");
                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response using async okhttp call");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string().toString();
                if (!response.isSuccessful()){
                    throw new IOException("Error response: " + response);
                }
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String login = jsonObject.getString("status");
                    Log.e(TAG, "status = " + login);
                    if (login.equalsIgnoreCase("1")){
                        Nasabah.username = username;
                        Nasabah.id = jsonObject.getString("id_nasabah");
                        Log.e(TAG, "ID = " + Nasabah.id);
                        Log.i(TAG, "Login Success, [" + "Username = " + username + ", password = " + finalHashPassword + "]");
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Login success, [" + "Username = " + username + ", password = " + finalHashPassword + "]");

                        //call function get nasabah data
                        try {
                            if (getNasabahData()){
                                Log.i(TAG, "Get Nasabah Data Success");
                                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Get Nasabah Data Success");
                                writeLogs();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Get Nasabah data error: " + e.getMessage());
                            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Get Nasabah data error: " + e.getMessage());
                            Toast.makeText(mContext, "Get Nasabah data error", Toast.LENGTH_LONG).show();
                        }
                    } else{
                        Log.e(TAG, "Username or password wrong");
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Username or password wrong");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Username atau password salah!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Json parsing error: " + e.getMessage());
                }
            }
        });
    }

    private void loadNewRekeningView(){
        writeLogs();
        Intent intent = new Intent(this, NewNasabahActivity.class);
        startActivity(intent);
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

    //send data to server
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
