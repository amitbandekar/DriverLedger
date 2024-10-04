package com.example.driverledger;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.viewpager2.widget.ViewPager2;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeScreen extends AppCompatActivity {
    private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
    int ViewId = 1;
    private static final int PICK_PDF_REQUEST = 1; // Define your request code here
    private PDFHandler pdfHandler;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    ImageView addnew,btnExportPdf,btnImport;
    private TextView HeadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        pdfHandler = new PDFHandler(this); // Initialize PDFHandler with context

        addnew = findViewById(R.id.addIcon);
        btnExportPdf = findViewById(R.id.btnExportPdf);
        HeadingText = findViewById(R.id.titleTextView);
        btnImport = findViewById(R.id.btnImportPdf);


        setupViewPager();
        setupBottomNavigation();
        handleIncomingIntent();

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
    private void handleIncomingIntent() {
        if (getIntent() != null) {
            ViewId = getIntent().getIntExtra("id", 1);
            navigateToPage(ViewId);
        }
    }
    private void navigateToPage(int viewId) {
        int position;
        switch (viewId) {
            case 1:
                position = 0; // Oil Change
                break;
            case 2:
                position = 1; // Other Maintenance
                break;
            case 3:
                position = 2; // Tyre Change
                break;
            case 4:
                position = 3; // Driver Complaints
                break;
            default:
                position = 0;
                break;
        }

        // Update ViewPager
        viewPager.setCurrentItem(position, false); // false for instant switch

        // Update UI directly (don't rely on callbacks when coming from intent)
        updateUI(position);
    }
    private void setupBottomNavigation() {
        // This is your click listener for bottom navigation
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Determine which page to show based on clicked item
                if (itemId == R.id.nav_oil_change) {
                    viewPager.setCurrentItem(0);
                    updateUI(0);
                    return true;
                } else if (itemId == R.id.nav_maintenance) {
                    viewPager.setCurrentItem(1);
                    updateUI(1);
                    return true;
                } else if (itemId == R.id.nav_tyre_change) {
                    viewPager.setCurrentItem(2);
                    updateUI(2);
                    return true;
                } else if (itemId == R.id.nav_driver_complaints) {
                    viewPager.setCurrentItem(3);
                    updateUI(3);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    viewPager.setCurrentItem(4);
                    updateUI(4);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // This handles swipe gestures and syncs with bottom navigation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update bottom navigation when page is changed by swipe
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.nav_oil_change);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.nav_maintenance);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.nav_tyre_change);
                        break;
                    case 3:
                        bottomNavigation.setSelectedItemId(R.id.nav_driver_complaints);
                        break;
                    case 4:
                        bottomNavigation.setSelectedItemId(R.id.nav_profile);
                        break;
                }
                updateUI(position);
            }
        });
    }

    private void updateUI(int position) {
        // Your existing UI update code
        addnew.setVisibility(position == 4 ? View.GONE : View.VISIBLE);
        btnExportPdf.setVisibility(position == 4 ? View.GONE : View.VISIBLE);
        //btnImport.setVisibility(position == 4 ? View.GONE : View.VISIBLE);

        switch (position) {
            case 0:
                HeadingText.setText("Oil Change");
                ViewId =1;
                break;
            case 1:
                HeadingText.setText("Other Maintainance");
                ViewId =2;
                break;
            case 2:
                HeadingText.setText("Tyer Change");
                ViewId =3;
                break;
            case 3:
                HeadingText.setText("Driver Complaints");
                ViewId =4;
                break;
            case 4:
                HeadingText.setText("Profile");
                ViewId =5;
                break;
        }
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
