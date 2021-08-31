package com.mobdeve.s11.g25.pidyon.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g25.pidyon.databinding.ActivitySignInBinding;

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
        // Create Account
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        // Sign in
        binding.buttonSignIn.setOnClickListener(v -> {
            // Minimize Keyboard & Toggle Progress Views
            minimizeKeyboard();
            toggleProgressMode();

            // Retrieve inputs
            String email_address = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString().trim();

            // Input validation
            if (!validateData(email_address, password)) {
                toggleProgressMode();
                return;
            }

            // Authentication
            firebaseAuth.signInWithEmailAndPassword(email_address, password).addOnCompleteListener(task -> {
                Log.d("PROGRAM-FLOW", "User Login Authenticated!");
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    toggleProgressMode();

                    // Email Verification
                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(SignInActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    toggleProgressMode();
                    Toast.makeText(SignInActivity.this, "Failed to login! check your credentials!", Toast.LENGTH_LONG).show();
                }
            });
        });

        // Recover Password
        binding.textRecoverPassword.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PasswordRecoveryActivity.class));
        });
    }

    // Validate Data
    private boolean validateData(String email_address, String password) {
        Log.d("PROGRAM-FLOW", "Validating Data!");
        if (email_address.isEmpty()) {
            binding.inputEmail.setError("Please enter your email address!");
            binding.inputEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
            binding.inputEmail.setError("Please enter a valid email address!");
            binding.inputEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError("Please enter your password!");
            binding.inputPassword.requestFocus();
            return false;
        }

        return true;
    }

    // Toggles Visibility of Progress Related Views
    private void toggleProgressMode() {
        Log.d("PROGRAM-FLOW", "Toggled Progress Mode!");
        if (binding.buttonSignIn.isClickable()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setClickable(false);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.buttonSignIn.setClickable(true);
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