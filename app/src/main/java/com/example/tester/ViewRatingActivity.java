package com.example.tester;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewRatingActivity extends AppCompatActivity {

    private LinearLayout ratingsContainer;
    private DatabaseReference ratingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);

        ratingsContainer = findViewById(R.id.ratingsContainer);

        String showroomName = getIntent().getStringExtra("showroomName");
        ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings").child(showroomName);

        ratingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingsContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String feedback = snapshot.child("feedback").getValue(String.class);
                    float rating = snapshot.child("rating").getValue(Float.class);

                    TextView ratingView = new TextView(ViewRatingActivity.this);
                    ratingView.setText("User: " + username + "\nRating: " + rating + "\nFeedback: " + feedback);
                    ratingView.setPadding(16, 16, 16, 16);
                    ratingsContainer.addView(ratingView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
