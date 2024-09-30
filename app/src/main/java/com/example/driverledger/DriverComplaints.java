package com.example.driverledger;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DriverComplaints extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, detailsEditText, remarksEditText;
    private TextView vehicleNoValidationText, modelNameValidationText, detailsValidationText, remarksValidationText;
    private Switch problemCloseSwitch;
    private Button submitButton;
    private DatabaseHelper databaseHelper;
    int recordid =0;
    int id =1;
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
        // Insert data into the database
        boolean isInserted = databaseHelper.saveDriverComplaints(recordid,vehicleNo, modelName, details, remarks, problemClosed, currentDateTime);

        if (isInserted) {
            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Data saved successfully")
                    .show();
            Intent intent = new Intent(getContext(), HomeScreen.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error saving data")
                    .show();
        }
    }

    private void clearForm() {
        vehicleNoEditText.setText("");
        modelNameEditText.setText("");
        detailsEditText.setText("");
        remarksEditText.setText("");
        problemCloseSwitch.setChecked(false);

    }
    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0); // Assuming you need the first bundle from the list

        // Set text fields with data from the bundle
        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        detailsEditText.setText(data.getString("details", ""));
        remarksEditText.setText(data.getString("remarks", ""));

        // Set switches based on "Yes" or "No" stored in the bundle
        problemCloseSwitch.setChecked("Yes".equals(data.getString("problemClosed")));
    }
}
