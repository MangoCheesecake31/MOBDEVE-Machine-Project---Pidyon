package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.io.File;
import java.io.IOException;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    private RoundedImageView imageProfile;
    private TextView textName;
    private TextView textEmail;
    private AppCompatImageView imageAcceptRequest;
    private AppCompatImageView imageDeclineRequest;

    // Constructors
    public FriendRequestViewHolder(View itemView) {
        super(itemView);

        imageProfile =  itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
        imageAcceptRequest = itemView.findViewById(R.id.imageAcceptFriend);
        imageDeclineRequest = itemView.findViewById(R.id.imageDeclineFriend);
    }

    // Methods
    public void bindData(Contact contact) {
        textName.setText(contact.getUsername());
        textEmail.setText(contact.getEmailAddress());

        StorageReference storage = FirebaseStorage.getInstance().getReference("user_avatars/" + contact.getContactID());
        imageProfile.setImageResource(R.drawable.default_avatar);

        // Retrieve Profile Avatar
        try {
            final File file = File.createTempFile(contact.getContactID(), "jpeg");
            storage.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageProfile.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindAcceptButton(View.OnClickListener accept) {
        imageAcceptRequest.setOnClickListener(accept);
    };

    public void bindDeclineButton(View.OnClickListener decline) {
        imageDeclineRequest.setOnClickListener(decline);
    }
}
