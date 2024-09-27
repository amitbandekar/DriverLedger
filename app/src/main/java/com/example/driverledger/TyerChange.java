package com.example.driverledger;

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
import java.util.Date;
import java.util.Locale;

public class TyerChange extends Fragment {

    private EditText vehicleNoEditText, modelNameEditText, tyreQtyEditText, alignmentKmEditText, nextAlignmentKmEditText, remarkEditText;
    private Switch alignmentBalancingSwitch;
    private TextView vehicleNoValidationText, modelNameValidationText, tyreQtyValidationText, alignmentKmValidationText, remarkValidationText;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oil_change_service, container, false);

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
            int id =0;

            boolean isSaved = dbHelper.saveTyreChangeData(id,vehicleNo, modelName, tyreQty, alignmentBalancing, alignmentKm, nextAlignmentKm, remark,currentDateTime);
            if (isSaved) {
                Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error saving data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
