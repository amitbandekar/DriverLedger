package com.example.driverledger;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtherMaintenance extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, detailsEditText, dateTimePicker;
    private TextView vehicleNoValidationText, modelNameValidationText, detailsValidationText;
    private Button submitButton;
    private DatabaseHelper databaseHelper;
    int recordid = 0;
    int id = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_maintenance, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize UI components
        initializeViews(view);

        // Set current date and time
        setCurrentDateTime();

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Bundle> bundleList = args.getParcelableArrayList("bundleList");
            recordid = args.getInt("recordid");
            id = args.getInt("id");
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

        // Setup DateTime Picker
        setupDateTimePicker();

        return view;
    }

    private void initializeViews(View view) {
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        detailsEditText = view.findViewById(R.id.detailsEditText);
        dateTimePicker = view.findViewById(R.id.otherMaintenanceDateTime); // Add this EditText to your layout
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        detailsValidationText = view.findViewById(R.id.detailsValidationText);
        submitButton = view.findViewById(R.id.submitButton);
    }

    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy hh:mm a", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        dateTimePicker.setText(currentDateAndTime);
    }

    private void setupDateTimePicker() {
        dateTimePicker.setOnClickListener(v -> showDateTimePicker());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                            (view1, hourOfDay, minute) -> {
                                Calendar selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy hh:mm a", Locale.getDefault());
                                dateTimePicker.setText(sdf.format(selectedDateTime.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (TextUtils.isEmpty(vehicleNoEditText.getText().toString().trim())) {
            vehicleNoValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            vehicleNoValidationText.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(modelNameEditText.getText().toString().trim())) {
            modelNameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            modelNameValidationText.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(detailsEditText.getText().toString().trim())) {
            detailsValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            detailsValidationText.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void saveData() {
        String vehicleNo = vehicleNoEditText.getText().toString().trim();
        String modelName = modelNameEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();
        String selectedDateTime = dateTimePicker.getText().toString();

        boolean isInserted = databaseHelper.saveOtherMaintenanceData(recordid, vehicleNo, modelName, details, selectedDateTime);

        if (isInserted) {
            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Data saved successfully")
                    .show();
            Intent intent = new Intent(getContext(), HomeScreen.class);
            intent.putExtra("id", id);
            startActivity(intent);
            requireActivity().finish();
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
        setCurrentDateTime();
    }

    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0);
        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        detailsEditText.setText(data.getString("details", ""));

        String savedDateTime = data.getString("currentDateTime", "");
        if (!savedDateTime.isEmpty()) {
            dateTimePicker.setText(savedDateTime);
        } else {
            setCurrentDateTime();
        }
    }
}