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

    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        // Add Friend
        imageAddFriend.setOnClickListener(v -> {
            imageAddFriend.setClickable(false);
            imageBlockUser.setClickable(false);
            imageAddFriend.setImageResource(R.drawable.background_chat_input);

            // Update Database (Send Requests)
            firebaseDatabase.child("Requests").child("Send").child(uid).push().child("contactID").setValue(contact.getContactID());
            firebaseDatabase.child("Requests").child("Receive").child(contact.getContactID()).push().child("contactID").setValue(uid);
        });

        // Block User
        imageBlockUser.setOnClickListener(v -> {
            imageAddFriend.setClickable(false);
            imageBlockUser.setClickable(false);
            imageBlockUser.setImageResource(R.drawable.background_chat_input);

            // Update Database (Blocked Users)
            firebaseDatabase.child("Blocks").child("Blocking").child(uid).push().child("contactID").setValue(contact.getContactID());
            firebaseDatabase.child("Blocks").child("BlockedBy").child(contact.getContactID()).push().child("contactID").setValue(uid);
        });
    }
}
