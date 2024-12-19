package com.example.glass_project.config.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessaging;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Khởi chạy các service hoặc tác vụ cần thiết
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        }
    }
}

