package com.example.tester;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class home extends AppCompatActivity {
    RecyclerView recyclerView;
    ModelAdapter mainAdapter;
    EditText searchEditText, positionSearchEditText;
    DatabaseReference databaseReference;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi " + email);

        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        positionSearchEditText = findViewById(R.id.positionSearchEditText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(databaseReference, MainModel.class)
                        .build();

        mainAdapter = new ModelAdapter(options);
        recyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new ModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainModel mainModel) {
                Intent intent = new Intent(home.this, ShowroomDetailsActivity.class);
                intent.putExtra("name", mainModel.getName());
                intent.putExtra("position", mainModel.getPosition());
                intent.putExtra("email", mainModel.getEmail());
                intent.putExtra("image", mainModel.getImage());
                intent.putExtra("username", mainModel.getUsername()); // Add the username to the intent

                startActivity(intent);
            }
        });

        // Adding click listeners for the buttons
        Button buttonShowroom = findViewById(R.id.buttonShowroom);
        Button buttonService = findViewById(R.id.buttonService);

        buttonShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh the RecyclerView by recreating the query
                FirebaseRecyclerOptions<MainModel> refreshedOptions =
                        new FirebaseRecyclerOptions.Builder<MainModel>()
                                .setQuery(databaseReference, MainModel.class)
                                .build();
                mainAdapter.updateOptions(refreshedOptions);
            }
        });

        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to service_home.java when the Service button is clicked
                Intent intent = new Intent(home.this, service_home.class);
                startActivity(intent);
            }
        });
        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, community_chat.class);
                intent.putExtra("email", email); // Pass the email string to the next activity
                startActivity(intent);
            }
        });

        // Implementing search functionality for name
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String searchText = searchEditText.getText().toString().trim();
                    performSearch(searchText);
                    return true;
                }
                return false;
            }
        });

        // Implementing search functionality for position
        positionSearchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String searchText = positionSearchEditText.getText().toString().trim();
                    performPositionSearch(searchText);
                    return true;
                }
                return false;
            }
        });
        Button btn2 = findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
            }
        });
    }
    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Use the location
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    // Do something with latitude and longitude
                                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void performSearch(String searchText) {
        Query query = databaseReference.orderByChild("name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<MainModel> searchOptions =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(query, MainModel.class)
                        .build();

        mainAdapter.updateOptions(searchOptions);
    }

    private void performPositionSearch(String searchPosition) {
        Query query = databaseReference.orderByChild("position")
                .startAt(searchPosition)
                .endAt(searchPosition + "\uf8ff");

        FirebaseRecyclerOptions<MainModel> searchOptions =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(query, MainModel.class)
                        .build();

        mainAdapter.updateOptions(searchOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}
