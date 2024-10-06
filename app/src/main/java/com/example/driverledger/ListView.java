package com.example.driverledger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListView extends Fragment {
    private static final Logger log = LoggerFactory.getLogger(ListView.class);
    private DatabaseHelper databaseHelper;
    private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
    private VehicleDataAdapter adapter;

    private RecyclerView recyclerView;
    private ImageView loadingImageView;
    private TextView noRecordsTextView;
    private List<HashMap<String, String>> originalDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout with the ScrollView and LinearLayout
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        databaseHelper = new DatabaseHelper(requireContext());

        // Find the LinearLayout where dynamic cards will be added
        loadingImageView = rootView.findViewById(R.id.loadingImageView);
        noRecordsTextView = rootView.findViewById(R.id.noRecordsTextView);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new VehicleDataAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        loadData();


        return rootView;
    }


/*

    private void loadData(LayoutInflater inflater) {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        cardListLayout.removeAllViews();

        // Get the passed data from arguments
        if (getArguments() != null) {
            loadingImageView.setVisibility(View.VISIBLE);
            noRecordsTextView.setVisibility(View.GONE);
            int viewId = getArguments().getInt("ViewId", 1); // -1 is the default value if no id is found
            Cursor cursor = databaseHelper.getAllData(TableNames[viewId - 1]);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> dataMap = new HashMap<>();
                    for (int colIndex = 0; colIndex < cursor.getColumnCount(); colIndex++) {
                        String columnName = cursor.getColumnName(colIndex);
                        String columnValue = cursor.getString(colIndex);
                        dataMap.put(columnName, columnValue);
                    }
                    dataList.add(dataMap);
                } while (cursor.moveToNext());
            }

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
                                            boolean success = databaseHelper.DeleteRecordById(TableNames[viewId-1],recordId);
                                            if (success) {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success!")
                                                        .setContentText("Record deleted successfully!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            // Refresh the current fragment
                                                            loadData(inflater);
                                                        })
                                                        .show();

                                            } else {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error!")
                                                        .setContentText("Failed to delete the record.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                                                        .show();
                                            }                                        }
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
                                            boolean success = databaseHelper.DeleteRecordById(TableNames[viewId-1],recordId);
                                            if (success) {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success!")
                                                        .setContentText("Record deleted successfully!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            // Refresh the current fragment
                                                            loadData(inflater);
                                                        })
                                                        .show();
                                            } else {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error!")
                                                        .setContentText("Failed to delete the record.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                                                        .show();
                                            }                                        }
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
                            intent.putExtra("id", viewId);
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
                                            boolean success = databaseHelper.DeleteRecordById(TableNames[viewId-1],recordId);
                                            if (success) {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success!")
                                                        .setContentText("Record deleted successfully!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            // Refresh the current fragment
                                                            loadData(inflater);
                                                        })
                                                        .show();
                                            } else {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error!")
                                                        .setContentText("Failed to delete the record.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                                                        .show();
                                            }                                        }
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
                            intent.putExtra("id", viewId);
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
                                            boolean success = databaseHelper.DeleteRecordById(TableNames[viewId-1],recordId);
                                            if (success) {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success!")
                                                        .setContentText("Record deleted successfully!")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            // Refresh the current fragment
                                                            loadData(inflater);
                                                        })
                                                        .show();

                                            } else {
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error!")
                                                        .setContentText("Failed to delete the record.")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                                                        .show();
                                            }
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
                            intent.putExtra("id", viewId);
                            intent.putExtra("recordId", recordId);
                            startActivity(intent);
                        });
                        // Add the dynamically created card view to the parent layout
                        cardListLayout.addView(cardView);
                    }


                }
                loadingImageView.setVisibility(View.GONE);
                noRecordsTextView.setVisibility(View.GONE);
            }
            else {
                loadingImageView.setVisibility(View.GONE);
                noRecordsTextView.setVisibility(View.VISIBLE);
            }
        }
    }
*/


    private void loadData() {
        if (getArguments() != null) {
            loadingImageView.setVisibility(View.VISIBLE);
            noRecordsTextView.setVisibility(View.GONE);

            int viewId = getArguments().getInt("ViewId", 1);
            Cursor cursor = databaseHelper.getAllData(TableNames[viewId - 1]);

            List<HashMap<String, String>> dataList = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> dataMap = new HashMap<>();
                    for (int colIndex = 0; colIndex < cursor.getColumnCount(); colIndex++) {
                        String columnName = cursor.getColumnName(colIndex);
                        String columnValue = cursor.getString(colIndex);
                        dataMap.put(columnName, columnValue);
                    }
                    dataList.add(dataMap);
                } while (cursor.moveToNext());
            }

            if (dataList.isEmpty()) {
                loadingImageView.setVisibility(View.GONE);
                noRecordsTextView.setVisibility(View.VISIBLE);
            } else {
                loadingImageView.setVisibility(View.GONE);
                noRecordsTextView.setVisibility(View.GONE);
                adapter.setData(dataList, viewId);
            }
        }
    }
    public void filterData(String query,int viewId) {
        if (originalDataList == null) {
            originalDataList = new ArrayList<>(adapter.getDataList());
        }

        List<HashMap<String, String>> filteredList = new ArrayList<>();
        for (HashMap<String, String> data : originalDataList) {
            if (dataMatchesQuery(data, query)) {
                filteredList.add(data);
            }
        }

        adapter.setData(filteredList, viewId);
    }

    private boolean dataMatchesQuery(HashMap<String, String> data, String query) {
        for (String value : data.values()) {
            if (value.toLowerCase().contains(query.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public  class VehicleDataAdapter extends RecyclerView.Adapter<VehicleDataAdapter.ViewHolder> {

        public List<HashMap<String, String>> getDataList() {
            return dataList;
        }

        private final Context context;
        private List<HashMap<String, String>> dataList;
        private int viewId;

        public VehicleDataAdapter(Context context) {
            this.context = context;
            this.dataList = new ArrayList<>();
        }

        public void setData(List<HashMap<String, String>> dataList, int viewId) {
            this.dataList = dataList;
            this.viewId = viewId;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewId) {
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout_servicingdetails, parent, false);
                    break;
                case 2:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout_maintenancedetails, parent, false);
                    break;
                case 3:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout_tyrerepairs, parent, false);
                    break;
                case 4:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout_drivercomplaint, parent, false);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type");
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, String> dataMap = dataList.get(position);
            holder.bind(dataMap);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvVehicleNo, tvModelName, tvDate;
            ImageView ivDelete;

            // Additional TextViews for specific layouts
            TextView tvRunningKm, tvNextServiceKm;  // For servicing details
            TextView tvDetails;  // For maintenance details
            TextView tvTyreQty, tvAlignmentKm;  // For tyre repairs
            TextView tvRemarks;  // For driver complaints

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvVehicleNo = itemView.findViewById(R.id.tvVehicleNo);
                tvModelName = itemView.findViewById(R.id.tvModelName);
                tvDate = itemView.findViewById(R.id.tvDate);
                ivDelete = itemView.findViewById(R.id.ivDelete);

                // Initialize additional TextViews based on the layout
                switch (viewId) {
                    case 1:
                        tvRunningKm = itemView.findViewById(R.id.tvRunningKm);
                        tvNextServiceKm = itemView.findViewById(R.id.tvNextServiceKm);
                        break;
                    case 2:
                        tvDetails = itemView.findViewById(R.id.tvDetails);

                    case 3:
                        tvTyreQty = itemView.findViewById(R.id.tvTyreQty);
                        tvAlignmentKm = itemView.findViewById(R.id.tvAlignmentKm);
                        break;
                    case 4:
                        tvDetails = itemView.findViewById(R.id.tvDetails);
                        tvRemarks = itemView.findViewById(R.id.tvRemarks);

                        break;
                }
            }

            public void bind(HashMap<String, String> dataMap) {
                tvVehicleNo.setText("Vehicle No: " + dataMap.get("vehicleNo"));
                tvModelName.setText("Model: " + dataMap.get("modelName"));
                tvDate.setText("Date: " + dataMap.get("currentDateTime"));

                int recordId = Integer.parseInt(dataMap.get("id"));

                switch (viewId) {
                    case 1:
                        tvRunningKm.setText("Running KM: " + dataMap.get("runningKm"));
                        tvNextServiceKm.setText("Next Service KM: " + dataMap.get("nextServiceKm"));
                        break;
                    case 2:
                        tvDetails.setText("Details: " + dataMap.get("details"));
                        break;
                    case 3:
                        tvTyreQty.setText("Tyre Quantity: " + dataMap.get("tyreQty"));
                        tvAlignmentKm.setText("Alignment KM: " + dataMap.get("alignmentKm"));
                        break;
                    case 4:
                        tvDetails.setText("Details: " + dataMap.get("details"));
                        tvRemarks.setText("Remarks: " + dataMap.get("remarks"));
                        break;
                }

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ViewData.class);
                    intent.putExtra("id", viewId);
                    intent.putExtra("recordId", recordId);
                    context.startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish(); // Close HomeScreenActivity
                    }
                });

                ivDelete.setOnClickListener(v -> showDeleteConfirmationDialog(recordId));
            }

            private void showDeleteConfirmationDialog(int recordId) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("You won't be able to recover this record!")
                        .setConfirmText("Yes, delete it!")
                        .setCancelText("No, cancel!")
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismissWithAnimation();
                            deleteRecord(recordId);
                        })
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }

            private void deleteRecord(int recordId) {
                boolean success = databaseHelper.DeleteRecordById(TableNames[viewId - 1], recordId);
                if (success) {
                    showSuccessDialog();
                } else {
                    showErrorDialog();
                }
            }

            private void showSuccessDialog() {
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Record deleted successfully!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                            loadData();
                        })
                        .show();
            }

            private void showErrorDialog() {
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error!")
                        .setContentText("Failed to delete the record.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }
        }
    }

}

