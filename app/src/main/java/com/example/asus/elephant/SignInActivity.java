package com.example.asus.elephant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.View;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText e, p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        e = (EditText)findViewById(R.id.emailSignIn);   // for the email.
        p = (EditText)findViewById(R.id.passwordSignIn);    // for the password.

        auth = FirebaseAuth.getInstance();
    }

    public void login(android.view.View v) {

        auth.signInWithEmailAndPassword(e.getText().toString(), p.getText().toString())
                // the listener below is only called when task is completed.
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    // public abstract void onComplete?
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Notifies user if login is okay or not.
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();
                            if (user.isEmailVerified()) {

                                Toast.makeText(getApplicationContext(),
                                        R.string.toast_loginYeah,
                                        Toast.LENGTH_LONG).show();

                                Intent myIntent = new Intent(SignInActivity.this, LocationMainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }

                            else {

                                Toast.makeText(getApplicationContext(),
                                        R.string.email_not_verified,
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }

                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.toast_loginBoo,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }
}
