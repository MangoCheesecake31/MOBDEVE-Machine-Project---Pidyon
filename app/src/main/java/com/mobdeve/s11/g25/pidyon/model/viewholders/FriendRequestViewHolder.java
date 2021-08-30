package com.mobdeve.s11.g25.pidyon.model.viewholders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Constructors
    public FriendRequestViewHolder(View itemView) {
        super(itemView);

        imageProfile =  itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
        imageAcceptRequest = itemView.findViewById(R.id.imageMessageFriend);
        imageDeclineRequest = itemView.findViewById(R.id.imageRemoveFriend);
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

        // Accept Requests
        imageAcceptRequest.setOnClickListener(v -> {
            buttonPressed();
            // Remove Requests
            removeRequests(contact);
            imageAcceptRequest.setImageResource(R.drawable.ic_photo_black_48dp);

            // Add Contact
            firebaseDatabase.child("Contacts").child(uid).push().child("contactID").setValue(contact.getContactID());
        });

        // Decline Requests
        imageDeclineRequest.setOnClickListener(v -> {
            buttonPressed();
            // Remove Requests
            removeRequests(contact);
            imageDeclineRequest.setImageResource(R.drawable.ic_photo_black_48dp);
        });
    }

    private void removeRequests(Contact contact) {
        // Remove Send Request
        firebaseDatabase.child("Requests").child("Send").child(contact.getContactID()).get().addOnCompleteListener(send_task -> {
            for (DataSnapshot dss: send_task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(uid)) {
                    firebaseDatabase.child("Requests").child("Send").child(contact.getContactID()).child(dss.getKey()).removeValue();
                    Log.d("PROGRAM-FLOW", "Send Request Removed");
                }
            }
        });

        // Remove Receive Request
        firebaseDatabase.child("Requests").child("Receive").child(uid).get().addOnCompleteListener(receive_task -> {
            for (DataSnapshot dss: receive_task.getResult().getChildren()) {
                if (dss.child("contactID").getValue(String.class).equalsIgnoreCase(contact.getContactID())) {
                    firebaseDatabase.child("Requests").child("Receive").child(uid).child(dss.getKey()).removeValue();
                    Log.d("PROGRAM-FLOW", "Receive Request Removed");
                }
            }
        });
    }

    private void buttonPressed() {
        imageAcceptRequest.setClickable(false);
        imageDeclineRequest.setClickable(false);
    }
}
