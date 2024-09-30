package com.example.driverledger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class View_Maintenancedetails extends Fragment {

    private TextView vehicleNoTextView;
    private TextView modelNameTextView;
    private TextView detailsTextView;
    private TextView dateTimeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view__maintenancedetails, container, false);

        // Initialize TextViews
        vehicleNoTextView = view.findViewById(R.id.vehicleNoTextView);
        modelNameTextView = view.findViewById(R.id.modelNameTextView);
        detailsTextView = view.findViewById(R.id.detailsTextView);
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView);

        // Get data from bundle (passed from the activity)
        if (getArguments() != null) {
            ArrayList<HashMap<String, String>> dataList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("dataList");

            // Extract the relevant data from the dataList
            if (dataList != null && !dataList.isEmpty()) {
                HashMap<String, String> dataMap = dataList.get(0); // Assuming only one record

                // Set values to TextViews
                vehicleNoTextView.setText(dataMap.get("vehicleNo"));
                modelNameTextView.setText(dataMap.get("modelName"));
                detailsTextView.setText(dataMap.get("details"));
                dateTimeTextView.setText(dataMap.get("currentDateTime"));
            }
        }

        return view;
    }
}
