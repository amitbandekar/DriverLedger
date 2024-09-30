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

public class OilChangeService extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, runningKmEditText, nextServiceKmEditText, remarkEditText;
    private TextView vehicleNoValidationText, modelNameValidationText, kmValidationText, nextServiceKmValidationText, remarkValidationText;
    private Switch dieselFilterSwitch, breakOilSwitch, coolantSwitch;
    private Button submitButton;
    private DatabaseHelper databaseHelper;
    int recordid =0;
    int id =1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oil_change_service, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());



        // Initialize UI components
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        runningKmEditText = view.findViewById(R.id.runningKmEditText);
        nextServiceKmEditText = view.findViewById(R.id.nextServiceKmEditText);
        remarkEditText = view.findViewById(R.id.remarkEditText);
        dieselFilterSwitch = view.findViewById(R.id.dieselFilterSwitch);
        breakOilSwitch = view.findViewById(R.id.breakOilSwitch);
        coolantSwitch = view.findViewById(R.id.coolantSwitch);

        // Validation Text Views
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        kmValidationText = view.findViewById(R.id.kmValidationText);
        remarkValidationText = view.findViewById(R.id.remarkValidationText);

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
        // Submit Button
        submitButton = view.findViewById(R.id.submitButton);

        // Set onClickListener for the Submit Button
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

        // Validate Vehicle No.
        if (TextUtils.isEmpty(vehicleNoEditText.getText().toString().trim())) {
            vehicleNoValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            vehicleNoValidationText.setVisibility(View.GONE);
        }

        // Validate Model Name
        if (TextUtils.isEmpty(modelNameEditText.getText().toString().trim())) {
            modelNameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            modelNameValidationText.setVisibility(View.GONE);
        }

        // Validate Running KM
        if (TextUtils.isEmpty(kmValidationText.getText().toString().trim())) {
            kmValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            kmValidationText.setVisibility(View.GONE);
        }

        // Validate Next Service KM
        if (TextUtils.isEmpty(nextServiceKmEditText.getText().toString().trim())||TextUtils.isEmpty(runningKmEditText.getText().toString().trim())) {
            nextServiceKmValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            nextServiceKmValidationText.setVisibility(View.GONE);
        }

        // Validate Remark
        if (TextUtils.isEmpty(remarkEditText.getText().toString().trim())) {
            remarkValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            remarkValidationText.setVisibility(View.GONE);
        }

        return isValid;
    }

    // Method to save data into the database
    private void saveData() {
        String vehicleNo = vehicleNoEditText.getText().toString().trim();
        String modelName = modelNameEditText.getText().toString().trim();
        String runningKm = runningKmEditText.getText().toString().trim();
        String nextServiceKm = nextServiceKmEditText.getText().toString().trim();
        String remark = remarkEditText.getText().toString().trim();

        String dieselFilterChange = dieselFilterSwitch.isChecked() ?"Yes":"No";
        String breakOilChange = breakOilSwitch.isChecked() ?"Yes":"No";
        String coolantChange = coolantSwitch.isChecked() ?"Yes":"No";

        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        // Insert data into the database
        boolean isInserted = databaseHelper.saveOilChangeService(recordid,vehicleNo, modelName, currentDate, runningKm,  nextServiceKm, dieselFilterChange, breakOilChange, coolantChange, remark);

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

    // Method to clear the form after submission
    private void clearForm() {
        vehicleNoEditText.setText("");
        modelNameEditText.setText("");
        runningKmEditText.setText("");
        nextServiceKmEditText.setText("");
        remarkEditText.setText("");
        dieselFilterSwitch.setChecked(false);
        breakOilSwitch.setChecked(false);
        coolantSwitch.setChecked(false);
    }
    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0); // Assuming you need the first bundle from the list

        // Set text fields with data from the bundle
        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        runningKmEditText.setText(data.getString("runningKm", ""));
        nextServiceKmEditText.setText(data.getString("nextServiceKm", ""));
        remarkEditText.setText(data.getString("remark", ""));

        // Set switches based on "Yes" or "No" stored in the bundle
        dieselFilterSwitch.setChecked("Yes".equals(data.getString("dieselFilterChange")));
        breakOilSwitch.setChecked("Yes".equals(data.getString("breakOilChange")));
        coolantSwitch.setChecked("Yes".equals(data.getString("coolantChange")));
    }
}
