package com.mobdeve.s11.g25.pidyon.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivitySignInBinding;

import org.jetbrains.annotations.NotNull;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        // Sign in
        binding.buttonSignIn.setOnClickListener(v -> {
            // Retrieve inputs
            String email_address = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString().trim();

            // Input validation
            if (email_address.isEmpty()) {
                binding.inputEmail.setError("Please enter your email address!");
                binding.inputEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
                binding.inputEmail.setError("Please enter a valid email address!");
                binding.inputEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.inputPassword.setError("Please enter your password!");
                binding.inputPassword.requestFocus();
                return;
            }

            // Authentication
            firebaseAuth.signInWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(SignInActivity.this, "Failed to login! check your credentials!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}