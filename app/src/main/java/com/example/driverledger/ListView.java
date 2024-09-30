package com.example.driverledger;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListView extends Fragment {
    private DatabaseHelper databaseHelper;

    private LinearLayout cardListLayout;
    private ImageView loadingImageView;
    private TextView noRecordsTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout with the ScrollView and LinearLayout
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        databaseHelper = new DatabaseHelper(requireContext());

        // Find the LinearLayout where dynamic cards will be added
        loadingImageView = rootView.findViewById(R.id.loadingImageView);
        noRecordsTextView = rootView.findViewById(R.id.noRecordsTextView);
        cardListLayout = rootView.findViewById(R.id.cardListLayout);

        // Get the passed data from arguments
        if (getArguments() != null) {
            loadingImageView.setVisibility(View.VISIBLE);
            noRecordsTextView.setVisibility(View.GONE);
            int viewId = getArguments().getInt("ViewId", -1); // -1 is the default value if no id is found
            ArrayList<HashMap<String, String>> dataList = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("dataList");
            if (dataList != null && !dataList.isEmpty()) {
                if (viewId == 1) {
                    // Loop through each record in the dataList and create card views
                    for (HashMap<String, String> dataMap : dataList) {
                        // Inflate card layout
                        View cardView = inflater.inflate(R.layout.cardlayout_servicingdetails, cardListLayout, false);

                        int recordId = Integer.parseInt((String) dataMap.get("id"));

                        // Find and set data to TextViews
                        TextView tvVehicleNo = cardView.findViewById(R.id.tvVehicleNo);
                        TextView tvModelName = cardView.findViewById(R.id.tvModelName);
                        TextView tvRunningKm = cardView.findViewById(R.id.tvRunningKm);
                        TextView tvNextServiceKm = cardView.findViewById(R.id.tvNextServiceKm);
                        TextView tvDate = cardView.findViewById(R.id.tvDate);
                        ImageView ivDelete = cardView.findViewById(R.id.ivDelete);

                        // Set data from dataMap to the TextViews
                        tvVehicleNo.setText("Vehicle No: " + dataMap.get("vehicleNo"));
                        tvModelName.setText("Model: " + dataMap.get("modelName"));
                        tvRunningKm.setText("Running KM: " + dataMap.get("runningKm"));
                        tvNextServiceKm.setText("Next Service KM: " + dataMap.get("nextServiceKm"));
                        tvDate.setText("Date: " + dataMap.get("currentDateTime"));

                        // Handle delete action (e.g., remove from database or array)
                        ivDelete.setOnClickListener(v -> {
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure?")
                                    .setContentText("You won't be able to recover this record!")
                                    .setConfirmText("Yes, delete it!")
                                    .setCancelText("No, cancel!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Handle the delete action, for example: deleteItem();
                                            databaseHelper.DeleteRecordById("tblServicingDetails",recordId);
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Optional: Handle the cancel action if needed
                                            // For example: Toast.makeText(DriverDetails.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show(); // Implement delete logic here, such as calling delete method with recordId
                        });
                        cardView.setOnClickListener(v -> {
                            // Passing the id (can be any value or fetched from an object)

                            // Start new activity and pass the id
                            Intent intent = new Intent(requireContext(), ViewData.class);
                            intent.putExtra("id", viewId);
                            intent.putExtra("recordId", recordId);
                            startActivity(intent);
                        });
                        // Add the dynamically created card view to the LinearLayout
                        cardListLayout.addView(cardView);
                    }

                } else if (viewId == 2) {

                    // Loop through each record in the dataList and create card views
                    for (HashMap<String, String> dataMap : dataList) {
                        // Inflate card layout
                        View cardView = inflater.inflate(R.layout.cardlayout_maintenancedetails, cardListLayout, false);
                        int recordId = Integer.parseInt((String) dataMap.get("id"));

                        // Find and set data to TextViews
                        TextView tvVehicleNo = cardView.findViewById(R.id.tvVehicleNo);
                        TextView tvModelName = cardView.findViewById(R.id.tvModelName);
                        TextView tvDetails = cardView.findViewById(R.id.tvDetails);
                        TextView tvDate = cardView.findViewById(R.id.tvDate);
                        ImageView ivDelete = cardView.findViewById(R.id.ivDelete);

                        // Set data from dataMap to the TextViews
                        tvVehicleNo.setText("Vehicle No: " + dataMap.get("vehicleNo"));
                        tvModelName.setText("Model: " + dataMap.get("modelName"));
                        tvDetails.setText("Details: " + dataMap.get("details"));
                        tvDate.setText("Date: " + dataMap.get("currentDateTime"));

                        // Handle delete action (e.g., remove from database or array)
                        ivDelete.setOnClickListener(v -> {

                            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure?")
                                    .setContentText("You won't be able to recover this record!")
                                    .setConfirmText("Yes, delete it!")
                                    .setCancelText("No, cancel!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Handle the delete action, for example: deleteItem();
                                            databaseHelper.DeleteRecordById("tblMaintenanceDetails",recordId);
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Optional: Handle the cancel action if needed
                                            // For example: Toast.makeText(DriverDetails.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();// Implement delete logic here, such as calling delete method with recordId
                        });

                        cardView.setOnClickListener(v -> {
                            // Passing the id (can be any value or fetched from an object)

                            // Start new activity and pass the id
                            Intent intent = new Intent(requireContext(), ViewData.class);
                            intent.putExtra("viewId", viewId);
                            intent.putExtra("recordId", recordId);
                            startActivity(intent);
                        });
                        // Add the dynamically created card view to the LinearLayout
                        cardListLayout.addView(cardView);
                    }

                } else if (viewId == 3) {

                    // Loop through each record in the dataList and create card views
                    for (HashMap<String, String> dataMap : dataList) {
                        // Inflate card layout
                        View cardView = inflater.inflate(R.layout.cardlayout_tyrerepairs, cardListLayout, false);
                        int recordId = Integer.parseInt((String) dataMap.get("id"));

                        // Find and set data to TextViews
                        TextView tvVehicleNo = cardView.findViewById(R.id.tvVehicleNo);
                        TextView tvModelName = cardView.findViewById(R.id.tvModelName);
                        TextView tvTyreQty = cardView.findViewById(R.id.tvTyreQty);
                        TextView tvAlignmentKm = cardView.findViewById(R.id.tvAlignmentKm);
                        TextView tvDate = cardView.findViewById(R.id.tvDate);
                        ImageView ivDelete = cardView.findViewById(R.id.ivDelete);

                        // Set data from dataMap to the TextViews
                        tvVehicleNo.setText("Vehicle No: " + dataMap.get("vehicleNo"));
                        tvModelName.setText("Model: " + dataMap.get("modelName"));
                        tvTyreQty.setText("Tyre Quantity: " + dataMap.get("tyreQty"));
                        tvAlignmentKm.setText("Alignment KM: " + dataMap.get("alignmentKm"));
                        tvDate.setText("Date: " + dataMap.get("currentDateTime"));

                        // Handle delete action (e.g., remove from database or array)
                        ivDelete.setOnClickListener(v -> {

                            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure?")
                                    .setContentText("You won't be able to recover this record!")
                                    .setConfirmText("Yes, delete it!")
                                    .setCancelText("No, cancel!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Handle the delete action, for example: deleteItem();
                                            databaseHelper.DeleteRecordById("tblTyreRepairs",recordId);
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Optional: Handle the cancel action if needed
                                            // For example: Toast.makeText(DriverDetails.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();// Implement delete logic here, such as calling delete method with recordId
                        });

                        cardView.setOnClickListener(v -> {
                            // Passing the id (can be any value or fetched from an object)

                            // Start new activity and pass the id
                            Intent intent = new Intent(requireContext(), ViewData.class);
                            intent.putExtra("viewId", viewId);
                            intent.putExtra("recordId", recordId);
                            startActivity(intent);
                        });
                        // Add the dynamically created card view to the LinearLayout
                        cardListLayout.addView(cardView);
                    }


                } else if (viewId == 4) {

                    // Loop through each complaint record and create card views
                    for (HashMap<String, String> complaintMap : dataList) {
                        // Inflate the card layout
                        View cardView = inflater.inflate(R.layout.cardlayout_drivercomplaint, cardListLayout, false);
                        int recordId = Integer.parseInt((String) complaintMap.get("id"));

                        // Find and set data to TextViews
                        TextView tvVehicleNo = cardView.findViewById(R.id.tvVehicleNo);
                        TextView tvModelName = cardView.findViewById(R.id.tvModelName);
                        TextView tvDetails = cardView.findViewById(R.id.tvDetails);
                        TextView tvRemarks = cardView.findViewById(R.id.tvRemarks);
                        TextView tvDate = cardView.findViewById(R.id.tvDate);
                        ImageView ivDelete = cardView.findViewById(R.id.ivDelete);

                        // Set data from complaintMap to the TextViews
                        tvVehicleNo.setText("Vehicle No: " + complaintMap.get("vehicleNo"));
                        tvModelName.setText("Model: " + complaintMap.get("modelName"));
                        tvDetails.setText("Details: " + complaintMap.get("details"));
                        tvRemarks.setText("Remarks: " + complaintMap.get("remarks"));
                        tvDate.setText("Date: " + complaintMap.get("currentDateTime"));

                        // Handle delete action (e.g., remove from database or array)
                        ivDelete.setOnClickListener(v -> {
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure?")
                                    .setContentText("You won't be able to recover this record!")
                                    .setConfirmText("Yes, delete it!")
                                    .setCancelText("No, cancel!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Handle the delete action, for example: deleteItem();
                                            databaseHelper.DeleteRecordById("tblDriverComplaints",recordId);
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            // Optional: Handle the cancel action if needed
                                            // For example: Toast.makeText(DriverDetails.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();// Implement delete logic here, such as calling delete method with recordId
                        });
                        cardView.setOnClickListener(v -> {
                            // Passing the id (can be any value or fetched from an object)

                            // Start new activity and pass the id
                            Intent intent = new Intent(requireContext(), ViewData.class);
                            intent.putExtra("viewId", viewId);
                            intent.putExtra("recordId", recordId);
                            startActivity(intent);
                        });
                        // Add the dynamically created card view to the parent layout
                        cardListLayout.addView(cardView);
                    }


                }
            }
            else {
                loadingImageView.setVisibility(View.GONE);
                noRecordsTextView.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }
}

