package com.example.asus.elephant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText e;
    EditText p;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        e = (EditText)findViewById(R.id.input_emailRegister);
        p = (EditText)findViewById(R.id.passwordRegister);
        auth = FirebaseAuth.getInstance();  // initialise it.
        dialog = new ProgressDialog(this);
    }

    // For checking if the email is already in the Firebase.
    // There are actually 2 ways
    // First auth.createUser...
    // and then if it fails, it basically means email is already registered.
    // Secondly, auth.fetchSignInMethodsForEmail.
    public void goToNameActivity(View v) {
        dialog.setMessage("Loading");
        dialog.show();
        auth.fetchSignInMethodsForEmail(e.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()){

                            dialog.dismiss();
                            // if it is not empty.
                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                            // email is not in the Firebase.
                            if (!check) {

                                if (p.getText().toString().length() > 7) {
                                    //Toast.makeText(getApplicationContext(), R.string.toast_testing, Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(RegisterActivity.this, NameActivity.class);
                                    myIntent.putExtra("email", e.getText().toString());
                                    myIntent.putExtra("password", p.getText().toString());
                                    startActivity(myIntent);
                                    finish();
                                }
                                else {
                                    // Password isn't strong enough.
                                    Toast.makeText(getApplicationContext(),
                                            R.string.toast_passwordLen,
                                            Toast.LENGTH_LONG).show();
                                }
//

                            }

                            else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        R.string.toast_emailUsed,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
