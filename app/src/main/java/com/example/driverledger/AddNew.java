package com.example.driverledger;

import android.content.Intent;
import android.health.connect.datatypes.Record;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Get the passed id from the intent
        int id = getIntent().getIntExtra("id", 1);
        int RecordId = getIntent().getIntExtra("recordId", 1);
        // Default is -1 if id is not passed
        ArrayList<Bundle> bundleList = getIntent().getParcelableArrayListExtra("dataList");
        // Default is -1 if id is not passed

        // Based on the id, load the appropriate fragment
        if (id == 1) {
            loadFragment(new OilChangeService(),bundleList,id, RecordId);
        } else if (id == 2) {
            loadFragment(new OtherMaintenance(),bundleList,id, RecordId);
        }else if (id == 3) {
            loadFragment(new TyerChange(),bundleList,id, RecordId);
        }else if (id == 4) {
            loadFragment(new DriverComplaints(),bundleList,id, RecordId);
        }


        // Inside your current activity
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddNew.this, HomeScreen.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


    }
    private void loadFragment(Fragment fragment, ArrayList<Bundle> bundleList,int id,int recordid) {
        Bundle args = new Bundle();
        args.putInt("id",id);
        args.putInt("recordid",recordid);
        args.putParcelableArrayList("bundleList", bundleList);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // R.id.fragment_container is the FrameLayout where fragment will be loaded
        fragmentTransaction.commit();
    }
}