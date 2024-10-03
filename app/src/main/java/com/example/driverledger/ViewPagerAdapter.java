package com.example.driverledger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final int[] viewIds = {1, 2, 3, 4, 5}; // ViewId for each position

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create fragment and set arguments
        Fragment fragment;
        if (position == 4) { // Profile position
            fragment = new Profile();
        } else {
            fragment = new ListView();
        }

        // Pass ViewId to fragment
        Bundle args = new Bundle();
        args.putInt("ViewId", viewIds[position]);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return viewIds.length;
    }
}
