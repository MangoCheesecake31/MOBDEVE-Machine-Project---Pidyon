package com.mobdeve.s11.g25.pidyon.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.R;

import java.util.ArrayList;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private Contact user_contact;

    // Constructors
    public AddContactAdapter(ArrayList<Contact> data, Contact user_contact) {
        this.data = data;
        this.user_contact = user_contact;
    }

    // Methods
    @Override
    public AddContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new AddContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddContactViewHolder holder, int position) {
        holder.bindData(data.get(position), user_contact);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
