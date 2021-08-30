package com.mobdeve.s11.g25.pidyon.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityMainBinding;


import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainFragmentAdapter adapter;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Profile");
    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("user_avatars/" + uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("PROGRAM-FLOW", "User: " + uid + " in session");

        if (retrieveProfile()) {
            Log.d("PROGRAM-FLOW", "Retrieved Profile!");
            configureTabLayout();
            setListeners();
        } else {
            Log.d("PROGRAM-FLOW", "Retrieving Profile Failed!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update Avatar & Username if Changes were made
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        binding.textName.setText(sp.getString("USERNAME", ""));
        Bitmap bitmap = new ImageSaver(getApplicationContext()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").load();
        if (bitmap != null) {
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private boolean retrieveProfile() {
        Log.d("PROGRAM-FLOW", "Retrieving Profile from Database...");
        firebaseDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot result = task.getResult();
                String username = result.child("username").getValue(String.class);
                String email_address = result.child("emailAddress").getValue(String.class);
                String token = result.child("token").getValue(String.class);
                binding.textName.setText(username);

                // SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Retrieve Profile Avatar
                binding.imageProfile.setImageResource(R.drawable.default_avatar);
                try {
                    final File file = File.createTempFile(uid, "jpeg");
                    firebaseStorage.getFile(file).addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        binding.imageProfile.setImageBitmap(bitmap);

                        // Save to Internal Storage
                        new ImageSaver(getApplicationContext()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").save(bitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    editor.putString("USERNAME", username);
                    editor.putString("EMAIL_ADDRESS", email_address);
                    editor.putString("TOKEN", token);
                    editor.apply();
                }
            }
        });
        Log.d("PROGRAM-FLOW", "?" + getSharedPreferences("User", MODE_PRIVATE).getString("USERNAME", ""));
        return !getSharedPreferences("User", MODE_PRIVATE).getString("USERNAME", "").isEmpty();
    }

    private void setListeners() {
        // Switching Tabs
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

        // Signing Out
        binding.imageSignOut.setOnClickListener(v -> {
            Log.d("PROGRAM-FLOW", "User Signing Out!");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        // Adding Contacts
        binding.fabNewChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });

        // Profile Page
        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void configureTabLayout() {
        FragmentManager fm = getSupportFragmentManager();
        adapter = new MainFragmentAdapter(fm, getLifecycle());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(Task<String> task) {
//                if (!task.isSuccessful()) {
//                    return;
//                }
//
//                String token = task.getResult();
//
//
//                Log.d("TEST", token);
//            }
//        });