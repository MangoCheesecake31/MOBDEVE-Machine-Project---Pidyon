package com.mobdeve.s11.g25.pidyon.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s11.g25.pidyon.databinding.ActivitySignUpBinding;
import com.mobdeve.s11.g25.pidyon.model.Contact;
import com.mobdeve.s11.g25.pidyon.model.User;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference firebaseDatabase;

    private Uri selected_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        setListeners();
    }

    private void setListeners() {
        // Navigate Back to Sign In Activity
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        // Sign Up
        binding.buttonSignUp.setOnClickListener(v -> {
            // Minimize Keyboard & Toggle Progress Views
            minimizeKeyboard();
            toggleProgressMode();

            // Retrieves inputs
            String username = binding.inputName.getEditableText().toString().trim();
            String email_address = binding.inputEmail.getEditableText().toString().trim();
            String password = binding.inputPassword.getEditableText().toString().trim();
            String confirm_password = binding.inputConfirmPassword.getEditableText().toString().trim();

            // Data validation
            if (!validateData(username, email_address, password, confirm_password)) {
                toggleProgressMode();
                return;
            }

            // Authentication & Database
            firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(create_user_task -> {
                if (create_user_task.isSuccessful()) {
                    Log.d("PROGRAM-FLOW", "User Authenticated!");

                    // Get Device Token for Notifications
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(get_token_task -> {
                        Log.d("PROGRAM-FLOW", "Token Retrieved!");
                        String token = get_token_task.getResult();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        User user = new User(username, email_address, token);
                        ArrayList<Contact> contact = new ArrayList<>();
                        uploadImage();

                        // Add User Data to Database at <user_id>/User/.
                        firebaseDatabase.child(uid).child("User").setValue(user).addOnCompleteListener(add_database_task -> {
                            toggleProgressMode();
                            if (add_database_task.isSuccessful()) {
                                Log.d("PROGRAM-FLOW", "User Data Added to Database!");
                                Toast.makeText(SignUpActivity.this, "User has been successfully registered", Toast.LENGTH_LONG).show();
                                delayTime(3);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                } else {
                    toggleProgressMode();
                    Toast.makeText(SignUpActivity.this, "Email Address is already registered!", Toast.LENGTH_LONG).show();
                }
            });
        });

        // Select Image
        binding.imageProfile.setOnClickListener(v -> {
            // Max Resolution: 1000x1000, Gallery, Croppable, Max Size: 1MB
            ImagePicker.Companion.with(SignUpActivity.this).maxResultSize(1000, 1000).cropSquare().galleryOnly().compress(1024).start();
        });
    }

    // Update Image Profile to selected Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selected_image = data.getData();
        binding.addImageText.setVisibility(View.INVISIBLE);
        binding.imageProfile.setImageURI(selected_image);
    }

    // Upload User Profile Image to Firebase Cloud Storage
    private void uploadImage() {
        Log.d("PROGRAM-FLOW", "Uploading Image!");
        // No Image
        if (selected_image == null) {
            Log.d("PROGRAM-FLOW", "User has No Selected Image!");
            return;
        }

        // Filename: user_avatars/<User ID>
        StorageReference storageReference = firebaseStorage.getReference("user_avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference.putFile(selected_image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PROGRAM-FLOW", " User Avatar Uploaded");
            } else {
                Log.d("PROGRAM-FLOW", "User Avatar Upload Fail");
            }
        });
    }

    // Validate User Data
    private boolean validateData(String username, String email_address, String password, String confirm_password) {
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

        if (email_address.isEmpty()) {
            binding.inputEmail.requestFocus();
            binding.inputEmail.setError("Email Address is required!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
            binding.inputEmail.setError("Please set a valid email!");
            binding.inputEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError("Please provide a password!");
            binding.inputPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.inputPassword.setError("Minimum password length is 6!");
            binding.inputPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirm_password)) {
            binding.inputConfirmPassword.requestFocus();
            binding.inputConfirmPassword.setError("Password confirmation does not match!");
            return false;
        }

        return true;
    }

    // 3 Second Delay
    private void delayTime(int seconds) {
        Log.d("PROGRAM-FLOW", "Delaying 3 Seconds!");
        // 3 Second Delay
        new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // ¯\_(ツ)_/¯
            }

            @Override
            public void onFinish() {
                // ¯\_(ツ)_/¯
            }
        }.start();
    }

    // Toggles Visibility of Progress Related Views
    private void toggleProgressMode() {
        Log.d("PROGRAM-FLOW", "Toggled Progress Mode!");
        if (binding.buttonSignUp.isClickable()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setClickable(false);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setClickable(true);
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
}
