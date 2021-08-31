package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.model.PidyonMessage;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    // Constructors
    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    //  Methods
    public abstract void bindData(PidyonMessage message, Context context);
}
