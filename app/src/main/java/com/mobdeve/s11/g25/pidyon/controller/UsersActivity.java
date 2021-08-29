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
import com.mobdeve.s11.g25.pidyon.model.AddContactAdapter;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.util.ArrayList;
import java.util.Collections;

public class UsersActivity extends AppCompatActivity {
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
        Contact user = generateUserContact();

        /*
            Variable Names:

            dsr:    DataSnapshot Requests
            dsc:    DataSnapshot Contacts
            dsnc:   DataSnapshot New Contacts
            dsncp:  DataSnapshot New Contact Profile

            Retrieve List of new Contacts (No Friend Requests Sent)
         */

        firebaseDatabase.child("Users").get().addOnCompleteListener(contacts_requests_task -> {
            if (contacts_requests_task.isSuccessful()) {
                DataSnapshot cr_result = contacts_requests_task.getResult().child(uid);
                ArrayList<String> contacts_requests = new ArrayList<>();

                // Current Contacts
                for (DataSnapshot dsc: cr_result.child("Contacts").getChildren()) {
                    contacts_requests.add(dsc.child("contactID").getValue(String.class));
                }

                // Pending Requests
                for (DataSnapshot dsr: cr_result.child("Pending-Requests").getChildren()) {
                    contacts_requests.add(dsr.child("contactID").getValue(String.class));
                }

                DataSnapshot nc_result = contacts_requests_task.getResult();
                ArrayList<Contact> data = new ArrayList<>();

                // New Contacts
                for (DataSnapshot dsnc: nc_result.getChildren()) {
                    DataSnapshot dsncp = dsnc.child("Profile");
                    String contact_id = dsncp.child("contactID").getValue(String.class);

                    if (!contact_id.equalsIgnoreCase(uid) && !contacts_requests.contains(contact_id)) {
                        String username = dsncp.child("username").getValue(String.class);
                        String email_address = dsncp.child("emailAddress").getValue(String.class);
                        String token = dsncp.child("token").getValue(String.class);

                        data.add(new Contact(username, email_address, contact_id, token));
                    }
                }

                // Sort Possible Contacts
                Collections.sort(data);

                // Set RecyclerView Adapters
                AddContactAdapter adapter = new AddContactAdapter(data, user);
                binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                binding.usersRecyclerView.setAdapter(adapter);
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Create a Contact Object of the Current User in Session
    private Contact generateUserContact() {
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String username = sp.getString("USERNAME", "");
        String email_address = sp.getString("EMAIL_ADDRESS", "");
        String token = sp.getString("TOKEN", "");

        return new Contact(username, email_address, uid, token);
    }
}