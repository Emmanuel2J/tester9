package com.example.tester;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class home extends AppCompatActivity {
    RecyclerView recyclerView;
    ModelAdapter mainAdapter;
    EditText searchEditText;
    Query databaseQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi " + email);

        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseQuery = databaseReference.orderByChild("name");

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(databaseQuery, MainModel.class)
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
        Button btn = findViewById(R.id.button);

        buttonShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh the RecyclerView by recreating the query
                databaseQuery = databaseReference.orderByChild("name");
                FirebaseRecyclerOptions<MainModel> refreshedOptions =
                        new FirebaseRecyclerOptions.Builder<MainModel>()
                                .setQuery(databaseQuery, MainModel.class)
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, community_chat.class);
                intent.putExtra("email", email); // Pass the email string to the next activity
                startActivity(intent);
            }
        });

        // Implementing search functionality
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String searchText = searchEditText.getText().toString();
                    performSearch(searchText);
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String searchText) {
        databaseQuery = FirebaseDatabase.getInstance().getReference().child("User")
                .orderByChild("name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<MainModel> searchOptions =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(databaseQuery, MainModel.class)
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
