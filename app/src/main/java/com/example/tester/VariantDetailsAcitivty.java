package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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

        String username = "Emmanuel"; // Replace with actual username logic
        String carId = "-Ny5WNaZjlbT5Or2qdxH"; // Replace with actual carId logic
        String key = getIntent().getStringExtra("key");

        DatabaseReference variantRef = FirebaseDatabase.getInstance().getReference()
                .child(username)
                .child(carId)
                .child("variants")
                .child(key);

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

                    String currentPrice = dataSnapshot.child("price").getValue(String.class);
                    variantPricee.setText("Rs. " + currentPrice);

                    // Check if offers node exists for the variant
                    DataSnapshot offersSnapshot = dataSnapshot.child("offers");
                    if (offersSnapshot.exists()) {
                        for (DataSnapshot offer : offersSnapshot.getChildren()) {
                            String discountPrice = offer.child("discount_price").getValue(String.class);
                            if (discountPrice != null && !discountPrice.isEmpty()) {
                                // Display current price with strike-through and discounted price
                                SpannableString spannableString = new SpannableString("Rs. " + currentPrice + "   Rs. " + discountPrice);
                                spannableString.setSpan(new StrikethroughSpan(), 0, currentPrice.length() + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                variantPricee.setText(spannableString);
                                break; // Assuming only one discount applies at a time, you can adjust logic if multiple discounts are possible
                            }
                        }
                    }

                    // Display interior colors
                    displayColors(interiorColorsContainer, dataSnapshot.child("interior_colors"));

                    // Display exterior colors
                    displayColors(exteriorColorsContainer, dataSnapshot.child("exterior_colors"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VariantDetails", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    // Helper method to display colors
    private void displayColors(LinearLayout container, DataSnapshot colorsSnapshot) {
        container.removeAllViews();
        for (DataSnapshot colorSnapshot : colorsSnapshot.getChildren()) {
            String colorHex = colorSnapshot.getValue(String.class);
            View colorView = new View(VariantDetailsAcitivty.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            params.setMargins(8, 0, 8, 0);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(android.graphics.Color.parseColor(colorHex));
            container.addView(colorView);
        }
    }
}
