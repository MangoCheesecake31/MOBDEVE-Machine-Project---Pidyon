package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.viewholders.ContactViewHolder;

import java.util.ArrayList;


public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private Context context;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructor
    public ContactAdapter(ArrayList<Contact> data, Context context) {
        this.data = data;
        this.context = context;
    }

    // Methods
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_add_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bindData(data.get(position), context);
        holder.bindUnfriendButton(v -> {
            removeContact(data.get(position));
            removeRecent(data.get(position));
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    public void removeContact(Contact contact) {
        // Remove Contact of Current User
        firebaseDatabase.child("Contacts").child(uid).get().addOnCompleteListener(task -> {
            for (DataSnapshot dss: task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(contact.getContactID())) {
                    firebaseDatabase.child("Contacts").child(uid).child(dss.getKey()).child("contactID").removeValue();
                    Log.d("PROGRAM-FLOW", "Current User Contact Modified!");
                    break;
                }
            }
        });

        // Remove Contact of Friend
        firebaseDatabase.child("Contacts").child(contact.getContactID()).get().addOnCompleteListener(task -> {
            for (DataSnapshot dss: task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(uid)) {
                    firebaseDatabase.child("Contacts").child(contact.getContactID()).child(dss.getKey()).child("contactID").removeValue();
                    Log.d("PROGRAM-FLOW", "Friendz User Contact Modified!");
                    break;
                }
            }
        });
    }

    public void removeRecent(Contact contact) {
        firebaseDatabase.child("Recent").child(uid).child(contact.getContactID()).child("time").removeValue();
        firebaseDatabase.child("Recent").child(contact.getContactID()).child(uid).child("time").removeValue();
    }
}
