package com.example.asus.elephant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinHerdActivity extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference ref, ref2, circleRef;
    FirebaseUser user;
    FirebaseAuth auth;
    String userId, joinUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_herd);

        pinview = (Pinview)findViewById(R.id.pinviewCode);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference().child("Users"); // Points toward Users.
        ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()); // Points towards current user.
        // Look below for circleRef.

        userId = user.getUid();
    }

    public void joinButton(View v) {

        Query query = ref.orderByChild("code").equalTo(pinview.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    CreateUser createUser = null;
                    for(DataSnapshot childDSS : dataSnapshot.getChildren()) {

                        createUser = childDSS.getValue(CreateUser.class);

                        joinUserId = createUser.userId;   // The User A which User B wants to join.

                        circleRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(joinUserId)
                                .child("Herd Members");

                        // Now create the herd member object by calling the UserHerd class.
                        // Sets the user asking to join herd, as a member (giving him/her a member ID).
                        UserHerd userHerd = new UserHerd(userId);   // User B
                        UserHerd userHerd1 = new UserHerd(joinUserId);  // User A

                        circleRef.child(user.getUid()).setValue(userHerd)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.join_herd_success,
                                            Toast.LENGTH_LONG).show();

                                    finish();

                                    Intent intent = new Intent(JoinHerdActivity.this, LocationMainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.join_herd_fail,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                }
                else {

                    Toast.makeText(getApplicationContext(),
                            R.string.code_missing,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
