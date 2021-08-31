package com.mobdeve.s11.g25.pidyon.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentContactsBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.adapters.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;


public class ContactsFragment extends Fragment {
    // Attributes
    private FragmentContactsBinding binding;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructors
    public ContactsFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
    }

    // Setup Contact Recycler View
    private void configureRecyclerView() {
        // Retrieve Contacts
        firebaseDatabase.child("Contacts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot contacts_snapshot) {
                ArrayList<String> contacts = new ArrayList<>();
                ArrayList<Contact> data = new ArrayList<>();

                for (DataSnapshot dss: contacts_snapshot.getChildren()) {
                    contacts.add(dss.child("contactID").getValue(String.class));
                }

                // Retrieve Contact Objects
                firebaseDatabase.child("Users").get().addOnCompleteListener(users_task -> {
                    for (DataSnapshot dssu: users_task.getResult().getChildren()) {
                        if (contacts.contains(dssu.getKey())) {
                            DataSnapshot profile = dssu.child("Profile");
                            String username = profile.child("username").getValue(String.class);
                            String email_address = profile.child("emailAddress").getValue(String.class);
                            String contact_id = profile.child("contactID").getValue(String.class);
                            String token = profile.child("token").getValue(String.class);

                            data.add(new Contact(username, email_address, contact_id, token));
                        }
                    }

                    // Sort Contacts
                    Collections.sort(data);

                    // Setup RecyclerView
                    ContactAdapter adapter = new ContactAdapter(data, getActivity());
                    binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    binding.usersRecyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("PROGRAM-FLOW", "Retrieving User's Contacts Cancelled!");
            }
        });
    }
}