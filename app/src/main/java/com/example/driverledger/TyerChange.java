package com.example.driverledger;

import android.content.Intent;
import android.os.Bundle;
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

public class TyerChange extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, tyreQtyEditText, alignmentKmEditText, nextAlignmentKmEditText, remarkEditText;
    private Switch alignmentBalancingSwitch;
    private TextView vehicleNoValidationText, modelNameValidationText, tyreQtyValidationText, alignmentKmValidationText, remarkValidationText;
    private DatabaseHelper dbHelper;
    int recordid =0;
    int id =1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tyer_change, container, false);

        // Initialize Views
        vehicleNoEditText = view.findViewById(R.id.vehicleNoEditText);
        modelNameEditText = view.findViewById(R.id.modelNameEditText);
        tyreQtyEditText = view.findViewById(R.id.tyreQtyEditText);
        alignmentKmEditText = view.findViewById(R.id.alignmentKmEditText);
        nextAlignmentKmEditText = view.findViewById(R.id.nextAlignmentKmEditText);
        remarkEditText = view.findViewById(R.id.remarkEditText);
        alignmentBalancingSwitch = view.findViewById(R.id.alignmentBalancingSwitch);

        // Validation Text Views
        vehicleNoValidationText = view.findViewById(R.id.vehicleNoValidationText);
        modelNameValidationText = view.findViewById(R.id.modelNameValidationText);
        tyreQtyValidationText = view.findViewById(R.id.tyreQtyValidationText);
        alignmentKmValidationText = view.findViewById(R.id.alignmentKmValidationText);
        remarkValidationText = view.findViewById(R.id.remarkValidationText);

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(getContext());
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
        // Handle Submit Button
        Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> saveTyreChangeData());

        return view;
    }

    private void saveTyreChangeData() {
        // Reset validation messages
        vehicleNoValidationText.setVisibility(View.GONE);
        modelNameValidationText.setVisibility(View.GONE);
        tyreQtyValidationText.setVisibility(View.GONE);
        alignmentKmValidationText.setVisibility(View.GONE);
        remarkValidationText.setVisibility(View.GONE);

        // Get values from inputs
        String vehicleNo = vehicleNoEditText.getText().toString();
        String modelName = modelNameEditText.getText().toString();
        String tyreQtyStr = tyreQtyEditText.getText().toString();
        String alignmentKmStr = alignmentKmEditText.getText().toString();
        String nextAlignmentKmStr = nextAlignmentKmEditText.getText().toString();
        String remark = remarkEditText.getText().toString();
        String alignmentBalancing = alignmentBalancingSwitch.isChecked()  ?"Yes":"No";

        // Validate inputs
        boolean isValid = true;

        if (vehicleNo.isEmpty()) {
            vehicleNoValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (modelName.isEmpty()) {
            modelNameValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (tyreQtyStr.isEmpty()) {
            tyreQtyValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (alignmentKmStr.isEmpty()) {
            alignmentKmValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (remark.isEmpty()) {
            remarkValidationText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (isValid) {
            int tyreQty = Integer.parseInt(tyreQtyStr);
            int alignmentKm = Integer.parseInt(alignmentKmStr);
            int nextAlignmentKm = Integer.parseInt(nextAlignmentKmStr);
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            boolean isSaved = dbHelper.saveTyreChangeData(recordid,vehicleNo, modelName, tyreQty, alignmentBalancing, alignmentKm, nextAlignmentKm, remark,currentDateTime);
            if (isSaved) {
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
    }
    private void clearForm() {
        vehicleNoEditText.setText("");
        modelNameEditText.setText("");
        tyreQtyEditText.setText("");
        alignmentKmEditText.setText("");
        nextAlignmentKmEditText.setText("");
        remarkEditText.setText("");
        alignmentBalancingSwitch.setChecked(false);

    }
    private void SetData(ArrayList<Bundle> bundleList) {
        Bundle data = bundleList.get(0); // Assuming you need the first bundle from the list

        // Set text fields with data from the bundle
        vehicleNoEditText.setText(data.getString("vehicleNo", ""));
        modelNameEditText.setText(data.getString("modelName", ""));
        tyreQtyEditText.setText(data.getString("tyreQty", ""));
        alignmentKmEditText.setText(data.getString("alignmentKm", ""));
        remarkEditText.setText(data.getString("remark", ""));
        nextAlignmentKmEditText.setText(data.getString("nextAlignmentKm", ""));

        // Set switches based on "Yes" or "No" stored in the bundle
        alignmentBalancingSwitch.setChecked("Yes".equals(data.getString("alignmentBalancing")));

    }
}
