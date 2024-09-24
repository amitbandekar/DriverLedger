package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView usernameValidationText;
    private TextView passwordValidationText;
    private Button loginButton;
    private ExecutorService executor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        usernameValidationText = view.findViewById(R.id.usernameValidationText);
        passwordValidationText = view.findViewById(R.id.passwordValidationText);
        loginButton = view.findViewById(R.id.loginButton);

        // Initialize Executor for background tasks
        executor = Executors.newSingleThreadExecutor();

        loginButton.setOnClickListener(v -> handleLogin());

        return view;
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            usernameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            usernameValidationText.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(password)) {
            passwordValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            passwordValidationText.setVisibility(View.GONE);
        }

        if (isValid) {
            executor.execute(() -> {
                try {
                    String response = ApiService.login(username, password);

                    // Process the response in the UI thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if ("login_success".equals(status)) {
                                String authkey = jsonResponse.getString("authkey");
                                SharedPreferences sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("authkey", authkey);
                                editor.apply();

                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText("Login successful!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(sDialog -> {
                                            sDialog.dismissWithAnimation();
                                            Intent intent = new Intent(requireActivity(), HomeScreen.class);
                                            startActivity(intent);
                                            requireActivity().finish();
                                        })
                                        .show();
                            } else {
                                handleError("Invalid username or password.");
                            }
                        } catch (JSONException e) {
                            handleError("Invalid response from the server.");
                        }
                    });
                } catch (Exception e) {
                    handleError("Error connecting to server.");
                }
            });
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
