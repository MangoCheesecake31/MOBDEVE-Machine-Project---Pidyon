package com.mobdeve.s11.g25.pidyon.model;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    // Constructors
    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    //  Methods
    public abstract void bindData(Message message);
}
