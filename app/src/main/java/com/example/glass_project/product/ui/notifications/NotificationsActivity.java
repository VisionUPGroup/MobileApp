package com.example.glass_project.product.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.NotificationAdapter;
import com.example.glass_project.data.model.other.Notification;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notifications = new ArrayList<>();
    private View noDataView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Khởi tạo RecyclerView
        notificationRecyclerView = findViewById(R.id.notification_recycler_view);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this, notifications);
        notificationRecyclerView.setAdapter(notificationAdapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Khởi tạo View khi không có dữ liệu
        noDataView = findViewById(R.id.noDataView);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Fetch Notifications
        fetchNotifications();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý sự kiện khi nhấn vào nút quay lại
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void handleNotificationClick(Notification notification) {
        String targetActivity = notification.getTargetActivity();
        String extraData = notification.getExtraData();

        Intent intent;

        // Điều hướng đến Activity tương ứng dựa trên targetActivity
        if ("OrderDetailsActivity".equals(targetActivity)) {
            intent = new Intent(this, OrderDetailActivity.class);
            if (extraData != null) {
                try {
                    JSONObject extraDataJson = new JSONObject(extraData);
                    if (extraDataJson.has("orderId")) {
                        intent.putExtra("orderId", extraDataJson.getInt("orderId"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Mặc định chuyển về MainActivity
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
    }
    private void fetchNotifications() {
        new Thread(() -> {
            String accountId = sharedPreferences.getString("id", "");
            if (accountId.isEmpty()) {
                runOnUiThread(() -> noDataView.setVisibility(View.VISIBLE));
                return;
            }

            try {
                // Gọi API
                String BaseUrl = Config.getBaseUrl(); // Thay thế bằng URL thực tế
                URL url = new URL(BaseUrl + "/api/notifications/notifications/" + accountId);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    List<Notification> fetchedNotifications = parseNotificationsFromJSON(jsonResponse);

                    runOnUiThread(() -> {
                        notifications.clear();
                        if (!fetchedNotifications.isEmpty()) {
                            notifications.addAll(fetchedNotifications);
                            noDataView.setVisibility(View.GONE);
                        } else {
                            noDataView.setVisibility(View.VISIBLE);
                        }
                        notificationAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("API", "Failed to fetch notifications. Response Code: " + responseCode);
                    runOnUiThread(() -> noDataView.setVisibility(View.VISIBLE));
                }

            } catch (Exception e) {
                Log.e("API", "Error fetching notifications", e);
                runOnUiThread(() -> noDataView.setVisibility(View.VISIBLE));
            }
        }).start();
    }

    private List<Notification> parseNotificationsFromJSON(JSONObject jsonResponse) {
        List<Notification> notificationList = new ArrayList<>();
        try {
            if (jsonResponse.has("notifications")) {
                JSONArray notificationsArray = jsonResponse.getJSONArray("notifications");

                for (int i = 0; i < notificationsArray.length(); i++) {
                    JSONObject notificationObject = notificationsArray.getJSONObject(i);

                    String id = notificationObject.optString("id", "");
                    String title = notificationObject.optString("title", "");
                    String body = notificationObject.optString("body", "");
                    String targetActivity = notificationObject.optString("targetActivity", "");
                    JSONObject extraDataJSON = notificationObject.optJSONObject("extraData");
                    boolean isRead = notificationObject.has("isRead") && notificationObject.optBoolean("isRead", false);

                    Notification notification = new Notification(
                            id,
                            title,
                            body,
                            targetActivity,
                            extraDataJSON != null ? extraDataJSON.toString() : null,
                            notificationObject.optString("timestamp", ""),
                            isRead
                    );

                    notificationList.add(notification);
                }
            }
        } catch (Exception e) {
            Log.e("ParseNotifications", "Error parsing notifications JSON", e);
        }

        return notificationList;
    }
}
