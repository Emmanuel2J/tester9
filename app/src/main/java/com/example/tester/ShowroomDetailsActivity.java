package com.example.tester;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ShowroomDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom_details);

        // Retrieve data passed from previous activity
        String name = getIntent().getStringExtra("name");
        String position = getIntent().getStringExtra("position");
        String email = getIntent().getStringExtra("email");
        String image = getIntent().getStringExtra("image");
        String username = getIntent().getStringExtra("username"); // Retrieve the username
        Log.d("Username kiti", username);

        // Initialize views
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPosition = findViewById(R.id.tvPosition);
        TextView tvEmail = findViewById(R.id.tvEmail);
        Button buttonEnquiry = findViewById(R.id.buttonEnquiry);
        ImageView imageView1 = findViewById(R.id.imageView1);

        LinearLayout imageContainer = findViewById(R.id.imageContainer); // LinearLayout to hold ImageViews

        // Set text views
        tvName.setText(name);
        tvPosition.setText(position);
        tvEmail.setText(email);
        Picasso.get().load(image).placeholder(R.drawable.carveno).into(imageView1);


        // Firebase reference to the username node
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child(username);

        // Retrieve IMAGE1 data
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("ShowroomDetails", "DataSnapshot exists");
                    Log.d("ShowroomDetails", "Child count: " + dataSnapshot.getChildrenCount());
                    // Loop through all child nodes under username node
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("ShowroomDetails", "Snapshot key: " + snapshot.getKey());

                        if (snapshot.hasChild("image_url1")) {
                            Log.d("ShowroomDetails", "Found IMAGE1 node");

                            // Retrieve IMAGE1 value
                            String imageUrl = snapshot.child("image_url1").getValue(String.class);

                            // Create new ImageView
                            ImageView imageView = new ImageView(ShowroomDetailsActivity.this);
                            // Load image into ImageView using Picasso
                            Picasso.get().load(imageUrl).into(imageView);
                            // Add OnClickListener to navigate to another activity
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Intent to navigate to another activity
                                    Intent intent = new Intent(ShowroomDetailsActivity.this, InventoryDetailsActivity.class);
                                    // Pass any data if needed
                                    intent.putExtra("username", username);
                                    intent.putExtra("key",snapshot.getKey());
                                    startActivity(intent);
                                }
                            });
                            // Add ImageView to imageContainer LinearLayout
                            imageContainer.addView(imageView);
                        }
                        else{
                            Log.d("ShowroomDetails", "DataSnapshot does not exist");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("ShowroomDetails", "DatabaseError: " + databaseError.getMessage());

            }
        });


        // Add intent to navigate to EnquiryActivity
        buttonEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowroomDetailsActivity.this, Showroom_EnquiryActivity.class);
                intent.putExtra("showroomName", name); // Pass showroom name to EnquiryActivity
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }
}
