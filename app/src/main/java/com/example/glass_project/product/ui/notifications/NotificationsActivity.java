package com.example.glass_project.product.ui.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnSendNotification;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        etTitle = findViewById(R.id.etNotificationTitle);
        etMessage = findViewById(R.id.etNotificationMessage);
        btnSendNotification = findViewById(R.id.btnSendNotification);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
    }

    private void sendNotification() {
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();
        long timestamp = System.currentTimeMillis();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Title and message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Gửi tin nhắn qua Firebase Cloud Messaging
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);

        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder("ecb29f67e5a1cd2a33faeb62d34e073bad4892f7")
                .setMessageId(Integer.toString((int) timestamp))
                .setData(data)
                .build());


        // 2. Lưu thông báo vào Firestore
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", timestamp);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Notification sent and saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving notification", Toast.LENGTH_SHORT).show();
                });
    }
}
