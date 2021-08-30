package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.viewholders.AddContactViewHolder;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.util.ArrayList;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactViewHolder> {
    // Attributes
    private ArrayList<Contact> data;

    // Constructors
    public AddContactAdapter(ArrayList<Contact> data) {
        this.data = data;
    }

    // Methods
    @Override
    public AddContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user, parent, false);
        return new AddContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddContactViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
