package com.mobdeve.s11.g25.pidyon.controller.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentFriendRequestsBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.adapters.FriendRequestAdapter;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {
    // Attributes
    private FragmentFriendRequestsBinding binding;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructors
    public FriendRequestsFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        configureRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendRequestsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        // Retrieve Requests
        firebaseDatabase.child("Requests").child("Receive").child(uid).get().addOnCompleteListener(receive_task -> {
            ArrayList<String> requests = new ArrayList<>();
            ArrayList<Contact> data = new ArrayList<>();

            for (DataSnapshot dssr: receive_task.getResult().getChildren()) {
                requests.add(dssr.child("contactID").getValue(String.class));
            }

            // Retrieve Contact Objects
            firebaseDatabase.child("Users").get().addOnCompleteListener(users_task -> {
                for (DataSnapshot dssu : users_task.getResult().getChildren()) {
                    if (requests.contains(dssu.getKey())) {
                        DataSnapshot profile = dssu.child("Profile");
                        String username = profile.child("username").getValue(String.class);
                        String email_address = profile.child("emailAddress").getValue(String.class);
                        String contact_id = profile.child("contactID").getValue(String.class);
                        String token = profile.child("token").getValue(String.class);

                        data.add(new Contact(username, email_address, contact_id, token));
                    }
                }

                // Setup RecyclerView
                FriendRequestAdapter adapter = new FriendRequestAdapter(data);
                binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                binding.usersRecyclerView.setAdapter(adapter);
            });
        });
    }
}