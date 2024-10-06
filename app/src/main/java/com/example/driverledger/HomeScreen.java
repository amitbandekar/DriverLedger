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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeScreen extends AppCompatActivity {
    private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
    int ViewId = 1;
    private static final int PICK_PDF_REQUEST = 1; // Define your request code here
    private PDFHandler pdfHandler;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private ImageView searchIcon;
    private ImageView closesearchbarIcon;
    private androidx.appcompat.widget.SearchView searchView;
    private TextView titleTextView;
    ImageView addnew,btnExportPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        pdfHandler = new PDFHandler(this); // Initialize PDFHandler with context
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        addnew = findViewById(R.id.addIcon);
        btnExportPdf = findViewById(R.id.btnExportPdf);
        searchIcon = findViewById(R.id.searchIcon);
        closesearchbarIcon = findViewById(R.id.closesearchbarIcon);
        searchView = findViewById(R.id.searchView);
        titleTextView = findViewById(R.id.titleTextView);

        setupViewPager(adapter);
        setupBottomNavigation();
        handleIncomingIntent();

        addnew.setOnClickListener(v -> {

            Intent intent = new Intent(HomeScreen.this, AddNew.class);
            intent.putExtra("id", ViewId);              // Pass the id
            intent.putExtra("recordId", 0);             // Pass recordId as 0
            intent.putParcelableArrayListExtra("dataList", null);  // Pass bundleList as null
            startActivity(intent);
            finish();
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

        // Set up search functionality
        searchIcon.setOnClickListener(v -> toggleSearchBar());
        closesearchbarIcon.setOnClickListener(v -> toggleSearchBar());

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query,adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText,adapter);
                return true;
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

    private void setupViewPager(ViewPagerAdapter adapter) {
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
        searchIcon.setVisibility(position == 4 ? View.GONE : View.VISIBLE);
        //btnImport.setVisibility(position == 4 ? View.GONE : View.VISIBLE);
        searchView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                titleTextView.setText("Oil Change");
                ViewId =1;
                break;
            case 1:
                titleTextView.setText("Other Maintainance");
                ViewId =2;
                break;
            case 2:
                titleTextView.setText("Tyer Change");
                ViewId =3;
                break;
            case 3:
                titleTextView.setText("Driver Complaints");
                ViewId =4;
                break;
            case 4:
                titleTextView.setText("Profile");
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

    private void toggleSearchBar() {
        if (searchView.getVisibility() == View.VISIBLE) {
            // Hide the search bar
            searchView.setVisibility(View.GONE);
            searchIcon.setVisibility(View.VISIBLE);
            closesearchbarIcon.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            btnExportPdf.setVisibility(View.VISIBLE);
            addnew.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
        } else {
            // Show the search bar
            searchIcon.setVisibility(View.GONE);
            closesearchbarIcon.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.GONE);
            btnExportPdf.setVisibility(View.GONE);
            addnew.setVisibility(View.GONE);
            searchView.requestFocus();
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


    private void performSearch(String query, ViewPagerAdapter adapter) {
        Fragment currentFragment = adapter.getFragment(viewPager.getCurrentItem());
        if (currentFragment instanceof ListView) {
            ((ListView) currentFragment).filterData(query,ViewId);
        }
    }
    public class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            // Initialize fragments
            for (int i = 1; i <= 4; i++) {
                ListView listViewFragment = new ListView();
                Bundle args = new Bundle();
                args.putInt("ViewId", i);
                listViewFragment.setArguments(args);
                fragments.add(listViewFragment);
            }
            fragments.add(new Profile());
        }

        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public Fragment getFragment(int position) {
            if (position >= 0 && position < fragments.size()) {
                return fragments.get(position);
            }
            return null;
        }
    }
}
