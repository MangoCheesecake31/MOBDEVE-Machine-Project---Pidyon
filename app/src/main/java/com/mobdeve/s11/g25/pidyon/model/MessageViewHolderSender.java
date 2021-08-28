package com.mobdeve.s11.g25.pidyon.model;

import android.view.View;
import android.widget.TextView;

import com.mobdeve.s11.g25.pidyon.R;

public class MessageViewHolderSender extends MessageViewHolder {
    // Attributes
    private TextView textMessage;
    private TextView textDateTime;

    // Constructors
    public MessageViewHolderSender(final View view) {
        super(view);

        textMessage = view.findViewById(R.id.textMessage);
        textDateTime = view.findViewById(R.id.textDateTime);
    }

    // Methods
    @Override
    public void bindData(Message message) {
        textMessage.setText(message.getText());

    }
}
