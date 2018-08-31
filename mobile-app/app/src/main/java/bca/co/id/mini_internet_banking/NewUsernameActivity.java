package bca.co.id.mini_internet_banking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewUsernameActivity extends AppCompatActivity {
    private TextView txtNewUsername;
    private Button btnOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_username);

        txtNewUsername = findViewById(R.id.txtNewUsername);
        btnOk = findViewById(R.id.btnOk);

//        String[] names = Nasabah.name.split(" ");
//        String[] birthday = Nasabah.birthday.split("/");
//        String username = names[0] + birthday[0].substring(2, 3);
//        String rekening = "0000";

        //set nasabah username
        txtNewUsername.setText(Nasabah.username);

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
