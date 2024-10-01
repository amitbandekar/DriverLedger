package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register extends Fragment {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView usernameValidationText;
    private TextView emailValidationText;
    private TextView passwordValidationText;
    private TextView confirmPasswordValidationText;
    private Button registerButton;
    DatabaseHelper databaseHelper;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize UI components
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        usernameValidationText = view.findViewById(R.id.usernameValidationText);
        emailValidationText = view.findViewById(R.id.emailValidationText);
        passwordValidationText = view.findViewById(R.id.passwordValidationText);
        confirmPasswordValidationText = view.findViewById(R.id.confirmPasswordValidationText);
        registerButton = view.findViewById(R.id.registerButton);
        databaseHelper = new DatabaseHelper(getContext());

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> handleRegister());

        TextView loginTextView = view.findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(v -> {
            // Open the Register fragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.LoginRegister, new Login());
            transaction.addToBackStack(null); // Add to back stack so user can navigate back
            transaction.commit();
        });
        return view;
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        boolean isValid = true;

        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            usernameValidationText.setVisibility(View.GONE);
        }

        // Validate email
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            emailValidationText.setVisibility(View.GONE);
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            passwordValidationText.setVisibility(View.GONE);
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            confirmPasswordValidationText.setVisibility(View.GONE);
        }

        if (isValid) {
            handleRegister(username,password,email);
        }
    }


    private void handleRegister(String username, String password, String email) {
        // Call the register method
        String userKey = databaseHelper.register(username, password, email);

        if (userKey != null) {
            // Registration successful, use the userKey
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userKey", userKey);
            editor.apply();
            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Registration successful!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            // Redirect to next activity or main screen
                            Intent intent = new Intent(getContext(), HomeScreen.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        } else {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Registration Failed")
                    .setConfirmText("OK")
                    .show();
        }
    }

}
