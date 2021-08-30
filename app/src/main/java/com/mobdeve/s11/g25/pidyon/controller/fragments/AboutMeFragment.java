package com.mobdeve.s11.g25.pidyon.controller.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.controller.EditProfileActivity;
import com.mobdeve.s11.g25.pidyon.controller.ImageSaver;
import com.mobdeve.s11.g25.pidyon.databinding.FragmentAboutMeBinding;

public class AboutMeFragment extends Fragment {
    // Attributes
    private FragmentAboutMeBinding binding;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("user_avatars/" + uid);

    // Constructors
    public AboutMeFragment() {

    }

    // Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bitmap bitmap = new ImageSaver(this.getActivity()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").load();
        if (bitmap != null) {
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutMeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadProfile();
        setListeners();
    }

    // Load Profile related Views
    private void loadProfile() {
        SharedPreferences sp = this.getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        binding.textUsername.setText(sp.getString("USERNAME", ""));
        binding.textEmail.setText(sp.getString("EMAIL_ADDRESS", ""));

        binding.imageProfile.setImageResource(R.drawable.default_avatar);
        Bitmap bitmap = new ImageSaver(this.getActivity().getApplicationContext()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").load();
        if (bitmap != null) {
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private void setListeners() {
        binding.textEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this.getActivity(), EditProfileActivity.class));
        });
    }
}