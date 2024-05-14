package com.example.tester;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Showroom_EnquiryActivity extends AppCompatActivity {

    EditText editTextEnquiry;
    Button buttonSubmitEnquiry;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    String showroomName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom_enquiry);

        showroomName = getIntent().getStringExtra("showroomName");
        final String username = getIntent().getStringExtra("username"); // Retrieve the username

        editTextEnquiry = findViewById(R.id.editTextEnquiry);
        buttonSubmitEnquiry = findViewById(R.id.buttonSubmitEnquiry);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();
            databaseReference = FirebaseDatabase.getInstance().getReference().child(username).child("Enquiries");
        }

        buttonSubmitEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enquiry = editTextEnquiry.getText().toString().trim();
                if (!enquiry.isEmpty()) {
                    if (showroomName != null && !showroomName.isEmpty()) {
                        // Store enquiry details in the database with showroom name
                        String enquiryId = databaseReference.push().getKey();
                        if (enquiryId != null) {
                            databaseReference.child(enquiryId).child("email").setValue(userEmail);
                            databaseReference.child(enquiryId).child("enquiry").setValue(enquiry);
                            databaseReference.child(enquiryId).child("name").setValue(showroomName);
                            Toast.makeText(Showroom_EnquiryActivity.this, "Enquiry submitted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Showroom_EnquiryActivity.this, "Failed to generate enquiry ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Showroom_EnquiryActivity.this, "Showroom name is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Showroom_EnquiryActivity.this, "Please enter enquiry details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
