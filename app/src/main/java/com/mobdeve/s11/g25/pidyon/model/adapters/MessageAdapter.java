package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g25.pidyon.model.Message;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageUser;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolder;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolderReceiver;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolderSender;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    // Attributes
    private ArrayList<Message> data;
    private String sender;

    // Constructors
    public MessageAdapter(ArrayList<Message> data, String sender) {
        this.data = data;
        this.sender = sender;
    }

    // Methods
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == MessageUser.SENDER) {
            return new MessageViewHolderSender(itemView);
        } else {
            return new MessageViewHolderReceiver(itemView);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getSender() == this.sender) {
            return MessageUser.SENDER;
        } else {
            return MessageUser.RECEIVER;
        }
    }
}
