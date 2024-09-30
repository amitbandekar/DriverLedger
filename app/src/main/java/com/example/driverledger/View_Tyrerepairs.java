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

public class View_Tyrerepairs extends Fragment {

    private TextView vehicleNoTextView;
    private TextView modelNameTextView;
    private TextView tyreQtyTextView;
    private TextView alignmentBalancingTextView;
    private TextView alignmentKmTextView;
    private TextView nextAlignmentKmTextView;
    private TextView remarkTextView;
    private TextView dateTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view__tyrerepairs, container, false);

        // Initialize TextViews
        vehicleNoTextView = view.findViewById(R.id.vehicleNoTextView);
        modelNameTextView = view.findViewById(R.id.modelNameTextView);
        tyreQtyTextView = view.findViewById(R.id.tyreQtyTextView);
        alignmentBalancingTextView = view.findViewById(R.id.alignmentBalancingTextView);
        alignmentKmTextView = view.findViewById(R.id.alignmentKmTextView);
        nextAlignmentKmTextView = view.findViewById(R.id.nextAlignmentKmTextView);
        remarkTextView = view.findViewById(R.id.remarkTextView);
        dateTextView = view.findViewById(R.id.dateTextView);

        // Get data from bundle (passed from the activity)
        if (getArguments() != null) {
            int viewId = getArguments().getInt("ViewId");
            int recordId = getArguments().getInt("RecordId");
            ArrayList<HashMap<String, String>> dataList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("dataList");

            // Extract the relevant data from the dataList
            if (dataList != null && !dataList.isEmpty()) {
                HashMap<String, String> dataMap = dataList.get(0); // Assuming only one record

                // Set values to TextViews
                vehicleNoTextView.setText(dataMap.get("vehicleNo"));
                modelNameTextView.setText(dataMap.get("modelName"));
                tyreQtyTextView.setText(dataMap.get("tyreQty"));
                alignmentBalancingTextView.setText(dataMap.get("alignmentBalancing"));
                alignmentKmTextView.setText(dataMap.get("alignmentKm"));
                nextAlignmentKmTextView.setText(dataMap.get("nextAlignmentKm"));
                remarkTextView.setText(dataMap.get("remark"));
                dateTextView.setText(dataMap.get("currentDateTime"));
            }
        }

        return view;
    }
}
