package com.mobdeve.s11.g25.pidyon.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class AddContactViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    protected TextView textName;
    protected TextView textEmail;
    protected RoundedImageView imageProfile;
    protected AppCompatImageView imageAddFriend;

    private ArrayList<Contact> data;
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructor
    public AddContactViewHolder(View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
        imageAddFriend = itemView.findViewById(R.id.imageAddFriend);
    }

    // Methods
    public void bindData(Contact contact, Contact user) {
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
            imageAddFriend.setImageResource(R.drawable.background_chat_input);

            // Update Database (Requests)
            firebaseDatabase.child("Users").child(contact.getContactID()).child("Requests").push().child("contactID").setValue(uid);
            firebaseDatabase.child("Users").child(uid).child("Pending-Requests").push().child("contactID").setValue(contact.getContactID());
        });
    }
}
