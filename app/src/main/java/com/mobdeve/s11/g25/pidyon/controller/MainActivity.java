package com.mobdeve.s11.g25.pidyon.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityMainBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;


import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FragmentAdapter adapter;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Profile");
    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("user_avatars/" + uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureTabLayout();
        configureProfile();
        setListeners();

        Log.d("PROGRAM-FLOW", "User: " + uid + " in session");
    }

    private void setListeners() {
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
            firebaseDatabase.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String username = task.getResult().child("username").getValue(String.class);
                    String email_address = task.getResult().child("emailAddress").getValue(String.class);
                    String token = task.getResult().child("token").getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                    intent.putExtra("USERNAME", username);
                    intent.putExtra("EMAIL_ADDRESS", email_address);
                    intent.putExtra("TOKEN", token);
                    startActivity(intent);

                    Log.d("PROGRAM-FLOW", "Retrieved User Contact!");
                } else {
                    Log.d("PROGRAM-FLOW", "Retrieving User Contact Failed!");
                }
            });
        });

        // Profile Page
        binding.imageProfile.setOnClickListener(v -> {
            firebaseDatabase.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String username = task.getResult().child("username").getValue(String.class);
                    String email_address = task.getResult().child("emailAddress").getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("USERNAME", username);
                    intent.putExtra("EMAIL_ADDRESS", email_address);
                    startActivity(intent);

                    Log.d("PROGRAM-FLOW", "Retrieved User Contact!");
                } else {
                    Log.d("PROGRAM-FLOW", "Retrieving User Contact Failed!");
                }
            });
        });
    }

    private void configureTabLayout() {
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    private void configureProfile() {
        // Retrieve Username
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                binding.textName.setText(snapshot.child("username").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // Retrieve Profile Avatar
        binding.imageProfile.setImageResource(R.drawable.default_avatar);
        try {
            final File file = File.createTempFile(uid, "jpeg");
            firebaseStorage.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                binding.imageProfile.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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