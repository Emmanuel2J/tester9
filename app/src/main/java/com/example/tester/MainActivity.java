package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void onLoginClick(View view) {
        // Launch Login Activity
        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
    }

    public void onRegisterClick(View view) {
        // Launch Register Activity
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
    }

    public void logout(View view) {
        // Code to logout the user
        // For example, you can clear user session or navigate to the login screen
        // Here, I'll just close the activity for demonstration purposes
        finish();
    }
}


