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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView usernameValidationText;
    private TextView passwordValidationText;
    private Button loginButton;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        usernameValidationText = view.findViewById(R.id.usernameValidationText);
        passwordValidationText = view.findViewById(R.id.passwordValidationText);
        loginButton = view.findViewById(R.id.loginButton);

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
            saveData();
        }
    }

    private void saveData() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        // Insert data into the database
        String isInserted = databaseHelper.Login(username,password);

        if (isInserted=="") {
            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Login SuccessFully")
                    .show();
            Intent intent = new Intent(getContext(), HomeScreen.class);
            intent.putExtra("id", 1);
            startActivity(intent);
        } else {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Register First")
                    .show();
        }
    }


}
