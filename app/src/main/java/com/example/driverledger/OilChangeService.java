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
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OilChangeService extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, runningKmEditText, nextServiceKmEditText,
            remarkEditText, dateTimePicker;
    private TextView vehicleNoValidationText, modelNameValidationText, kmValidationText, remarkValidationText;
    private Switch dieselFilterSwitch, breakOilSwitch, coolantSwitch;
    private Button submitButton;
    private DatabaseHelper databaseHelper;
    int recordid = 0;
    int id = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oil_change_service, container, false);

        initializeViews(view);
        setCurrentDateTime();
        setupDateTimePicker();

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<Bundle> bundleList = args.getParcelableArrayList("bundleList");
            recordid = args.getInt("recordid");
            id = args.getInt("id");
            if (bundleList != null && bundleList.size() != 0) {
                SetData(bundleList);
            } else {
                clearForm();
            }
        }

        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                saveData();
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize UI components
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        runningKmEditText = view.findViewById(R.id.runningKmEditText);
        nextServiceKmEditText = view.findViewById(R.id.nextServiceKmEditText);
        remarkEditText = view.findViewById(R.id.remarkEditText);
        dateTimePicker = view.findViewById(R.id.OilchangedateTime);
        dieselFilterSwitch = view.findViewById(R.id.dieselFilterSwitch);
        breakOilSwitch = view.findViewById(R.id.breakOilSwitch);
        coolantSwitch = view.findViewById(R.id.coolantSwitch);

        // Validation Text Views
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        kmValidationText = view.findViewById(R.id.kmValidationText);
        remarkValidationText = view.findViewById(R.id.remarkValidationText);

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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

        if (TextUtils.isEmpty(nextServiceKmEditText.getText().toString().trim()) ||
                TextUtils.isEmpty(runningKmEditText.getText().toString().trim())) {
            kmValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            kmValidationText.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(remarkEditText.getText().toString().trim())) {
            remarkValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            remarkValidationText.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void saveData() {
        String vehicleNo = vehicleNoEditText.getText().toString().trim();
        String modelName = modelNameEditText.getText().toString().trim();
        String runningKm = runningKmEditText.getText().toString().trim();
        String nextServiceKm = nextServiceKmEditText.getText().toString().trim();
        String remark = remarkEditText.getText().toString().trim();
        String currentDateTime = dateTimePicker.getText().toString();

        String dieselFilterChange = dieselFilterSwitch.isChecked() ? "Yes" : "No";
        String breakOilChange = breakOilSwitch.isChecked() ? "Yes" : "No";
        String coolantChange = coolantSwitch.isChecked() ? "Yes" : "No";

        boolean isInserted = databaseHelper.saveOilChangeService(recordid, vehicleNo, modelName,
                currentDateTime, runningKm, nextServiceKm, dieselFilterChange,
                breakOilChange, coolantChange, remark);

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
        runningKmEditText.setText("");
        nextServiceKmEditText.setText("");
        remarkEditText.setText("");
        setCurrentDateTime(); // Reset datetime to current
        dieselFilterSwitch.setChecked(false);
        breakOilSwitch.setChecked(false);
        coolantSwitch.setChecked(false);
    }

    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0);

        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        runningKmEditText.setText(data.getString("runningKm", ""));
        nextServiceKmEditText.setText(data.getString("nextServiceKm", ""));
        remarkEditText.setText(data.getString("remark", ""));

        // Set the datetime from the bundle, or current time if not available
        String savedDateTime = data.getString("currentDateTime", "");
        if (!savedDateTime.isEmpty()) {
            dateTimePicker.setText(savedDateTime);
        } else {
            setCurrentDateTime();
        }

        dieselFilterSwitch.setChecked("Yes".equals(data.getString("dieselFilterChange")));
        breakOilSwitch.setChecked("Yes".equals(data.getString("breakOilChange")));
        coolantSwitch.setChecked("Yes".equals(data.getString("coolantChange")));
    }
}