package com.example.tester;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ShowroomDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom_details);

        String name = getIntent().getStringExtra("name");
        String position = getIntent().getStringExtra("position");
        String email = getIntent().getStringExtra("email");
        String image = getIntent().getStringExtra("image");

        TextView tvName = findViewById(R.id.tvName);
        TextView tvPosition = findViewById(R.id.tvPosition);
        TextView tvEmail = findViewById(R.id.tvEmail);
        ImageView imageView = findViewById(R.id.imageView);

        tvName.setText(name);
        tvPosition.setText(position);
        tvEmail.setText(email);
        Picasso.get().load(image).placeholder(R.drawable.carveno).into(imageView);
    }
}
