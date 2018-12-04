package com.example.asus.elephant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    // The GoogleApiClient connects to the API.

    FirebaseAuth auth;
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LocationManager locationManager;
    LatLng x_y;
    Location location;
    double latitude, longitude;
    double center;

    DatabaseReference ref;
    DatabaseReference refInsertCoordinates;
    FirebaseUser user;

    String user_name;
    String user_email;
    View header;
    TextView name, email;

    double x, y;
    // After connecting to the Google Maps API
    // Need to request user location.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();  // create the object.
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);  // get that specific user.
                // It's String.class because we want to extract a string.
                user_email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);

                // So after getting the name and email from the real time database.
                // Update the user's information on the navigation bar accordingly.
                name.setText(user_name);
                email.setText(user_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // The Google Map!
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.title_name);
        email = header.findViewById(R.id.title_email);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // To connect to Google Maps API.
        // Upon connection, go to onConnection method.
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();

        if (location==null){
            // Will use default location.
//            Toast.makeText(getApplicationContext(),
//                    "No location detected. Using default", Toast.LENGTH_SHORT).show();
            LatLng current_location = new LatLng( 5.3223009, 100.2798323);
            mMap.addMarker(new MarkerOptions().position(current_location).title("Current Location!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        }
        else {
            // Will get current location.
            latitude = location.getLatitude();
            longitude = location.getLongitude(); // In double format.
            // Added time: 18:22
            LatLng current_location = new LatLng( latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(current_location).title("Current Location!"));
        }

        // Entire app crashes when below is inserted.
//        x_y = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions options = new MarkerOptions();
//        options.position(x_y);
//        options.title("Current Location");
//
//        mMap.addMarker(options);


    }

    @Override
    public void onLocationChanged(Location location) {

        if (location==null) {
            Toast.makeText(getApplicationContext(),
                    R.string.no_location,
                    Toast.LENGTH_LONG).show();
        }

        else {

            x_y = new LatLng(location.getLatitude(), location.getLongitude());
            x = location.getLatitude();
            y = location.getLongitude();

//            Toast.makeText(getApplicationContext(),
//                      "BOXXXXXXX",
//                    Toast.LENGTH_LONG).show();

            MarkerOptions options = new MarkerOptions();
            options.position(x_y);
            options.title("Current Location");

            mMap.addMarker(options);
            // This keeps the screen showing your current location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(x_y));

            // All this does is update X coordinate in database.
            refInsertCoordinates = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(user.getUid())
                    .child("x");
            refInsertCoordinates.setValue(x)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "X inserted",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Unable to insert X",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // This updates the Y coordinate in the database.
            refInsertCoordinates = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(user.getUid())
                    .child("y");
            refInsertCoordinates.setValue(y)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "Y inserted",
                                        Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Unable to insert Y",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // mMap.addMarker(options);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myCircle) {

            Intent intent = new Intent(LocationMainActivity.this, MyHerdActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_joinHerd) {

            // finish()
            Intent intent = new Intent(LocationMainActivity.this, JoinHerdActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_shareLocation) {
            // BUG!
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.putExtra(Intent.EXTRA_TEXT, "I'm at: " + "https://www.google.com/maps/@" +
            x_y.latitude + "," + x_y.longitude + ", 17z");
            startActivity(myIntent.createChooser(myIntent, "Share location via: "));

        } else if (id == R.id.nav_signOut) {

            //FirebaseUser user = auth.getCurrentUser();
            if (user != null) {

                auth.signOut();
                finish();

                Intent myIntent = new Intent(LocationMainActivity.this, MainActivity.class);
                startActivity(myIntent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);  // Request location every (e.g. 5 second).

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),
                "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),
                "Connection suspended", Toast.LENGTH_SHORT).show();
    }

// Moved: 19:15
//    @Override
//    public void onLocationChanged(Location location) {
//
//        if (location==null) {
//            Toast.makeText(getApplicationContext(),
//                    R.string.no_location,
//                    Toast.LENGTH_LONG).show();
//        }
//
//        else {
//
//            x_y = new LatLng(location.getLatitude(), location.getLongitude());
//            x = location.getLatitude();
//            y = location.getLongitude();
//
////            Toast.makeText(getApplicationContext(),
////                      "BOXXXXXXX",
////                    Toast.LENGTH_LONG).show();
//
//            MarkerOptions options = new MarkerOptions();
//            options.position(x_y);
//            options.title("Current Location");
//
//            mMap.addMarker(options);
//            // This keeps the screen showing your current location.
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(x_y));
//
//            // All this does is update X coordinate in database.
//            refInsertCoordinates = FirebaseDatabase.getInstance().getReference()
//                    .child("Users")
//                    .child(user.getUid())
//                    .child("x");
//            refInsertCoordinates.setValue(x)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(),
//                                        "X inserted",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                            else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Unable to insert X",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//            // This updates the Y coordinate in the database.
//            refInsertCoordinates = FirebaseDatabase.getInstance().getReference()
//                    .child("Users")
//                    .child(user.getUid())
//                    .child("y");
//            refInsertCoordinates.setValue(y)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(),
//                                        "Y inserted",
//                                        Toast.LENGTH_SHORT).show();
//
//                            }
//                            else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Unable to insert Y",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//            // mMap.addMarker(options);
//        }
//    }


}
