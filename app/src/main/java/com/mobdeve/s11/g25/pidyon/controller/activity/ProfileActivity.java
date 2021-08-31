package com.mobdeve.s11.g25.pidyon.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.mobdeve.s11.g25.pidyon.controller.adapters.ProfileFragmentAdapter;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityProfileInformationBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureTabLayout();
        setListeners();
    }

    private void configureTabLayout() {
        FragmentManager fm = getSupportFragmentManager();
        ProfileFragmentAdapter adapter = new ProfileFragmentAdapter(fm, getLifecycle());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    private void setListeners() {
        // Switch Tabs
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}