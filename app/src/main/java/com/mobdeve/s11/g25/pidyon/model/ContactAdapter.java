package com.mobdeve.s11.g25.pidyon.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    // Attributes
    private ArrayList<Contact> data;

    // Constructor
    public ContactAdapter(ArrayList<Contact> data) {
        this.data = data;
    }

    // Methods
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
