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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationsActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Button btnSendNotification;
    private FirebaseFirestore db;
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/daring-keep-410204/messages:send";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
        try {
            sendFCMNotification(title, message);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get access token", Toast.LENGTH_SHORT).show();
        }

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

    private void sendFCMNotification(String title, String message) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/all");  // Change this to target specific devices
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String accessToken = AccessTokenUtil.getAccessToken();

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(FCM_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(NotificationsActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(NotificationsActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
