package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.PidyonMessage;

import java.io.File;
import java.io.IOException;

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
    public void bindData(PidyonMessage message, Context context) {
        this.textMessage.setText(message.getText());
        this.textDateTime.setText(message.getTime());

        // Load Image
        StorageReference storage = FirebaseStorage.getInstance().getReference("user_avatars/" + message.getSender());
        storage.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(context).load(task.getResult()).placeholder(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageProfile);
            } else {
                imageProfile.setImageResource(R.drawable.default_avatar);
            }
        });
    }
}
