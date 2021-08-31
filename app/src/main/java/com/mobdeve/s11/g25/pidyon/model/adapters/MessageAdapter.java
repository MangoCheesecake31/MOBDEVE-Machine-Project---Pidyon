package com.mobdeve.s11.g25.pidyon.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.PidyonMessage;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageUser;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolder;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolderReceiver;
import com.mobdeve.s11.g25.pidyon.model.viewholders.MessageViewHolderSender;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    // Attributes
    private ArrayList<PidyonMessage> data;
    private String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Context context;

    // Constructors
    public MessageAdapter(ArrayList<PidyonMessage> data, Context context) {
        this.data = data;
        this.context = context;
    }

    // Methods
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == MessageUser.SENDER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
            return new MessageViewHolderSender(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
            return new MessageViewHolderReceiver(itemView);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bindData(data.get(position), context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getSender().equals(current_user)) {
            return MessageUser.SENDER;
        } else {
            return MessageUser.RECEIVER;
        }
    }
}
