package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.model.Contact;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    protected TextView textName;


    // Constructors
    public ChatViewHolder(View itemView) {
        super(itemView);
    }

    // Methods
    public void bindData(Contact contact) {

    }
}
