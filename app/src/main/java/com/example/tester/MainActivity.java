package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi user with email" + email);
    }
}