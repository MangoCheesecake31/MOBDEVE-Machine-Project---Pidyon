package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.viewholders.AddContactViewHolder;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.util.ArrayList;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructors
    public AddContactAdapter(ArrayList<Contact> data) {
        this.data = data;
    }

    // Methods
    @Override
    public AddContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_add_contact, parent, false);
        return new AddContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddContactViewHolder holder, int position) {
        holder.bindData(data.get(position));
        holder.bindAddFriendButton((View.OnClickListener) v -> {
            addFriend(data.get(position));
            removeAt(position);
        });

        holder.bindBlockUserButton((View.OnClickListener) v -> {
            blockUser(data.get(position));
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

    private void addFriend(Contact contact) {
        firebaseDatabase.child("Requests").child("Send").child(uid).push().child("contactID").setValue(contact.getContactID());
        firebaseDatabase.child("Requests").child("Receive").child(contact.getContactID()).push().child("contactID").setValue(uid);
    };

    private void blockUser(Contact contact) {
        firebaseDatabase.child("Blocks").child("Blocking").child(uid).push().child("contactID").setValue(contact.getContactID());
        firebaseDatabase.child("Blocks").child("BlockedBy").child(contact.getContactID()).push().child("contactID").setValue(uid);
    }
}
