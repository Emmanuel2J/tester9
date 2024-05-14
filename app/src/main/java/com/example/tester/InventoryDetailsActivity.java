package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class InventoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        String username = getIntent().getStringExtra("username");
        String key = getIntent().getStringExtra("key");
        Log.d("username", username);
        Log.d("key", key);

        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child(username).child(key);

        LinearLayout imageContainer = findViewById(R.id.imageContainer);
        TextView carNameTextView = findViewById(R.id.carNameTextView);
        TextView textEngine = findViewById(R.id.textEngine);
        TextView textFuel = findViewById(R.id.textFuel);
        TextView textMileage = findViewById(R.id.textMileage);
        TextView textTransm = findViewById(R.id.textTransm);

        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String carName = dataSnapshot.child("car_name").getValue(String.class);
                    carNameTextView.setText(carName);

                    // Set the values of text views
                    textEngine.setText(dataSnapshot.child("engine").getValue(String.class));
                    textFuel.setText(dataSnapshot.child("fuel_type").getValue(String.class));
                    textMileage.setText(dataSnapshot.child("mileage").getValue(String.class));
                    textTransm.setText(dataSnapshot.child("transmission").getValue(String.class));

                    for (int i = 1; i <= 5; i++) {
                        String imageUrl = dataSnapshot.child("image_url" + i).getValue(String.class);
                        if (imageUrl != null) {
                            ImageView imageView = new ImageView(InventoryDetailsActivity.this);
                            Picasso.get().load(imageUrl).into(imageView);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(10, 0, 10, 0);
                            imageView.setLayoutParams(layoutParams);
                            imageContainer.addView(imageView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("InventoryDetails", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

}

