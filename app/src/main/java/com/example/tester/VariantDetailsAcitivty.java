package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VariantDetailsAcitivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variant_details);

        String key = getIntent().getStringExtra("key");
        String username = getIntent().getStringExtra("username");
        String carId = getIntent().getStringExtra("carId");

         Log.d("key", key);


        DatabaseReference variantRef = FirebaseDatabase.getInstance().getReference().child(username).child(carId).child("variants").child(key);

        TextView engineTextView = findViewById(R.id.engineTextView);
        TextView mileageTextView = findViewById(R.id.mileageTextView);
        TextView transmissionTextView = findViewById(R.id.transmissionTextView);
        TextView seatingCapacityTextView = findViewById(R.id.seatingCapacityTextView);
        TextView airbagsTextView = findViewById(R.id.airbagsTextView);
        TextView variantPricee = findViewById(R.id.price);
        LinearLayout interiorColorsContainer = findViewById(R.id.interiorColorsContainer);
        LinearLayout exteriorColorsContainer = findViewById(R.id.exteriorColorsContainer);

        variantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    engineTextView.setText(dataSnapshot.child("engine").getValue(String.class));
                    mileageTextView.setText(dataSnapshot.child("mileage").getValue(String.class));
                    transmissionTextView.setText(dataSnapshot.child("transmission").getValue(String.class));
                    seatingCapacityTextView.setText(dataSnapshot.child("seating_capacity").getValue(String.class));
                    airbagsTextView.setText(dataSnapshot.child("no_of_airbags").getValue(String.class));
                    variantPricee.setText("Rs. " + dataSnapshot.child("price").getValue(String.class));

                    // Display interior colors
                    interiorColorsContainer.removeAllViews();
                    for (DataSnapshot colorSnapshot : dataSnapshot.child("interior_colors").getChildren()) {
                        String colorHex = colorSnapshot.getValue(String.class);
                        View colorView = new View(VariantDetailsAcitivty.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
                        params.setMargins(8, 0, 8, 0);
                        colorView.setLayoutParams(params);
                        colorView.setBackgroundColor(android.graphics.Color.parseColor(colorHex));
                        interiorColorsContainer.addView(colorView);
                    }

                    // Display exterior colors
                    exteriorColorsContainer.removeAllViews();
                    for (DataSnapshot colorSnapshot : dataSnapshot.child("exterior_colors").getChildren()) {
                        String colorHex = colorSnapshot.getValue(String.class);
                        View colorView = new View(VariantDetailsAcitivty.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
                        params.setMargins(8, 0, 8, 0);
                        colorView.setLayoutParams(params);
                        colorView.setBackgroundColor(android.graphics.Color.parseColor(colorHex));
                        exteriorColorsContainer.addView(colorView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VariantDetails", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }
}