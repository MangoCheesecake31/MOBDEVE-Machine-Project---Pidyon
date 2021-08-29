package com.mobdeve.s11.g25.pidyon.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityProfileBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("user_avatars/" + uid);
    private Uri selected_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserData();
        setListeners();
    }

    private void setListeners() {
        // Save Changes
        binding.buttonSaveChanges.setOnClickListener(v -> {
            // Minimize Keyboard & Toggle Progress Views
            minimizeKeyboard();
            toggleProgressMode();

            // Retrieves inputs
            String username = binding.inputName.getEditableText().toString().trim();
            String password = binding.inputCurrentPassword.getEditableText().toString().trim();
            String new_password = binding.inputNewPassword.getEditableText().toString().trim();
            String confirm_new_password = binding.inputConfirmNewPassword.getEditableText().toString().trim();

            // Validate Data
            if (!validateData(username, password, new_password, confirm_new_password)) {
                toggleProgressMode();
                return;
            }

            // Re-Authenticate
            Log.d("PROGRAM-FLOW", "Reauthenticating User!");
            AuthCredential credential = EmailAuthProvider.getCredential(getIntent().getStringExtra("EMAIL_ADDRESS"), password);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(authenticate_task -> {
                if (authenticate_task.isSuccessful()) {
                    Log.d("PROGRAM-FLOW", "User Authenticated!");
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(new_password).addOnCompleteListener(update_task -> {
                        toggleProgressMode();
                        if (update_task.isSuccessful()) {
                            Log.d("PROGRAM-FLOW", "Password Updated!");

                            //  UPDATE USERNAME TODO

                            binding.inputCurrentPassword.setText("");
                            binding.inputNewPassword.setText("");
                            binding.inputConfirmNewPassword.setText("");
                            Toast.makeText(EditProfileActivity.this, "Account Updated!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("PROGRAM-FLOW", "Password Update Failed!");
                        }
                    });
                } else {
                    toggleProgressMode();
                    Log.d("PROGRAM-FLOW", "Authentication Failed!");
                    Toast.makeText(EditProfileActivity.this, "Authentication failed! Check your credentials!", Toast.LENGTH_LONG).show();
                }
            });
        });

        // Cancel Changes
        binding.textCancel.setOnClickListener(v -> {
            onBackPressed();
        });

        // Select Image
        binding.imageProfile.setOnClickListener(v -> {
            // Max Resolution: 1000x1000, Gallery, Croppable, Max Size: 1MB
            ImagePicker.Companion.with(EditProfileActivity.this).maxResultSize(1000, 1000).cropSquare().compress(1024).start();
        });
    }

    // Update Image Profile to selected Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selected_image = data.getData();
        binding.imageProfile.setImageURI(selected_image);
    }

    // Toggles Visibility of Progress Related Views
    private void toggleProgressMode() {
        Log.d("PROGRAM-FLOW", "Toggled Progress Mode!");
        if (binding.buttonSaveChanges.isClickable()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSaveChanges.setVisibility(View.INVISIBLE);
            binding.buttonSaveChanges.setClickable(false);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSaveChanges.setVisibility(View.VISIBLE);
            binding.buttonSaveChanges.setClickable(true);
        }
    }

    // Minimizes Virtual Keyboard
    private void minimizeKeyboard() {
        Log.d("PROGRAM-FLOW", "Minimized Keyboard!");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Load Account Data onto related Views
    private void loadUserData() {
        Log.d("PROGRAM-FLOW", "Loading User Data!");
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        binding.inputName.setText(sp.getString("USERNAME", ""));

        binding.imageProfile.setImageResource(R.drawable.default_avatar);
        Bitmap bitmap = new ImageSaver(getApplicationContext()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").load();
        if (bitmap != null) {
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    // Validate User Data
    private boolean validateData(String username, String password, String new_password, String confirm_new_password) {
        Log.d("PROGRAM-FLOW", "Validating Data!");
        if (username.isEmpty()) {
            binding.inputName.setError("Username is required!");
            binding.inputName.requestFocus();
            return false;
        }

        if (Character.isDigit(username.charAt(0))) {
            binding.inputName.setError("Username must start with a letter!");
            binding.inputName.requestFocus();
            return false;
        }

        if (username.length() < 6) {
            binding.inputName.setError("Minimum username length is 6!");
            binding.inputName.requestFocus();
            return false;
        }

        String username_regex = "^[A-Za-z][A-Za-z0-9_]{5,29}$";
        if (!Pattern.compile(username_regex).matcher(username).matches()) {
            binding.inputName.setError("Username can only contain letters and numbers!");
            binding.inputName.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.inputCurrentPassword.setError("Password is required!");
            binding.inputCurrentPassword.requestFocus();
            return false;
        }

        if (new_password.isEmpty()) {
            binding.inputNewPassword.setError("Enter a password!");
            binding.inputNewPassword.requestFocus();
            return false;
        }

        if (new_password.length() < 6) {
            binding.inputNewPassword.setError("Minimum password length is 6!");
            binding.inputNewPassword.requestFocus();
            return false;
        }

        if (!new_password.equals(confirm_new_password)) {
            binding.inputConfirmNewPassword.requestFocus();
            binding.inputConfirmNewPassword.setError("Password confirmation does not match!");
            return false;
        }
        return true;
    }

    // Update Image
    private void updateImage() {
        Log.d("PROGRAM-FLOW", "Updating Image!");
        // No Image
        if (selected_image == null) {
            Log.d("PROGRAM-FLOW", "User has No New Selected Image!");
            return;
        }

        // Filename: user_avatars/<User ID>
        firebaseStorage.putFile(selected_image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PROGRAM-FLOW", " New User Avatar Uploaded");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected_image);
                    new ImageSaver(getApplicationContext()).setFileName(uid + ".jpeg").setDirectoryName("Avatars").save(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("PROGRAM-FLOW", "New User Avatar Upload Fail");
            }
        });
    }
}