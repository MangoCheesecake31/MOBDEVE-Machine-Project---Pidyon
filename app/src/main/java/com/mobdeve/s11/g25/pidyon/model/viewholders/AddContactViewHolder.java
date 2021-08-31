package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.io.File;
import java.io.IOException;

public class AddContactViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    protected TextView textName;
    protected TextView textEmail;
    protected RoundedImageView imageProfile;
    protected AppCompatImageView imageAddFriend;
    protected AppCompatImageView imageBlockUser;

    // Constructor
    public AddContactViewHolder(View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
        imageAddFriend = itemView.findViewById(R.id.imageAddFriend);
        imageBlockUser = itemView.findViewById(R.id.imageBlockUser);
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

    public void bindAddFriendButton(View.OnClickListener add) {
        imageAddFriend.setOnClickListener(add);
    }

    public void bindBlockUserButton(View.OnClickListener block) {
        imageBlockUser.setOnClickListener(block);
    }
}
