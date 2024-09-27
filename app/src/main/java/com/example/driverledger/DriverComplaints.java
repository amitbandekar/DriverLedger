package com.example.driverledger;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DriverComplaints extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, detailsEditText, remarksEditText;
    private TextView vehicleNoValidationText, modelNameValidationText, detailsValidationText, remarksValidationText;
    private Switch problemCloseSwitch;
    private Button submitButton;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_complaints, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize UI components
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        detailsEditText = view.findViewById(R.id.detailsEditText);
        remarksEditText = view.findViewById(R.id.remarksEditText);
        problemCloseSwitch = view.findViewById(R.id.problemCloseSwitch);
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        detailsValidationText = view.findViewById(R.id.detailsValidationText);
        remarksValidationText = view.findViewById(R.id.remarksValidationText);
        submitButton = view.findViewById(R.id.submitButton);

        // Set the submit button click listener
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                saveData();
            }
        });

        return view;
    }

    // Method to validate form fields
    private boolean validateForm() {
        boolean isValid = true;

        // Validate vehicle number
        if (TextUtils.isEmpty(vehicleNoEditText.getText().toString().trim())) {
            vehicleNoValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            vehicleNoValidationText.setVisibility(View.GONE);
        }

        // Validate model name
        if (TextUtils.isEmpty(modelNameEditText.getText().toString().trim())) {
            modelNameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            modelNameValidationText.setVisibility(View.GONE);
        }

        // Validate details
        if (TextUtils.isEmpty(detailsEditText.getText().toString().trim())) {
            detailsValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            detailsValidationText.setVisibility(View.GONE);
        }

        // Validate remarks
        if (TextUtils.isEmpty(remarksEditText.getText().toString().trim())) {
            remarksValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            remarksValidationText.setVisibility(View.GONE);
        }

        return isValid;
    }

    // Method to save data into the database
    private void saveData() {
        String vehicleNo = vehicleNoEditText.getText().toString().trim();
        String modelName = modelNameEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();
        String remarks = remarksEditText.getText().toString().trim();
        String problemClosed = problemCloseSwitch.isChecked() ? "Yes" :"No";

        // Get current date and time
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        int id = 0;
        // Insert data into the database
        boolean isInserted = databaseHelper.saveDriverComplaints(id,vehicleNo, modelName, details, remarks, problemClosed, currentDateTime);

        if (isInserted) {
            Toast.makeText(getContext(), "Data Saved Successfully", Toast.LENGTH_SHORT).show();
            // Reset form fields after saving
            vehicleNoEditText.setText("");
            modelNameEditText.setText("");
            detailsEditText.setText("");
            remarksEditText.setText("");
            problemCloseSwitch.setChecked(false);
        } else {
            Toast.makeText(getContext(), "Data Insertion Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
