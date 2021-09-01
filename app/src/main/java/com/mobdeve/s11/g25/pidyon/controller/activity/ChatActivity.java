package com.mobdeve.s11.g25.pidyon.controller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityChatBinding;
import com.mobdeve.s11.g25.pidyon.model.PidyonMessage;
import com.mobdeve.s11.g25.pidyon.model.adapters.MessageAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String receive_user;
    private String uid_A;
    private String uid_B;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private MessageAdapter adapter;
    private boolean loaded = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderUIDS();
        configureRecyclerView();
        setListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        // Back
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Info?
        binding.imageInfo.setOnClickListener(v -> {
            Toast.makeText(this.getApplicationContext(), "UWU!", Toast.LENGTH_LONG).show();
        });

        // Send
        binding.layoutSend.setOnClickListener(v -> {
            binding.layoutSend.setClickable(false);

            // Retrieve Inputs
            String message = binding.inputMessage.getEditableText().toString().trim();

            // Validate Data
            if (message.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Please enter a message!", Toast.LENGTH_LONG);
                binding.layoutSend.setClickable(true);
                return;
            }

            // Get Time
            LocalDateTime ldt = LocalDateTime.now().plusDays(1);
            DateTimeFormatter format = DateTimeFormatter.ofPattern("mm:ss", Locale.ENGLISH);
            String time = format.format(ldt);

            // Send Message
            firebaseDatabase.child("Chats").child(uid_A).child(uid_B).push().setValue(new PidyonMessage(message, current_user, receive_user, time)).addOnCompleteListener(send_task -> {
                if (send_task.isSuccessful()) {
                    binding.layoutSend.setClickable(true);
                    binding.inputMessage.setText("");
                }
            });
        });
    }

    private void configureRecyclerView() {
        firebaseDatabase.child("Chats").child(uid_A).child(uid_B).addValueEventListener(new ValueEventListener() {
            // Retrieve All Messages
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot message_snapshot) {
                // Initial
                if (!loaded) {
                    ArrayList<PidyonMessage> data = new ArrayList<>();
                    for (DataSnapshot dss: message_snapshot.getChildren()) {
                        String text = dss.child("text").getValue(String.class);
                        String sender = dss.child("sender").getValue(String.class);
                        String receiver = dss.child("receiver").getValue(String.class);
                        String time = dss.child("time").getValue(String.class);

                        data.add(new PidyonMessage(text, sender, receiver, time));
                    }

                    // Setup RecyclerView
                    adapter = new MessageAdapter(data, getApplicationContext());
                    binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    binding.chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    binding.chatRecyclerView.setAdapter(adapter);
                    binding.chatRecyclerView.scrollToPosition(data.size() - 1);
                    loaded = true;

                // Ongoing
                } else {
                    Iterable<DataSnapshot> dss = message_snapshot.getChildren();
                    ArrayList<DataSnapshot> data = new ArrayList<>();
                    dss.forEach(data::add);
                    DataSnapshot new_message = data.get((int) (message_snapshot.getChildrenCount() - 1));

                    String text = new_message.child("text").getValue(String.class);
                    String sender = new_message.child("sender").getValue(String.class);
                    String receiver = new_message.child("receiver").getValue(String.class);
                    String time = new_message.child("time").getValue(String.class);

                    adapter.addMessageView(new PidyonMessage(text, sender, receiver, time));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("PROGRAM-FLOW", "Retrieving Messages Cancelled!");
            }
        });
    }

    private void orderUIDS() {
        // Order UIDS
        receive_user = getIntent().getStringExtra("CONTACT_ID");
        uid_A = (current_user.compareTo(receive_user) < 0) ? current_user : receive_user;
        uid_B = (current_user.compareTo(receive_user) < 0) ? receive_user : current_user;
    }
}