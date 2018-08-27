package bca.co.id.mini_internet_banking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
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
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private EditText txtUname_login, txtPwd_login;
    private Button btnLogin;
    private TextView txtNewRekening;
    private Context mContext;
    private String TAG = MainActivity.class.getSimpleName();
    private String id, username, name, password, code, birthday, rekeningNum, saldo;
    private SharedPreferences sp;

    //static private final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LOG.info("Magang Internet Banking");

        mContext = this;

        sp = getSharedPreferences("ibank", MODE_PRIVATE);

        if (sp.getBoolean("isLogin", false)){
            Nasabah.id = sp.getString("id", "");
            Nasabah.name = sp.getString("name", "");
            Nasabah.username = sp.getString("username", "");
            Nasabah.password = sp.getString("password", "");
            Nasabah.code = sp.getString("code", "");
            Nasabah.birthday = sp.getString("birthday", "");
            Nasabah.rekeningNum = sp.getString("rekeningNum", "");
            Nasabah.saldo = sp.getFloat("saldo", 0);
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
        final String username = txtUname_login.getText().toString();
        final String password = txtPwd_login.getText().toString();

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
        }

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        final OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2/mini-internet-banking/API/nasabah/login.php";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", hashPassword);
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "error in getting response using async okhttp call");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string().toString();
                if (!response.isSuccessful()){
                    throw new IOException("Error response: " + response);
                }
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String login = jsonObject.getString("login");
                    if (login.equalsIgnoreCase("true")){
                        Nasabah.username = username;

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "LOGIN GAGAL !", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error login: " + e.getMessage());
                }
            }
        });

//        AsyncHttpClient client = new AsyncHttpClient();
//
//        JSONObject jsonParams = new JSONObject();
//        try {
//            jsonParams.put("username", username);
//            jsonParams.put("password", hashPassword);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        /*RequestParams rp = new RequestParams();
//        rp.add("username", username);
//        rp.add("password", password);*/
//
//        try {
//            StringEntity entity = new StringEntity(jsonParams.toString());
//
//            client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/nasabah/login.php", entity, "application/json", new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    String json = new String(responseBody);
//                    int jsonStart = json.indexOf("{");
//                    int jsonEnd = json.indexOf("}");
//
//                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart){
//                        json = json.substring(jsonStart, jsonEnd+1);
//                    }
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(json);
//                        String login = jsonObject.getString("login");
//                        if (login.equalsIgnoreCase("true")){
//                            Nasabah.username = username;
//
//                            try {
//                                if (getNasabahData()){
//                                    Intent intent = new Intent(mContext, HomeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Toast.makeText(mContext, "Get Nasabah data error", Toast.LENGTH_LONG).show();
//                            }
//                        } else{
//                            Toast.makeText(mContext, "Username atau password Salah!", Toast.LENGTH_LONG).show();
//                        }
//                    } catch(final JSONException e){
//                        Log.e(TAG, "Json parsing error login: " + e.getMessage());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(),"Json parsing error login: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                }
//            });
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    private void loadNewRekeningView(){
        Intent intent = new Intent(this, NewRekeningActivity.class);
        startActivity(intent);
    }

    private boolean getNasabahData() throws JSONException {
        final OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2/mini-internet-banking/API/nasabah/read-one.php").newBuilder();
        urlBuilder.addQueryParameter("unm", Nasabah.username);

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "OkHttp Call Failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string().toString();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    id = jsonObject.getString("id_nasabah");
                    username = jsonObject.getString("username");
                    password = jsonObject.getString("password");
                    name = jsonObject.getString("nama_lengkap");
                    code = jsonObject.getString("kode_rahasia");
                    birthday = jsonObject.getString("tgl_lahir");
                    rekeningNum = jsonObject.getString("no_rek");
                    saldo = jsonObject.getString("jml_saldo");

                    Nasabah.id = id;
                    Nasabah.name = name;
                    Nasabah.username = username;
                    Nasabah.password = password;
                    Nasabah.code = code;
                    Nasabah.birthday = birthday;
                    Nasabah.rekeningNum = rekeningNum;
                    if (saldo != null && saldo != "") {
                        Nasabah.saldo = Float.parseFloat(saldo);
                    }else{
                        Nasabah.saldo = 0;
                    }

                    SharedPreferences.Editor spEdit = sp.edit();
                    spEdit.putBoolean("isLogin", true);
                    spEdit.putString("id", Nasabah.id);
                    spEdit.putString("name", Nasabah.name);
                    spEdit.putString("username", Nasabah.username);
                    spEdit.putString("password", Nasabah.password);
                    spEdit.putString("code", Nasabah.code);
                    spEdit.putString("birthday", Nasabah.birthday);
                    spEdit.putString("rekeningNum", Nasabah.rekeningNum);
                    spEdit.putFloat("saldo", Nasabah.saldo);
                    spEdit.commit();
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error get data: " + e.getMessage());
                }
            }
        });

        /*AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("unm", Nasabah.username);
        client.get(this, "http://10.0.2.2/mini-internet-banking/API/nasabah/read-one.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                int jsonStart = json.indexOf("{");
                int jsonEnd = json.indexOf("}");

                if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart){
                    json = json.substring(jsonStart, jsonEnd+1);
                }

                try{
                    JSONObject jsonObject = new JSONObject(json);

                    id = jsonObject.getString("id_nasabah");
                    username = jsonObject.getString("username");
                    password = jsonObject.getString("password");
                    name = jsonObject.getString("nama_lengkap");
                    code = jsonObject.getString("kode_rahasia");
                    birthday = jsonObject.getString("tgl_lahir");
                    rekeningNum = jsonObject.getString("no_rek");
                    saldo = jsonObject.getString("jml_saldo");

                    Nasabah.id = id;
                    Nasabah.name = name;
                    Nasabah.username = username;
                    Nasabah.password = password;
                    Nasabah.code = code;
                    Nasabah.birthday = birthday;
                    Nasabah.rekeningNum = rekeningNum;
                    if (saldo != null && saldo != "") {
                        Nasabah.saldo = Float.parseFloat(saldo);
                    }else{
                        Nasabah.saldo = 0;
                    }

                    SharedPreferences.Editor spEdit = sp.edit();
                    spEdit.putBoolean("isLogin", true);
                    spEdit.putString("id", Nasabah.id);
                    spEdit.putString("name", Nasabah.name);
                    spEdit.putString("username", Nasabah.username);
                    spEdit.putString("password", Nasabah.password);
                    spEdit.putString("code", Nasabah.code);
                    spEdit.putString("birthday", Nasabah.birthday);
                    spEdit.putString("rekeningNum", Nasabah.rekeningNum);
                    spEdit.putFloat("saldo", Nasabah.saldo);
                    spEdit.commit();
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
        });*/
        return true;
    }
}
