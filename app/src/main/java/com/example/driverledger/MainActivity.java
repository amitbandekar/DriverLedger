package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if it's not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
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