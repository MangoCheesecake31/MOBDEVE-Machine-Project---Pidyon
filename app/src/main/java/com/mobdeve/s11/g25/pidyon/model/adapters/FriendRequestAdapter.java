package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.viewholders.FriendRequestViewHolder;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructors
    public FriendRequestAdapter(ArrayList<Contact> data) {
        this.data = data;
    }

    // Methods
    @Override
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_friend_request, parent, false);
        return new FriendRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder holder, int position) {
        holder.bindData(data.get(position));
        holder.bindAcceptButton((View.OnClickListener) v -> {
            removeRequests(data.get(position));
            addContact(data.get(position));
            removeAt(position);
        });

        holder.bindDeclineButton((View.OnClickListener) v -> {
            removeRequests(data.get(position));
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    private void removeRequests(Contact contact) {
        // Remove Send Request
        firebaseDatabase.child("Requests").child("Send").child(contact.getContactID()).get().addOnCompleteListener(send_task -> {
            for (DataSnapshot dss: send_task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(uid)) {
                    firebaseDatabase.child("Requests").child("Send").child(contact.getContactID()).child(dss.getKey()).removeValue();
                    Log.d("PROGRAM-FLOW", "Send Request Removed");
                }
            }
        });

        // Remove Receive Request
        firebaseDatabase.child("Requests").child("Receive").child(uid).get().addOnCompleteListener(receive_task -> {
            for (DataSnapshot dss: receive_task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(contact.getContactID())) {
                    firebaseDatabase.child("Requests").child("Receive").child(uid).child(dss.getKey()).removeValue();
                    Log.d("PROGRAM-FLOW", "Receive Request Removed");
                }
            }
        });
    }

    private void addContact(Contact contact) {
        // Add Contact
        firebaseDatabase.child("Contacts").child(uid).push().child("contactID").setValue(contact.getContactID());
        firebaseDatabase.child("Contacts").child(contact.getContactID()).push().child("contactID").setValue(uid);
    }
}
