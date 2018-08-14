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

        if (username.equals(Nasabah.username) && password.equals(Nasabah.password)){
            SharedPreferences.Editor spEdit = sp.edit();
            spEdit.putBoolean("isLogin", true);
            spEdit.commit();

            try {
                if (getNasabahData()){
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            Toast.makeText(this, "Login Gagal, Username atau password salah !", Toast.LENGTH_LONG).show();
        }
    }

    private void loadNewRekeningView(){
        Intent intent = new Intent(this, NewRekeningActivity.class);
        startActivity(intent);
    }

    public boolean getNasabahData() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, "http://192.168.43.234/mini-internet-banking/API/nasabah/read.php", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                try{
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("records");

                    String id = jsonArray.getJSONObject(0).getString("id");
                    String email = jsonArray.getJSONObject(0).getString("email");
                    String username = jsonArray.getJSONObject(0).getString("username");
                    String password = jsonArray.getJSONObject(0).getString("password");
                    String name = jsonArray.getJSONObject(0).getString("nama_lengkap");
                    String ktp = jsonArray.getJSONObject(0).getString("no_ktp");
                    String birthday = jsonArray.getJSONObject(0).getString("tgl_lahir");
                    String address = jsonArray.getJSONObject(0).getString("alamat");
                    String code = jsonArray.getJSONObject(0).getString("kode_rahasia");
                    String created = jsonArray.getJSONObject(0).getString("created");

                    Nasabah.id = id;
                    Nasabah.name = name;
                    Nasabah.username = username;
                    Nasabah.email = email;
                    Nasabah.password = password;
                    Nasabah.ktpNum = ktp;
                    Nasabah.birthday = birthday;
                    Nasabah.address = address;
                    Nasabah.code = code;
                    Nasabah.created = created;

                    Toast.makeText(mContext, "get data", Toast.LENGTH_LONG).show();
                } catch(final JSONException e){
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
