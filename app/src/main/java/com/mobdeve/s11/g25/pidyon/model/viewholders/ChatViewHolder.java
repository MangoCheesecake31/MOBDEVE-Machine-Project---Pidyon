package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.controller.activity.ChatActivity;
import com.mobdeve.s11.g25.pidyon.model.Contact;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    private TextView textName;
    private TextView textEmail;
    private RoundedImageView imageProfile;
    private AppCompatImageView button_A;
    private AppCompatImageView button_B;

    // Constructors
    public ChatViewHolder(View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
        button_A = itemView.findViewById(R.id.imageAddFriend);
        button_B = itemView.findViewById(R.id.imageBlockUser);
    }

    // Methods
    public void bindData(Contact contact, Context context) {
        textName.setText(contact.getUsername());
        textEmail.setVisibility(View.INVISIBLE);
        button_A.setVisibility(View.INVISIBLE);
        button_B.setVisibility(View.INVISIBLE);

        // Load Image
        StorageReference storage = FirebaseStorage.getInstance().getReference("user_avatars/" + contact.getContactID());
        storage.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(context).load(task.getResult()).placeholder(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageProfile);
            } else {
                imageProfile.setImageResource(R.drawable.default_avatar);
            }
        });

        View.OnClickListener chat_event = v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("USERNAME", contact.getUsername());
            intent.putExtra("CONTACT_ID", contact.getContactID());
            context.startActivity(intent);
        };

        textName.setOnClickListener(chat_event);
        textEmail.setOnClickListener(chat_event);
        imageProfile.setOnClickListener(chat_event);
        button_A.setOnClickListener(chat_event);
        button_B.setOnClickListener(chat_event);
    }
}
