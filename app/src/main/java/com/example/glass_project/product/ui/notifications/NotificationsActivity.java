package com.example.glass_project.product.ui.notifications;

import android.os.Bundle;
import android.util.Log;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

        // Define API endpoint
        String apiUrl = "https://localhost:7100/api/Notification/send";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // Create JSON object with title and body
                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("title", title);
                    jsonInput.put("body", message);

                    // Write JSON data to output stream
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInput.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Check response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read response body
                        String responseBody = conn.getResponseMessage();
                        //saveNotificationToFirestore(title, message, timestamp);
                        Log.d("NotificationsActivity", "Response body: " + responseBody);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NotificationsActivity.this, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e("NotificationsActivity", "Failed to send notification. Response code: " + responseCode);
                        showErrorToast("Failed to send notification. Response code: " + responseCode);
                    }

                    conn.disconnect();
                } catch (IOException | JSONException e) {
                    Log.e("NotificationsActivity", "Error sending notification: " + e.getMessage());
                    showErrorToast("Error sending notification: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveNotificationToFirestore(String title, String message, long timestamp) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", timestamp);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NotificationsActivity.this, "Notification saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NotificationsActivity.this, "Error saving notification", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    private void showErrorToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NotificationsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
