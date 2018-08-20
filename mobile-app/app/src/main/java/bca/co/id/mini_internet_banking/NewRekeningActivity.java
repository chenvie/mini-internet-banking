package bca.co.id.mini_internet_banking;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewRekeningActivity extends AppCompatActivity {
    private EditText new_name, new_email, new_password, new_ktp, new_birthday, new_address, new_code;
    private Button btnSubmitRekening;
    Calendar myCalendar = Calendar.getInstance();
    private String TAG = NewRekeningActivity.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sp;
    private String id, username, name, password, code, birthday, rekeningNum, saldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_new_rekening);

        mContext = this;

        sp = getSharedPreferences("ibank", MODE_PRIVATE);

        new_name = findViewById(R.id.new_name);
        new_email = findViewById(R.id.new_email);
        new_password = findViewById(R.id.new_password);
        new_ktp = findViewById(R.id.new_ktp);
        new_birthday = findViewById(R.id.new_birthday);
        new_address = findViewById(R.id.new_address);
        new_code = findViewById(R.id.new_code);
        btnSubmitRekening = findViewById(R.id.btnSubmitRekening);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        new_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewRekeningActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmitRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewRekening();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        new_birthday.setText(sdf.format(myCalendar.getTime()));
    }

    private void submitNewRekening(){
        final String name = new_name.getText().toString();
        final String email = new_email.getText().toString();
        final String password = new_password.getText().toString();
        final String ktp = new_ktp.getText().toString();
        final String birthday = new_birthday.getText().toString();
        final String address = new_address.getText().toString();
        final String code = new_code.getText().toString();

        Nasabah.birthday = birthday;

        if(PasswordStrength.calculateStrength(password).getValue() > PasswordStrength.MEDIUM.getValue()){
            if (CodeStrength.calculateStrength(code).getValue() > CodeStrength.MEDIUM.getValue()) {

                AsyncHttpClient client = new AsyncHttpClient();

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("nama_lengkap", name);
                    jsonParams.put("email", email);
                    jsonParams.put("password", password);
                    jsonParams.put("no_ktp", ktp);
                    jsonParams.put("tgl_lahir", birthday);
                    jsonParams.put("alamat", address);
                    jsonParams.put("kode_rahasia", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*RequestParams rp = new RequestParams();
                rp.add("nama_lengkap", name);
                rp.add("email", email);
                rp.add("password", password);
                rp.add("no_ktp", ktp);
                rp.add("tgl_lahir", birthday);
                rp.add("alamat", address);
                rp.add("kode_rahasia", code);*/


                try {
                    StringEntity entity = new StringEntity(jsonParams.toString());

                    client.post(mContext, "http://10.0.2.2/mini-internet-banking/API/nasabah/create.php", entity, "application/json",  new AsyncHttpResponseHandler() {
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
                                String result = jsonObject.getString("message");
                                String username = jsonObject.getString("username");

                                if (result.equalsIgnoreCase("pendaftaran berhasil")){
                                    Nasabah.name = name;
                                    Nasabah.username = username;
                                    Nasabah.email = email;
                                    Nasabah.password = password;
                                    Nasabah.ktpNum = ktp;
                                    Nasabah.birthday = birthday;
                                    Nasabah.address = address;
                                    Nasabah.code = code;

                                    if (getNasabahData()){
                                        Toast.makeText(mContext, "Pembukaan Rekening Berhasil!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(mContext, NewUsernameActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error open rek: " + e.getMessage());
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else{
                Toast.makeText(this, "Kode Rahasia harus terdiri 6 karakter, alfanumerik dan tidak terdiri dari tanggal lahir", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Password harus terdiri min 8 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean getNasabahData() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams rp = new RequestParams();
        rp.add("id", Nasabah.username);
        client.get(this, "http://10.0.2.2/mini-internet-banking/API/nasabah/read-one.php", rp, new AsyncHttpResponseHandler() {
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
        });
        return true;
    }
}
