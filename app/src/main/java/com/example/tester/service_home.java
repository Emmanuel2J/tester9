package com.example.tester;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class service_home extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BannerAdapter bannerAdapter;
    private ArrayList<Banner> bannerList;
    private Handler handler;
    private Runnable runnable;
    private Timer timer;

    RecyclerView recyclerView;
    ModelAdapter mainAdapter;
    EditText searchEditText, positionSearchEditText;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi " + email);

        // Initialize banner components
        viewPager = findViewById(R.id.viewPager);
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList);
        viewPager.setAdapter(bannerAdapter);

        loadBannersFromFirebase();
        setupAutoScroll();

        // Initialize RecyclerView components
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        positionSearchEditText = findViewById(R.id.positionSearchEditText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("service");

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(databaseReference, MainModel.class)
                        .build();

        mainAdapter = new ModelAdapter(options);
        recyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new ModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainModel mainModel) {
                Intent intent = new Intent(service_home.this, ShowroomDetailsActivity.class);
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
                // Navigate to home.class when the Showroom button is clicked
                Intent intent = new Intent(service_home.this, home.class);
                startActivity(intent);
            }
        });

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(service_home.this, community_chat.class);
                intent.putExtra("email", email); // Pass the email string to the next activity
                startActivity(intent);
            }
        });

        buttonService.setOnClickListener(new View.OnClickListener() {
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
        timer.cancel();
    }
}
