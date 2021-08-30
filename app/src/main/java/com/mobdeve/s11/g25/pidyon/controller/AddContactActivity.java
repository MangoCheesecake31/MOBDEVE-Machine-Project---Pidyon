package com.mobdeve.s11.g25.pidyon.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityUsersBinding;
import com.mobdeve.s11.g25.pidyon.model.adapters.AddContactAdapter;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class AddContactActivity extends AppCompatActivity {
    private ActivityUsersBinding binding;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
        configureRecyclerView();
    }

    private void setListeners() {
        // Back
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void configureRecyclerView() {
        Log.d("PROGRAM-FLOW", "Retrieving Possible Contacts!");
        // Contains List of Contact IDS not to be shown
        ArrayList<String> exceptions = new ArrayList<>();

        // Contacts to be shown
        ArrayList<Contact> data = new ArrayList<>();

        // Retrieve User Contacts
        firebaseDatabase.child("Contacts").child(uid).get().addOnCompleteListener(contacts_task -> {
            if (contacts_task.isSuccessful()) {
                for (DataSnapshot dss: contacts_task.getResult().getChildren()) {
                    exceptions.add(dss.child("contactID").getValue(String.class));
                }

                // Retrieve User Sent Requests
                firebaseDatabase.child("Requests").child("Send").child(uid).get().addOnCompleteListener(requests_task -> {
                    if (requests_task.isSuccessful()) {
                        for (DataSnapshot dss: requests_task.getResult().getChildren()) {
                            exceptions.add(dss.child("contactID").getValue(String.class));
                        }

                        // Retrieve User Receive Requests
                        firebaseDatabase.child("Requests").child("Receive").child(uid).get().addOnCompleteListener(receive_task -> {
                            if (receive_task.isSuccessful()) {
                                for (DataSnapshot dss: receive_task.getResult().getChildren()) {
                                    exceptions.add(dss.child("contactID").getValue(String.class));
                                }

                                // Retrieve User Blocked Users
                                firebaseDatabase.child("Blocks").child("Blocking").child(uid).get().addOnCompleteListener(blocking_task -> {
                                    if (blocking_task.isSuccessful()) {
                                        for (DataSnapshot dss: blocking_task.getResult().getChildren()) {
                                            exceptions.add(dss.child("contactID").getValue(String.class));
                                        }

                                        // Retrieve Users Blocking Current User
                                        firebaseDatabase.child("Blocks").child("BlockedBy").child(uid).get().addOnCompleteListener(blocked_task -> {
                                            if (blocked_task.isSuccessful()) {
                                                for (DataSnapshot dss: blocked_task.getResult().getChildren()) {
                                                    exceptions.add(dss.child("contactID").getValue(String.class));
                                                }

                                                // Retrieve User Contacts
                                                firebaseDatabase.child("Users").get().addOnCompleteListener(users_task -> {
                                                    if (users_task.isSuccessful()) {
                                                        for (DataSnapshot user: users_task.getResult().getChildren()) {
                                                            DataSnapshot profile = user.child("Profile");
                                                            String contact_id = profile.child("contactID").getValue(String.class);

                                                            if (!contact_id.equalsIgnoreCase(uid) && !exceptions.contains(contact_id)) {
                                                                String username = profile.child("username").getValue(String.class);
                                                                String email_address = profile.child("emailAddress").getValue(String.class);
                                                                String token = profile.child("token").getValue(String.class);

                                                                data.add(new Contact(username, email_address, contact_id, token));
                                                            }
                                                        }

                                                        // Sort Possible Contacts
                                                        Collections.sort(data);

                                                        // Set RecyclerView Adapters
                                                        AddContactAdapter adapter = new AddContactAdapter(data);
                                                        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                        binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                                        binding.usersRecyclerView.setAdapter(adapter);
                                                        binding.progressBar.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}