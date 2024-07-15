package com.example.glass_project.product.ui.message.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.example.glass_project.data.model.User;
import com.example.glass_project.databinding.ActivityChatBinding;
import com.example.glass_project.product.ui.message.Constants;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setContentView(binding.getRoot());
        loadReceiverDetails();
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.tvUsername.setText(receiverUser.name);
    }

    private void setListener(){
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }
}