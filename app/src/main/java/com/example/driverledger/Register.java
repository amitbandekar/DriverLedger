package com.example.driverledger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

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

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> handleRegister());

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
        }
    }

    private void handleError(String message) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(message)
                .setConfirmText("OK")
                .show();
    }
}
