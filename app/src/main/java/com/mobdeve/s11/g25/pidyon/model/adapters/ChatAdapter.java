package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.viewholders.ChatViewHolder;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    // Attributes
    private ArrayList<Contact> data;
    private Context context;

    // Constructors
    public ChatAdapter(ArrayList<Contact> data, Context context) {
        this.data = data;
        this.context = context;
    }

    // Methods
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_add_contact, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bindData(data.get(position), context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
