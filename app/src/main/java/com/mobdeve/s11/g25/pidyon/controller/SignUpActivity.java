package com.mobdeve.s11.g25.pidyon.controller;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s11.g25.pidyon.databinding.ActivitySignUpBinding;
import com.mobdeve.s11.g25.pidyon.model.User;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private Uri selected_image;
    private final String username_regex = "^[A-Za-z][A-Za-z0-9_]{5,29}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        setListeners();
    }

    private void setListeners() {
        // Navigate Back to Sign In Activity
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        // Sign Up
        binding.buttonSignUp.setOnClickListener(v -> {
            // Minimizes the virtual keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // Progress
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
            firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(username, email_address);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        uploadImage();
                        database.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                toggleProgressMode();
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "User has been successfully registered", Toast.LENGTH_LONG).show();
                                    delayTime(3);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        toggleProgressMode();
                        Toast.makeText(SignUpActivity.this, "Email Address is already registered!", Toast.LENGTH_LONG).show();
                    }
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
        // No Image
        if (selected_image == null) {
            return;
        }

        // Filename: user_avatars/<User ID>
        StorageReference storageReference = firebaseStorage.getReference("user_avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        storageReference.putFile(selected_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("DEBAGGER", " User Avatar Uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("DEBAGGER", " User Avatar Upload Fail");
            }
        });
    }

    // Validate User Data
    private boolean validateData(String username, String email_address, String password, String confirm_password) {
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
        // 3 Second Delay
        new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // ¯\_(ツ)_/¯
            }

            @Override
            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }

    // Toggles Visibility of Progress Related Views
    private void toggleProgressMode() {
        Log.d("DEBAGGER", "Toggled Progress Mode!");
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
}
