package com.mobdeve.s11.g25.pidyon.controller.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentFriendRequestsBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.adapters.FriendRequestAdapter;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {
    // Attributes
    private FragmentFriendRequestsBinding binding;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FriendRequestAdapter adapter;
    private boolean loaded;

    // Constructors
    public FriendRequestsFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        firebaseDatabase.child("Requests").child("Receive").child(uid).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot request_snapshot) {
                // Initial
                if (!loaded) {
                    ArrayList<String> requests = new ArrayList<>();
                    ArrayList<Contact> data = new ArrayList<>();

                    for (DataSnapshot dss: request_snapshot.getChildren()) {
                        requests.add(dss.child("contactID").getValue(String.class));
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
                        adapter = new FriendRequestAdapter(data);
                        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        binding.usersRecyclerView.setAdapter(adapter);
                        loaded = true;
                    });
                // Ongoing
                } else {
                    Iterable<DataSnapshot> dss = request_snapshot.getChildren();
                    ArrayList<DataSnapshot> data = new ArrayList<>();
                    dss.forEach(data::add);

                    if (binding.usersRecyclerView.getAdapter().getItemCount() < data.size()) {
                        DataSnapshot new_request = data.get((int) (data.size() - 1));
                        String contact_uid = new_request.child("contactID").getValue(String.class);

                        // Get Contact Object
                        firebaseDatabase.child("Users").child(new_request.child("contactID").getValue(String.class)).child("Profile").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DataSnapshot profile = task.getResult();
                                String username = profile.child("username").getValue(String.class);
                                String email_address = profile.child("emailAddress").getValue(String.class);
                                String contact_id = profile.child("contactID").getValue(String.class);
                                String token = profile.child("token").getValue(String.class);

                                adapter.addRequestView(new Contact(username, email_address, contact_id, token));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("PROGRAM-FLOW", "Retrieving Requests Cancelled!");
            }
        });
    }
}