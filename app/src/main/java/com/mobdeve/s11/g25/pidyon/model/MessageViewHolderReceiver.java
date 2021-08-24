package com.mobdeve.s11.g25.pidyon.model;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;

public class MessageViewHolderReceiver extends MessageViewHolder {

    private TextView textMessage;
    private TextView textDateTime;
    private RoundedImageView imageProfile;

    public MessageViewHolderReceiver(final View view) {
        super(view);

        textMessage = view.findViewById(R.id.textMessage);
        textDateTime = view.findViewById(R.id.textDateTime);
        imageProfile = view.findViewById(R.id.imageProfile);
    }

    @Override
    public void bindDate(Message message) {
        this.textMessage.setText(message.getText());

        // TODO: Set Time & Image Profile

    }
}
