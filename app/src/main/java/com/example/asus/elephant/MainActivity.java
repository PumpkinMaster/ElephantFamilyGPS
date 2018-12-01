package com.example.asus.elephant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            setContentView(R.layout.activity_main);

            permission = new PermissionManager() {};
            permission.checkAndRequestPermissions(this);
        }
        else {
            Intent myIntent = new Intent(MainActivity.this, LocationMainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        permission.checkResult(requestCode,permissions, grantResults);

        ArrayList<String> p_denied = permission.getStatus().get(0).denied;

        if (p_denied.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    R.string.permission,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // If user clicks Sign In.
    public void goToSignIn(View v) {
        Intent myIntent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(myIntent);
    }

    // If user clicks Register.
    public void goToRegister(View v) {
        Intent myIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(myIntent);
    }
}
