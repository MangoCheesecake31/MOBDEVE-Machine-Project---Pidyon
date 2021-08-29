package com.mobdeve.s11.g25.pidyon.controller;

import android.os.Bundle;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentContactsBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.ContactAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class ContactsFragment extends Fragment {
    // Attributes
    private FragmentContactsBinding binding;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

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
        Log.d("PROGRAM-FLOW", "Viewing Contacts!");

        // Recycler View
        configureRecyclerView(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    // Setup Contact Recycler View
    private void configureRecyclerView(String uid) {
        // Retrieve Contacts
        firebaseDatabase.child(uid).child("Contacts").get().addOnCompleteListener(task -> {


            ArrayList<Contact> data = new ArrayList<>();
//            for (DataSnapshot dss: task.getResult().getChildren()) {
//                String username = dss.child("username").getValue(String.class);
//                String email_address = dss.child("emailAddress").getValue(String.class);
//                String contact_id = dss.child("contactID").getValue(String.class);
//
//                data.add(new Contact(username, email_address, contact_id, "adjifjgodsgodsgods"));
//            }

            // Sort Contacts
//            Collections.sort(data);

            ContactAdapter adapter = new ContactAdapter(data);
            binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
            binding.usersRecyclerView.setAdapter(adapter);
        });
    }
}