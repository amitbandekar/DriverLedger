package com.example.driverledger;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeScreen extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
    int ViewId = 1;
    private static final int PICK_PDF_REQUEST = 1; // Define your request code here
    private PDFHandler pdfHandler;

    ImageView addnew,btnExportPdf,btnImport;
    private TextView HeadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        pdfHandler = new PDFHandler(this); // Initialize PDFHandler with context

        databaseHelper = new DatabaseHelper(this); // Correct context
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        addnew = findViewById(R.id.addIcon);
        btnExportPdf = findViewById(R.id.btnExportPdf);
        HeadingText = findViewById(R.id.titleTextView);
        btnImport = findViewById(R.id.btnImportPdf);

        if (getIntent() != null) {
            int ViewId = getIntent().getIntExtra("id", 1);

            if (ViewId == 1) {
                HeadingText.setText("Oil Change");
                bottomNavigation.setSelectedItemId(R.id.nav_oil_change);
            }
            else if (ViewId == 2) {
                HeadingText.setText("Other Maintenance");
                bottomNavigation.setSelectedItemId(R.id.nav_maintenance);
            }
            else if (ViewId == 3) {
                HeadingText.setText("Tyre Change");
                bottomNavigation.setSelectedItemId(R.id.nav_tyre_change);
            }
            else if (ViewId == 4) {
                HeadingText.setText("Driver Complaints");
                bottomNavigation.setSelectedItemId(R.id.nav_driver_complaints);
            }
        }

        // Load the default fragment (Driver Details)
        loadFragment(new ListView());

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                addnew.setVisibility(View.VISIBLE);
                btnExportPdf.setVisibility(View.VISIBLE);
                btnImport.setVisibility(View.VISIBLE);

                switch (item.getItemId()) {
                    case R.id.nav_oil_change:
                        fragment = new ListView();
                        HeadingText.setText("Oil Change");
                        ViewId = 1;
                        break;
                    case R.id.nav_maintenance:
                        fragment = new ListView();
                        HeadingText.setText("Other Maintainance");
                        ViewId = 2;
                        break;
                    case R.id.nav_tyre_change:
                        fragment = new ListView();
                        HeadingText.setText("Tyer Change");
                        ViewId = 3;
                        break;
                    case R.id.nav_driver_complaints:
                        fragment = new ListView();
                        HeadingText.setText("Driver Complaints");
                        ViewId = 4;
                        break;
                    case R.id.nav_profile:
                        fragment = new Profile();
                        HeadingText.setText("Profile");
                        addnew.setVisibility(View.GONE);
                        btnExportPdf.setVisibility(View.GONE);
                        btnImport.setVisibility(View.GONE);
                        break;
                }

                if (fragment != null) {
                    loadFragment(fragment);
                }
                return true;
            }
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);


        // Create the FragmentStateAdapter to manage the fragments
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new ListView(); // Oil Change
                    case 1:
                        return new ListView(); // Other Maintenance
                    case 2:
                        return new ListView(); // Tyre Change
                    case 3:
                        return new ListView(); // Driver Complaints
                    case 4:
                        return new Profile();   // Profile
                    default:
                        return new ListView(); // Default to Oil Change
                }
            }

            @Override
            public int getItemCount() {
                return 5; // Number of tabs
            }
        };

// Attach the adapter to ViewPager2
        viewPager.setAdapter(adapter);
        addnew.setOnClickListener(v -> {

            Intent intent = new Intent(HomeScreen.this, AddNew.class);
            intent.putExtra("id", ViewId);              // Pass the id
            intent.putExtra("recordId", 0);             // Pass recordId as 0
            intent.putParcelableArrayListExtra("dataList", null);  // Pass bundleList as null
            startActivity(intent);

        });
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
                        pdfHandler.exportDataToPDF(TableNames[ViewId - 1], HomeScreen.this);
                    }
                } else {
                    // Android version below 11 (R), check and request WRITE_EXTERNAL_STORAGE permission
                    if (ContextCompat.checkSelfPermission(HomeScreen.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeScreen.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        // If permission is already granted, proceed with exporting
                        PDFHandler pdfExporter = new PDFHandler(HomeScreen.this);
                        pdfExporter.exportDataToPDF(TableNames[ViewId - 1], HomeScreen.this);
                    }
                }
            }
        });


        btnImport.setOnClickListener(new View.OnClickListener() {
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
                        // Permission already granted, proceed with the import function
                        openFilePicker(); // Call the file picker function here
                    }
                } else {
                    // Android version below 11 (R), check and request READ_EXTERNAL_STORAGE permission
                    if (ContextCompat.checkSelfPermission(HomeScreen.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(HomeScreen.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        // If permission is already granted, proceed with the import function
                        openFilePicker(); // Call the file picker function here
                    }
                }
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        Cursor cursor = databaseHelper.getAllData(TableNames[ViewId - 1]); // Adjust index

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

        bundle.putInt("ViewId", ViewId);
        bundle.putSerializable("dataList", dataList);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit(); // Optional: .addToBackStack(null) if needed
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with exporting
                pdfHandler.exportDataToPDF(TableNames[ViewId - 1], HomeScreen.this);
            } else {
                // Permission denied, show SweetAlert dialog
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Permission Denied")
                        .setContentText("Cannot export PDF without permission.")
                        .setConfirmText("OK")
                        .show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pdfHandler.handleFilePicked(requestCode, resultCode, data, this, TableNames[ViewId - 1]); // Call handleFilePicked with necessary parameters
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Set the intent type to PDF
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }



}
