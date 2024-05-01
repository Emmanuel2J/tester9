package com.example.tester;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        String email = getIntent().getStringExtra("email");
        TextView abc = findViewById(R.id.Hi);
        abc.setText("Hi user with email" + email);
    }
}


