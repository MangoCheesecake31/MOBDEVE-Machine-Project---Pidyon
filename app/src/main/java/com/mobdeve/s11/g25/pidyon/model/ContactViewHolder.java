package com.mobdeve.s11.g25.pidyon.model;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mobdeve.s11.g25.pidyon.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    // Attributes
    private RoundedImageView imageProfile;
    private TextView textName;
    private TextView textEmail;

    // Constructor
    public ContactViewHolder(View itemView) {
        super(itemView);

        imageProfile = itemView.findViewById(R.id.imageProfile);
        textName = itemView.findViewById(R.id.textName);
        textEmail = itemView.findViewById(R.id.textEmail);
    }
}
