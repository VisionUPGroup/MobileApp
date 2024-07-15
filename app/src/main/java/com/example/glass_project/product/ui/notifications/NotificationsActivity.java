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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // Lấy tất cả device tokens từ Firestore
        db.collection("DeviceToken")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> deviceTokens = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                deviceTokens.add(document.getString("token"));
                            }
                            // Gửi thông báo đến từng device token
                            for (String token : deviceTokens) {
                                sendNotificationToDevice(token, title, message);
                            }
                            //saveNotificationToFirestore(title, message, timestamp);
                        } else {
                            Log.w("NotificationsActivity", "Error getting documents.", task.getException());
                            Toast.makeText(NotificationsActivity.this, "Error getting device tokens", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendNotificationToDevice(String token, String title, String message) {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder(token)
                .setMessageId(Integer.toString(getMessageId()))
                .addData("title", title)
                .addData("message", message);

        fm.send(messageBuilder.build());
    }

    private static int msgId = 0;
    private static int getMessageId() {
        return msgId++;
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
