package com.example.glass_project.config.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.product.ui.order.history.OrderDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ApiService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Nhận dữ liệu từ thông báo
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String targetActivity = remoteMessage.getData().get("activity");
        String extraData = remoteMessage.getData().get("extraData");

        // Hiển thị thông báo
        showNotification(title, body, targetActivity, extraData);
    }

    private void showNotification(String title, String body, String targetActivity, String extraData) {
        Intent intent;

        if ("OrderDetailActivity".equals(targetActivity)) {
            intent = new Intent(this, OrderDetailActivity.class);

            // Parse `extraData`
            if (extraData != null && !extraData.isEmpty()) {
                try {
                    JSONObject extraDataJson = new JSONObject(extraData);
                    if (extraDataJson.has("Id")) {
                        String orderId = extraDataJson.getString("Id"); // Lấy `orderId` dưới dạng chuỗi
                        intent.putExtra("orderId", orderId); // Truyền vào Intent
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            intent = new Intent(this, MainActivity.class); // Default activity
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "Default_Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
    public void deleteNotification(String accountId, String deviceToken) {
        String BaseUrl = Config.getBaseUrl();// Tạo kết nối tới API
        String url = BaseUrl + "/api/notifications/device-tokens/" + accountId + "/" + deviceToken;

        // Sử dụng OkHttpClient để gửi yêu cầu DELETE
        OkHttpClient client = new OkHttpClient();

        // Tạo request DELETE
        Request request = new Request.Builder()
                .url(url)
                .delete()  // DELETE request
                .addHeader("accept", "*/*")
                .build();

        // Thực hiện yêu cầu trong một background thread
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Thực thi yêu cầu và lấy kết quả
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Log.d(TAG, "Xoá thông báo thành công: " + response.body().string());
                    } else {
                        Log.e(TAG, "Lỗi khi xoá thông báo: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Lỗi kết nối: ", e);
                }
                return null;
            }
        }.execute();
    }

}

