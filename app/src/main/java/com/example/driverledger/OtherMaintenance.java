package com.example.driverledger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtherMaintenance extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, detailsEditText;
    private TextView vehicleNoValidationText, modelNameValidationText, detailsValidationText;
    private Button submitButton;
    private DatabaseHelper databaseHelper;
    int recordid =0;
    int id =1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_maintenance, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());



        // Initialize UI components
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        detailsEditText = view.findViewById(R.id.detailsEditText);
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        detailsValidationText = view.findViewById(R.id.detailsValidationText);
        submitButton = view.findViewById(R.id.submitButton);

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Bundle> bundleList = args.getParcelableArrayList("bundleList");
            recordid = args.getInt("recordid");
            id = args.getInt("id");
            // Check if bundleList is null
            if (bundleList != null) {
                SetData(bundleList);
            }
        } else {
            clearForm();
        }
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

        return isValid;
    }

    // Method to save data into the database
    private void saveData() {
        String vehicleNo = vehicleNoEditText.getText().toString().trim();
        String modelName = modelNameEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();

        // Get current date and time
        String currentDateTime  = new SimpleDateFormat("dd-MMMM-yy hh:mm a", Locale.getDefault()).format(new Date());
        // Insert data into the database
        boolean isInserted = databaseHelper.saveOtherMaintenanceData(recordid,vehicleNo, modelName, details, currentDateTime);

        if (isInserted) {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Data saved successfully")
                    .show();
            Intent intent = new Intent(getContext(), HomeScreen.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error saving data")
                    .show();
        }
    }

    private void clearForm() {
        vehicleNoEditText.setText("");
        modelNameEditText.setText("");
        detailsEditText.setText("");

    }
    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0); // Assuming you need the first bundle from the list

        // Set text fields with data from the bundle
        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        detailsEditText.setText(data.getString("details", ""));

    }
}
