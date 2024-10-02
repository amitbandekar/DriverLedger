package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Profile extends Fragment {
    private TextView usernameTextView;
    private TextView emailTextView;
    private Button btnLogout;
    private DatabaseHelper databaseHelper;

    private TextView usernameInitials;

    private SharedPreferences sharedPreferences;
    private String userKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        usernameTextView = view.findViewById(R.id.username);
        emailTextView = view.findViewById(R.id.email);
        btnLogout = view.findViewById(R.id.btnLogout);
        usernameInitials = view.findViewById(R.id.usernameInitials);

        // Initialize shared preferences and get user key
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userKey = sharedPreferences.getString("userKey", null);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Load user data
        loadUserData();

        // Set up logout button listener
        btnLogout.setOnClickListener(view1 -> {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Do you really want to log out?")
                    .setConfirmText("Yes, log out")
                    .setCancelText("No, stay")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        // Handle logout action
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userKey", null);
                        editor.apply();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // Finish current activity if needed
                    })
                    .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                    .show();
        });

        return view;
    }

    private void loadUserData() {
        Cursor cursor = databaseHelper.getUserData(userKey);
        if (cursor != null && cursor.moveToFirst()) {
            // Get column indices for the required columns
            int usernameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");

            // Check if the indices are valid
            if (usernameIndex != -1 && emailIndex != -1) {
                String username = cursor.getString(usernameIndex);
                String email = cursor.getString(emailIndex);

                // Set initials and text views
                usernameInitials.setText(username.substring(0, 1).toUpperCase());
                usernameTextView.setText(username);
                emailTextView.setText(email);
            }
            cursor.close(); // Always close the cursor to prevent memory leaks
        }
    }

}
