package bca.co.id.mini_internet_banking;

import android.app.DatePickerDialog;
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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class NewNasabahActivity extends AppCompatActivity {
    private EditText new_name, new_email, new_password, new_ktp, new_birthday, new_address, new_code;
    private Button btnSubmitRekening;
    Calendar myCalendar = Calendar.getInstance();
    private String TAG = NewNasabahActivity.class.getSimpleName();
    private Context mContext;
    private SharedPreferences sp;
    private String id, username, name, password, code, birthday, rekeningNum, saldo;
    private List<String> listLog = new ArrayList<String>();
    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US);
    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_new_nasabah);

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

        //set listener for date pciker dialog
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

        //show date picker dialog when EditText is tapped
        new_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NewNasabahActivity.this, date, myCalendar
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

    //set string date from date picker dialog
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
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing password failed: " + e.getMessage());
            Log.e(TAG, "Hashing password failed: " + e.getMessage());
            e.printStackTrace();
        }

        //encrypt secret code using MD5 algorithm
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
            Log.e(TAG, "Hashing secret code failed: " + e.getMessage());
            listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Hashing secret code failed: " + e.getMessage());
            e.printStackTrace();
        }

        Nasabah.birthday = birthday;

        //checking password strength & secret code strength, after that send http POST data to server about nasabah data
        if(PasswordStrength.calculateStrength(password).getValue() > PasswordStrength.MEDIUM.getValue()){
            if (CodeStrength.calculateStrength(code).getValue() > CodeStrength.MEDIUM.getValue()) {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String url = HttpClientURL.urlCreateNasabah;

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("nama_lengkap", name);
                    jsonParams.put("email", email);
                    jsonParams.put("password", hashPassword);
                    jsonParams.put("no_ktp", ktp);
                    jsonParams.put("tgl_lahir", birthday);
                    jsonParams.put("alamat", address);
                    jsonParams.put("kode_rahasia", hashCode);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to create JSONObject for post param: " + e.getMessage());
                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Failed to create JSONObject for post param: " + e.getMessage());
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(JSON, jsonParams.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                final String finalHashPassword = hashPassword;
                final String finalHashCode = hashCode;

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "error in getting response using async okhttp call");
                        listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[ERROR] " + ": " + "Error in getting response from async okhttp call");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseBody = response.body().string().toString();

                        Log.e(TAG, responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String result = jsonObject.getString("status");

                            if (result.equalsIgnoreCase("berhasil")){
                                Nasabah.id = jsonObject.getString("idnsb");
                                String noRek = jsonObject.getString("no_rek");
                                Nasabah.username = jsonObject.getString("username");

                                Log.i(TAG, "Registering nasabah suceess, [" +
                                        "Name = " + name +
                                        ", Email = " + email +
                                        ", Password = " + finalHashPassword +
                                        ", No KTP = " + ktp +
                                        ", Tangggal Lahir = " + birthday +
                                        ", Alamat = " + address +
                                        ", Kode Rahasia = " + finalHashCode + "]");
                                listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Registering nasabah success, [" +
                                        "Name = " + name +
                                        ", Email = " + email +
                                        ", Password = " + finalHashPassword +
                                        ", No KTP = " + ktp +
                                        ", Tangggal Lahir = " + birthday +
                                        ", Alamat = " + address +
                                        ", Kode Rahasia = " + finalHashCode + "]");

                                //call get nasabah data
                                if (getNasabahData()){
                                    Log.i(TAG, "Getting nasabah data success");
                                    listLog.add(s.format(new Date()) + " | " + TAG + " | " + "[INFO] " + ": " + "Getting Nasabah data success");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "Pembukaan Rekening Berhasil!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    writeLogs();
                                    Intent intent = new Intent(mContext, NewUsernameActivity.class);
                                    intent.putExtra("newNoRek", noRek);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else{
                Toast.makeText(this, "Kode Rahasia harus terdiri 6 karakter, alfanumerik dan tidak terdiri dari tanggal lahir", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "Password harus terdiri min 8 karakter, alfanumerik dan tidak terdiri dari tanggal lahir!", Toast.LENGTH_LONG).show();
        }
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
