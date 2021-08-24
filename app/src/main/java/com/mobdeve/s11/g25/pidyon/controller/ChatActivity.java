package com.mobdeve.s11.g25.pidyon.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mobdeve.s11.g25.pidyon.R;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityChatBinding;
import com.mobdeve.s11.g25.pidyon.databinding.ActivityMainBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_chat);
    }
}