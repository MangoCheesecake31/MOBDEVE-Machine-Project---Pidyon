package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.model.Contact;

import java.io.File;
import java.io.IOException;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    protected RoundedImageView imageProfile;
    protected TextView textName;
    protected TextView textEmail;

    // Constructor
    public ContactViewHolder(View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
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
}
