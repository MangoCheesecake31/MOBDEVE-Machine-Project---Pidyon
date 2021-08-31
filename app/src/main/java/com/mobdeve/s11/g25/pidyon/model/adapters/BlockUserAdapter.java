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
import com.mobdeve.s11.g25.pidyon.model.viewholders.BlockedUserViewHolder;

import java.util.ArrayList;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockedUserViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    // Constructors
    public BlockUserAdapter(ArrayList<Contact> data) {
        this.data = data;
    }

    // Methods
    @Override
    public BlockedUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_blocked_user, parent, false);
        return new BlockedUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BlockedUserViewHolder holder, int position) {
        holder.bindData(data.get(position));
        holder.bindUnblockButton(v -> {
            unblockUser(data.get(position));
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

    private void unblockUser(Contact contact) {
        // Unblock User
        firebaseDatabase.child("Blocks").child("Blocking").child(uid).get().addOnCompleteListener(blocking_task -> {
           for (DataSnapshot dss: blocking_task.getResult().getChildren()) {
               if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(contact.getContactID())) {
                   firebaseDatabase.child("Blocks").child("Blocking").child(uid).child(dss.getKey()).removeValue();
                   Log.d("PROGRAM-FLOW", "User Unblocked A!");
               }
           }
        });

        // UnBlockBy User
        firebaseDatabase.child("Blocks").child("BlockedBy").child(contact.getContactID()).get().addOnCompleteListener(blockedby_task -> {
            for (DataSnapshot dss: blockedby_task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(uid)) {
                    firebaseDatabase.child("Blocks").child("BlockedBy").child(contact.getContactID()).child(dss.getKey()).removeValue();
                    Log.d("PROGRAM-FLOW", "User Unblocked B!");
                }
            }
        });
    }
}
