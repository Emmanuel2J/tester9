package com.example.tester;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class service_home extends AppCompatActivity {
    RecyclerView recyclerView;
    ModelAdepter mainAdapter;
    Query databaseQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi " + email);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseQuery = FirebaseDatabase.getInstance().getReference().child("service");

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(databaseQuery, MainModel.class)
                        .build();

        mainAdapter = new ModelAdepter(options);
        recyclerView.setAdapter(mainAdapter);

        // Adding click listeners for the buttons
        Button buttonShowroom = findViewById(R.id.buttonShowroom);
        Button buttonService = findViewById(R.id.buttonService);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();

        // Add ValueEventListener for error handling
        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Data successfully loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // An error occurred while fetching data
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}
