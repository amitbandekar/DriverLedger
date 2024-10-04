package com.example.driverledger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewData extends AppCompatActivity {
int id =1;
int RecordId;
private DatabaseHelper databaseHelper;
private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
ImageView btnBack,btnExportPdf,btnEdit;
private PDFHandler pdfHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_data);
        Intent intent = getIntent();

        RecordId = intent.getIntExtra("recordId", -1);
        id = getIntent().getIntExtra("id", 1);
                                                                // Use a default value if not found
        databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getDataById(TableNames[id - 1],RecordId); // Adjust index

        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();


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
        // Retrieve the RecordId from the Intent
        // Create an ArrayList of Bundles
        ArrayList<Bundle> bundleList = new ArrayList<>();
        for (HashMap<String, String> map : dataList) {
            Bundle bundle = new Bundle();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
            bundleList.add(bundle);
        }

        // Based on the id, load the appropriate fragment
        if (id == 1) {
            loadFragment(new View_Servicingdetails(),id,RecordId,dataList);
        } else if (id == 2) {
            loadFragment(new View_Maintenancedetails(),id,RecordId, dataList);
        }else if (id == 3) {
            loadFragment(new View_Tyrerepairs(),id,RecordId, dataList);
        }else if (id == 4) {
            loadFragment(new View_Drivercomplaints(),id,RecordId, dataList);
        }

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewData.this, HomeScreen.class);
                intent.putExtra("id", id);
                startActivity(intent);


                // Finish the current activity
                finish();
            }
        });

        btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewData.this, AddNew.class);
                intent.putExtra("id", id);
                intent.putExtra("recordId", RecordId);
                intent.putParcelableArrayListExtra("dataList", bundleList);
                startActivity(intent);
            }
        });

        btnExportPdf = findViewById(R.id.btnExportPdf);

        pdfHandler = new PDFHandler(this); // Initialize PDFHandler with context

        btnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11 or above
                    if (!Environment.isExternalStorageManager()) {
                        // Request for Manage All Files Access permission
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                        }
                    } else {
                        // Permission already granted, export to PDF
                        pdfHandler.generateVehicleServiceReport(ViewData.this,dataList);
                    }
                } else {
                    // Android version below 11 (R), check and request WRITE_EXTERNAL_STORAGE permission
                    if (ContextCompat.checkSelfPermission(ViewData.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ViewData.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        // If permission is already granted, proceed with exporting
                        pdfHandler.generateVehicleServiceReport(ViewData.this,dataList);
                    }
                }
            }
        });


    }
    private void loadFragment(Fragment fragment, int Viewid, int RecordId, ArrayList<HashMap<String, String>> dataList) {
        Bundle bundle = new Bundle();


        bundle.putInt("ViewId", Viewid);
        bundle.putInt("RecordId", RecordId);
        bundle.putSerializable("dataList", dataList);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit(); // Optional: .addToBackStack(null) if needed
    }

}