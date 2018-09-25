package bca.co.id.mini_internet_banking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewUsernameActivity extends AppCompatActivity {
    private TextView txtNewUsername, txtNewRekening;
    private Button btnOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_username);

        txtNewUsername = findViewById(R.id.txtNewUsername);
        txtNewRekening = findViewById(R.id.txtNewRekening);
        btnOk = findViewById(R.id.btnOk);

        Intent intent = getIntent();
        txtNewUsername.setText(Nasabah.username);
        txtNewRekening.setText(intent.getStringExtra("newNoRek"));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeView();
            }
        });
    }

    private void loadHomeView(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
