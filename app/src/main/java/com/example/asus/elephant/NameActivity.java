package com.example.asus.elephant;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {

    String email, password;
    EditText n;
    //CircleImageView circleImageView;
    Button btn_click;
    //Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        n = (EditText)findViewById(R.id.txt_nameRegister);
        //circleImageView = (CircleImageView)findViewById(R.id.circleImageView);
        btn_click = (Button)findViewById(R.id.btn_nextName);

        Intent myIntent = getIntent();
        if (myIntent!=null) {
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
        }
    }

    // Need Date for knowing when a user was last seen.
    public void generateDateCode(View v) {
        Date needDate = new Date();
        SimpleDateFormat format_1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = format_1.format(needDate);

        Random rand = new Random();
        int num = 1000 + rand.nextInt(9000);    // Max will be 9999.
        String user_code = String.valueOf(num); // This method returns the string representation of the arg.

        // Email, password, nickname, date, code.

        Intent moveOn = new Intent(NameActivity.this, InviteActivity.class);
        moveOn.putExtra("email", email);
        moveOn.putExtra("password", password);
        moveOn.putExtra("name", n.getText().toString());
        moveOn.putExtra("date", date);
        moveOn.putExtra("code", user_code);
        moveOn.putExtra("isSharing", "false");
        startActivity(moveOn);
        finish();

    }


}
