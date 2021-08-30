package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Message;

public class MessageViewHolderReceiver extends MessageViewHolder {
    // Attributes
    private TextView textMessage;
    private TextView textDateTime;
    private RoundedImageView imageProfile;

    // Constructors
    public MessageViewHolderReceiver(final View view) {
        super(view);

        textMessage = view.findViewById(R.id.textMessage);
        textDateTime = view.findViewById(R.id.textDateTime);
        imageProfile = view.findViewById(R.id.imageProfile);
    }

    // Methods
    @Override
    public void bindData(Message message) {
        this.textMessage.setText(message.getText());

        // TODO: Set Time & Image Profile

    }
}
