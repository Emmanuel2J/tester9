package com.example.tester;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class home extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BannerAdapter bannerAdapter;
    private ArrayList<Banner> bannerList;
    private Handler handler;
    private Runnable runnable;
    private Timer timer;

    private RecyclerView recyclerView;
    private ModelAdapter mainAdapter;
    private EditText searchEditText, positionSearchEditText;
    private DatabaseReference databaseReference;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String email = getIntent().getStringExtra("email");
        getSupportActionBar().setTitle("Hi " + email);

        // Initialize banner components
        viewPager = findViewById(R.id.viewPager);
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList);
        viewPager.setAdapter(bannerAdapter);
        loadBannersFromFirebase();
        setupAutoScroll();

        // Existing RecyclerView setup
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
                Log.d("Showroom Location", "Latitude: " + mainModel.getLatitude() + ", Longitude: " + mainModel.getLongitude());

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
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<MainModel> showroomList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MainModel showroom = dataSnapshot.getValue(MainModel.class);
                            if (showroom != null) {
                                showroomList.add(showroom);
                            }
                        }
                        mainAdapter.updateList(showroomList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors.
                    }
                });
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

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permissions and get the device location
                getLocationPermission();
            }
        });

    }

    private void loadBannersFromFirebase() {
        DatabaseReference bannersRef = FirebaseDatabase.getInstance().getReference().child("offerbanner");
        bannersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageUrl = dataSnapshot.child("image_url").getValue(String.class);
                    Banner banner = new Banner(imageUrl);
                    bannerList.add(banner);
                }
                bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Radius of the earth in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // Distance in km
        return distance;
    }

    private void setupAutoScroll() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = bannerAdapter.getItemCount();
                if (currentItem < totalItems - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 3000, 3000);
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
        timer.cancel();
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
                                    double userLatitude = location.getLatitude();
                                    double userLongitude = location.getLongitude();

                                    // Get the list of showrooms and sort them based on the distance
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            List<MainModel> showroomList = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                MainModel showroom = dataSnapshot.getValue(MainModel.class);
                                                if (showroom != null) {
                                                    showroomList.add(showroom);
                                                }
                                            }

                                            Collections.sort(showroomList, new Comparator<MainModel>() {
                                                @Override
                                                public int compare(MainModel s1, MainModel s2) {
                                                    double distanceToS1 = calculateDistance(userLatitude, userLongitude,
                                                            Double.parseDouble(s1.getLatitude()), Double.parseDouble(s1.getLongitude()));
                                                    double distanceToS2 = calculateDistance(userLatitude, userLongitude,
                                                            Double.parseDouble(s2.getLatitude()), Double.parseDouble(s2.getLongitude()));
                                                    return Double.compare(distanceToS1, distanceToS2);
                                                }
                                            });

                                            // Update the adapter with the sorted list
                                            mainAdapter.updateList(showroomList);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle possible errors.
                                        }
                                    });
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
}
