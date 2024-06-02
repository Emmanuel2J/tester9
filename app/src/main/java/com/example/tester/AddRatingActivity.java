package com.example.tester;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddRatingActivity extends AppCompatActivity {

    private EditText feedbackEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private DatabaseReference ratingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);

        // Initialize views
        feedbackEditText = findViewById(R.id.feedbackEditText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);

        // Get data passed from previous activity
        String showroomName = getIntent().getStringExtra("showroomName");
        String username = getIntent().getStringExtra("username");

        // Firebase reference to the ratings node
        ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings").child(showroomName);

        // Set onClick listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating(username);
            }
        });
    }

    private void submitRating(String username) {
        String feedback = feedbackEditText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (feedback.isEmpty() || rating == 0) {
            Toast.makeText(AddRatingActivity.this, "Please provide a rating and feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        String ratingId = ratingsRef.push().getKey();
        HashMap<String, Object> ratingData = new HashMap<>();
        ratingData.put("username", username);
        ratingData.put("feedback", feedback);
        ratingData.put("rating", rating);

        if (ratingId != null) {
            ratingsRef.child(ratingId).setValue(ratingData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddRatingActivity.this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRatingActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
