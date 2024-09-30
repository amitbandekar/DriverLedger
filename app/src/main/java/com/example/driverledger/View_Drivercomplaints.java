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

public class View_Drivercomplaints extends Fragment {

    private TextView vehicleNoTextView;
    private TextView modelNameTextView;
    private TextView detailsTextView;
    private TextView remarksTextView;
    private TextView problemClosedTextView;
    private TextView dateTimeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view__drivercomplaints, container, false);

        // Initialize TextViews
        vehicleNoTextView = view.findViewById(R.id.vehicleNoTextView);
        modelNameTextView = view.findViewById(R.id.modelNameTextView);
        detailsTextView = view.findViewById(R.id.detailsTextView);
        remarksTextView = view.findViewById(R.id.remarksTextView);
        problemClosedTextView = view.findViewById(R.id.problemClosedTextView);
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView);

        // Get data from bundle (passed from the previous activity or fragment)
        if (getArguments() != null) {
            ArrayList<HashMap<String, String>> dataList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("dataList");

            // Extract the relevant data from the dataList
            if (dataList != null && !dataList.isEmpty()) {
                HashMap<String, String> dataMap = dataList.get(0); // Assuming only one record

                // Set the data to respective TextViews
                vehicleNoTextView.setText(dataMap.get("vehicleNo"));
                modelNameTextView.setText(dataMap.get("modelName"));
                detailsTextView.setText(dataMap.get("details"));
                remarksTextView.setText(dataMap.get("remarks"));
                problemClosedTextView.setText(dataMap.get("problemClosed"));
                dateTimeTextView.setText(dataMap.get("currentDateTime"));
            }
        }

        return view;
    }
}
