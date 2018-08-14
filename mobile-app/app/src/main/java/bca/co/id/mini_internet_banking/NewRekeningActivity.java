package bca.co.id.mini_internet_banking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewRekeningActivity extends AppCompatActivity {
    private EditText new_name, new_email, new_password, new_ktp, new_birthday, new_address, new_code;
    private Button btnSubmitRekening;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_new_rekening);

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
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        new_birthday.setText(sdf.format(myCalendar.getTime()));
    }

    private void submitNewRekening(){
        String name = new_name.getText().toString();
        String email = new_email.getText().toString();
        String password = new_password.getText().toString();
        String ktp = new_ktp.getText().toString();
        String birthday = new_birthday.getText().toString();
        String address = new_address.getText().toString();
        String code = new_code.getText().toString();

        if(PasswordStrength.calculateStrength(password). getValue() < PasswordStrength.MEDIUM.getValue()){
            Toast.makeText(this, "Pembukaan Rekening Berhasil!", Toast.LENGTH_LONG).show();
            Nasabah.name = name;
            Nasabah.email = email;
            Nasabah.password = password;
            Nasabah.ktpNum = ktp;
            Nasabah.birthday = birthday;
            Nasabah.address = address;
            Nasabah.code = code;
            Intent intent = new Intent(this, NewUsernameActivity.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(this, "Password harus terdiri min 8 karakter dan alfanumerik!", Toast.LENGTH_LONG).show();
        }
    }
}
