package com.example.asus.elephant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteActivity extends AppCompatActivity {

    String email, password, name, date, code, isSharing;
    TextView txt_code;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressDialog progressDialog;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        txt_code = (TextView)findViewById(R.id.userCode);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent myIntent = getIntent();
        if (myIntent!=null) {
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
            name = myIntent.getStringExtra("name");
            date = myIntent.getStringExtra("date");
            code = myIntent.getStringExtra("code");
            isSharing = myIntent.getStringExtra("isSharing");
        }

        txt_code.setText(code); // Displays the randomly generated code inside.
    }

    public void registerNewUser(View v) {
        progressDialog.setMessage("Please wait a minute while we create your own elephant!");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // update Firebase real-time database.
                            // isSharing == false means that the user is not sharing his/her location.
                            // Create the User object first.
                            user = auth.getCurrentUser();
                            userID = user.getUid();
                            CreateUser createUser = new CreateUser(name,
                                    email,
                                    password,
                                    code,
                                    "false",
                                    "N/A",
                                    "N/A",
                                    user.getUid());
                            // Then get the user id from the database.

                            ref.child(userID).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                progressDialog.dismiss();
                                                
                                                sendEmailVerification();
                                                auth.signOut();

                                                Intent myIntent = new Intent(InviteActivity.this, MainActivity.class);
                                                startActivity(myIntent);
                                                finish();

                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),
                                                        R.string.register_failure,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    });


                        }
                    }
                });
    }

    public void sendEmailVerification() {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),
                                    R.string.email_verify,
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.email_verify_fail,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
