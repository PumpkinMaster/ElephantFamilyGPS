package com.example.asus.elephant;

import android.content.Intent;
import android.drm.DrmStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyHerdActivity extends AppCompatActivity {

    private final String TAG = MyHerdActivity.class.getSimpleName();

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference usersRef, herdRef;

    HerdAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView mName, mEmail;

    CreateUser userObj;
    ArrayList<CreateUser> herdMembersList;
    ArrayList<String> herdMembersListString;
    String herdMemberListArray[];
    String herdMemberId;

    String otherPeopleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_herd);

        // The usual initialisations.
        recyclerView = (RecyclerView)findViewById(R.id.recyclerMyHerd);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        herdMembersList = new ArrayList<>();    // Will be used to store userObj.
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get a reference pointing to Users in general.
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Then get a reference pointing to the 'My Herd' members of a particular user.
        herdRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid())
                .child("herdMembers");

        // This is responsible for updating the 'My Herd' of a user.
        herdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                herdMembersList.clear();
                // dataSnapshot is basically a snapshot of the data in the Firebase Database.
                // It is immutable.
                // So if there is data in the herdRef,
                // List out every herd members.
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dss: dataSnapshot.getChildren()) {

                        // for each member, get his/her member ID.
                        herdMemberId = dss.child("memberId").getValue(String.class);

                        // Remember that usersRef points towards the general Users.
                        // So usersRef.child(herdMemberId) points towards that particular member's own node.
                        // Also, here I used .addListenerForSingleValueEvent
                        // because I only want this listener to be invoked when I need it.
                        // Not every time there are changes to the database reference it is attached to.
                        usersRef.child(herdMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // I want the user object.
                                userObj = dataSnapshot.getValue(CreateUser.class);
                                // Now add it to the list.

                                herdMembersList.add(userObj);
                                mAdapter.notifyDataSetChanged(); // Exactly what it says.
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext(),
                                        databaseError.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                } else {
                    Toast.makeText(getApplicationContext(), "DataSnapshot is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),
                        databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // This part is just for debugging.
        String[] membersList = new String[herdMembersList.size()];
        herdMemberListArray = herdMembersList.toArray(membersList);
        for(int i = 0; i < herdMemberListArray.length; i++){
            Log.d(TAG, herdMemberListArray[i]);
        }

        mAdapter = new HerdAdapter(herdMembersList, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        // If the user clicks on a user,
        // It will lead them to OtherPeopleLocationActivity.
        mAdapter.setOnItemClickListener(new HerdAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CreateUser obj, int position) {
                String memberId = obj.userId;   // Added 02:07
                Intent intent = new Intent(MyHerdActivity.this, OtherPeopleLocationActivity.class);
                intent.putExtra("otherPeople", otherPeopleName);
                intent.putExtra("otherPeopleMemberId", memberId);
                startActivity(intent);
            }
        });

        mAdapter.notifyDataSetChanged();
        Intent intent = getIntent();    // does not affect code.

    }


}
