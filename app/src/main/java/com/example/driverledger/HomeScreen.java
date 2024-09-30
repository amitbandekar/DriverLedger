package com.example.driverledger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeScreen extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private static final String[] TableNames = { "tblServicingDetails", "tblMaintenanceDetails", "tblTyreRepairs", "tblDriverComplaints" };
    int ViewId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        databaseHelper = new DatabaseHelper(this); // Correct context
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        ImageView addnew = findViewById(R.id.addIcon);
        ImageView btnExportPdf = findViewById(R.id.btnExportPdf);

        ViewId = getIntent().getIntExtra("id", 1);
        // Load the default fragment (Driver Details)
        loadFragment(new ListView());

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_oil_change:
                        fragment = new ListView();
                        ViewId = 1;
                        break;
                    case R.id.nav_maintenance:
                        fragment = new ListView();
                        ViewId = 2;
                        break;
                    case R.id.nav_tyre_change:
                        fragment = new ListView();
                        ViewId = 3;
                        break;
                    case R.id.nav_driver_complaints:
                        fragment = new ListView();
                        ViewId = 4;
                        break;
                }

                if (fragment != null) {
                    loadFragment(fragment);
                }
                return true;
            }
        });


        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeScreen.this, AddNew.class);
                intent.putExtra("id", ViewId);
                startActivity(intent);
            }
        });
        btnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFHandler pdfExporter = new PDFHandler(HomeScreen.this);
                pdfExporter.importDataFromPDF(TableNames[ViewId - 1], HomeScreen.this);

            }
        });
        btnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFHandler pdfExporter = new PDFHandler(HomeScreen.this);
                pdfExporter.exportDataToPDF(TableNames[ViewId - 1], HomeScreen.this);

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
}
