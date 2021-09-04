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
import com.mobdeve.s11.g25.pidyon.databinding.FragmentChatsBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.ContactTimeComparator;
import com.mobdeve.s11.g25.pidyon.model.adapters.ChatAdapter;
import com.mobdeve.s11.g25.pidyon.model.adapters.ContactAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatsFragment extends Fragment {
    // Attributes
    private FragmentChatsBinding binding;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    // Constructors
    public ChatsFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        firebaseDatabase.child("Recent").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot chat_snapshot) {
                ArrayList<String> chats = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();
                ArrayList<Contact> data = new ArrayList<>();

                for (DataSnapshot dss: chat_snapshot.getChildren()) {
                    chats.add(dss.getKey());
                    times.add(dss.child("time").getValue(String.class));
                }

                // Retrieve Contact Objects
                firebaseDatabase.child("Users").get().addOnCompleteListener(users_task -> {
                    for (DataSnapshot dssu: users_task.getResult().getChildren()) {
                        if (chats.contains(dssu.getKey())) {
                            DataSnapshot profile = dssu.child("Profile");
                            String username = profile.child("username").getValue(String.class);
                            String email_address = profile.child("emailAddress").getValue(String.class);
                            String contact_id = profile.child("contactID").getValue(String.class);
                            String token = profile.child("token").getValue(String.class);

                            Contact contact = new Contact(username, email_address, contact_id, token);
                            contact.setLatest_chat_time(times.get(chats.indexOf(dssu.getKey())));

                            data.add(contact);
                        }
                    }

                    // Sort Contacts by Recent
                    Collections.sort(data, new ContactTimeComparator());

                    // Setup RecyclerView
                    ChatAdapter adapter = new ChatAdapter(data, getActivity());
                    binding.conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    binding.conversationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    binding.conversationsRecyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("PROGRAM-FLOW", "Retrieving Recent Chats Cancelled");
            }
        });
    }

}