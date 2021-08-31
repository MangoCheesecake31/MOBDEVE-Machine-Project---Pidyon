package com.mobdeve.s11.g25.pidyon.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentBlockedUsersBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.adapters.BlockUserAdapter;
import com.mobdeve.s11.g25.pidyon.model.adapters.FriendRequestAdapter;

import java.util.ArrayList;

public class BlockedUsersFragment extends Fragment {
    // Attributes
    private FragmentBlockedUsersBinding binding;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    // Constructors
    public BlockedUsersFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBlockedUsersBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        // Retrieve Blocked Users
        firebaseDatabase.child("Blocks").child("Blocking").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot blocking_snapshot) {
                ArrayList<String> blocked_users = new ArrayList<>();
                ArrayList<Contact> data = new ArrayList<>();

                for (DataSnapshot dss: blocking_snapshot.getChildren()) {
                    blocked_users.add(dss.child("contactID").getValue(String.class));
                }

                // Retrieve Contact Objects
                firebaseDatabase.child("Users").get().addOnCompleteListener(users_task -> {
                    for (DataSnapshot dssu : users_task.getResult().getChildren()) {
                        if (blocked_users.contains(dssu.getKey())) {
                            DataSnapshot profile = dssu.child("Profile");
                            String username = profile.child("username").getValue(String.class);
                            String email_address = profile.child("emailAddress").getValue(String.class);
                            String contact_id = profile.child("contactID").getValue(String.class);
                            String token = profile.child("token").getValue(String.class);

                            data.add(new Contact(username, email_address, contact_id, token));
                        }
                    }

                    // Setup RecyclerView
                    BlockUserAdapter adapter = new BlockUserAdapter(data);
                    binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    binding.usersRecyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("PROGRAM-FLOW", "Retrieving Blocked Users Cancelled!");
            }
        });
    }
 }