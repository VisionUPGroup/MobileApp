package com.example.glass_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.glass_project.product.ui.auth.ViewFragmentApdater;
import com.example.glass_project.product.ui.other.ProductsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user session exists
        if (hasUserDetails()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Token của thiết bị
                        String token = task.getResult();
                        Log.d("FCM", "Device Token: " + token);
                    });
            // Redirect to ProductsActivity if user details are available
            Intent intent = new Intent(this, ProductsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewFragmentApdater adapter = new ViewFragmentApdater(this);
        viewPager.setAdapter(adapter);
        FirebaseApp.initializeApp(this);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Đăng nhập");
                            break;
                        case 1:
                            tab.setText("Đăng  kí");
                            break;
                    }
                }).attach();
    }

    private boolean hasUserDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        String username = sharedPreferences.getString("username", null);
        String email = sharedPreferences.getString("email", null);

        return id != null && username != null && email != null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Kiểm tra nếu Activity này là Activity cuối trong ngăn xếp
        if (isFinishing()) {
            // Xóa "ExamData_left" và "ExamData_right"
            SharedPreferences sharedPreferences = getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("ExamData_left");
            editor.remove("ExamData_right");
            editor.apply();
        }
    }


}