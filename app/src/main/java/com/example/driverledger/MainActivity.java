package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Check if SharedPreferences contains a non-null "userKey"
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userKey = sharedPreferences.getString("userKey", null);

        if (userKey != null) {
            // If userKey exists, start a new activity (e.g., DashboardActivity)
            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
            startActivity(intent);
            finish(); // Optional: Close MainActivity to prevent going back to login
        } else {
            // If userKey is null, load the login fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.LoginRegister, new Login()).commit();
        }
    }
}