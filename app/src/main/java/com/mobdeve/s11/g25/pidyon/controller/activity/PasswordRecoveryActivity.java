package com.mobdeve.s11.g25.pidyon.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityPasswordRecoveryBinding;


public class PasswordRecoveryActivity extends AppCompatActivity {

    ActivityPasswordRecoveryBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordRecoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        // Recover/Reset Password
        binding.buttonRecoverPassword.setOnClickListener(v -> {
            // Minimize Keyboard & Toggle Progress Views
            minimizeKeyboard();
            toggleProgressMode();

            // Retrieve Inputs
            String email_address = binding.inputEmail.getText().toString().trim();

            // Validate Data
            if (!validateData(email_address)) {
                toggleProgressMode();
                return;
            }

            // Send Password Reset Email
            firebaseAuth.sendPasswordResetEmail(email_address).addOnCompleteListener(recover_task -> {
                toggleProgressMode();
                if (recover_task.isSuccessful()) {
                    Log.d("PROGRAM-FLOW", "Password Recovery Email Sent!");
                    Toast.makeText(PasswordRecoveryActivity.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    delayTime(3);
                } else {
                    Log.d("PROGRAM-FLOW", "Password Recovery Email Failed!");
                    Toast.makeText(PasswordRecoveryActivity.this, "Email Address is not registered", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // Validate Data
    private boolean validateData(String email_address) {
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

        return true;
    }

    // Toggles Visibility of Progress Related Views
    private void toggleProgressMode() {
        Log.d("PROGRAM-FLOW", "Toggled Progress Mode!");
        if (binding.buttonRecoverPassword.isClickable()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonRecoverPassword.setVisibility(View.INVISIBLE);
            binding.buttonRecoverPassword.setClickable(false);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonRecoverPassword.setVisibility(View.VISIBLE);
            binding.buttonRecoverPassword.setClickable(true);
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
                onBackPressed();
            }
        }.start();
    }
}