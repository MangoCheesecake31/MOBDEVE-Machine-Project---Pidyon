package com.mobdeve.s11.g25.pidyon.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivitySignUpBinding;
import com.mobdeve.s11.g25.pidyon.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
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

            // Retrieves inputs
            String username = binding.inputName.getEditableText().toString().trim();
            String email_address = binding.inputEmail.getEditableText().toString().trim();
            String password = binding.inputPassword.getEditableText().toString().trim();
            String confirm_password = binding.inputConfirmPassword.getEditableText().toString().trim();

            // Data validation
            if (username.isEmpty()) {
                binding.inputName.setError("Username is required!");
                binding.inputName.requestFocus();
                return;
            }

            if (email_address.isEmpty()) {
                binding.inputEmail.requestFocus();
                binding.inputEmail.setError("Email Address is required!");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
                binding.inputEmail.setError("Please set a valid email!");
                binding.inputEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.inputPassword.setError("Please provide a password!");
                binding.inputPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                binding.inputPassword.setError("Minimum password length is 6!");
                binding.inputPassword.requestFocus();
                return;
            }

            if (!password.equals(confirm_password)) {
                binding.inputConfirmPassword.requestFocus();
                binding.inputConfirmPassword.setError("Password confirmation does not match!");
            }

            // Authentication & Database
            firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(username, email_address);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        database.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "User has been successfully registered", Toast.LENGTH_LONG).show();

                                    // 3 Second Delay
                                    new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            // ¯\_(ツ)_/¯
                                        }

                                        @Override
                                        public void onFinish() {
                                            onBackPressed();
                                        }
                                    }.start();

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Email Address is already registered!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}