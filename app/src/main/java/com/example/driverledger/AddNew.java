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
    int RecordId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new); // Ensure the correct layout is set here

        EdgeToEdge.enable(this);
        // Get the passed id from the intent
        int id = getIntent().getIntExtra("id", 1);
        RecordId= getIntent().getIntExtra("recordId", 0);
        // Default is -1 if id is not passed
        ArrayList<Bundle> bundleList = getIntent().getParcelableArrayListExtra("dataList");
        // Default is -1 if id is not passed

        // Inside your current activity
        ImageView btnBacktoHome = findViewById(R.id.btnBacktoHome);

        btnBacktoHome.setOnClickListener(v -> {
            Intent intent = new Intent(AddNew.this, HomeScreen.class);
            intent.putExtra("id", id);
            startActivity(intent);


            // Finish the current activity
            finish();
        });


        // Based on the id, load the appropriate fragment
        if (id == 1) {
            loadFragment(new OilChangeService(),bundleList,id);
        } else if (id == 2) {
            loadFragment(new OtherMaintenance(),bundleList,id);
        }else if (id == 3) {
            loadFragment(new TyerChange(),bundleList,id);
        }else if (id == 4) {
            loadFragment(new DriverComplaints(),bundleList,id);
        }





    }
    private void loadFragment(Fragment fragment, ArrayList<Bundle> bundleList,int id) {
        Bundle args = new Bundle();
        args.putInt("id",id);
        args.putInt("recordid",RecordId);
        args.putParcelableArrayList("bundleList", bundleList);
        fragment.setArguments(args);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.addNew_fragment_container, fragment)
                .commit();
    }
}