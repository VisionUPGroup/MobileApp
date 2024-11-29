package com.example.glass_project.product.ui.other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.glass_project.R;
import com.example.glass_project.databinding.ActivityProductsBinding;
import com.example.glass_project.product.ui.account.AccountFragment;
import com.example.glass_project.product.ui.notifications.NotificationsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProductsActivity extends AppCompatActivity {

    private ActivityProductsBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập toolbar
        setSupportActionBar(binding.toolbar);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Thiết lập toolbar với danh sách màu
        int[] colors = new int[]{
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.blue1),
                getResources().getColor(R.color.blue2),
                getResources().getColor(R.color.blue3),
                getResources().getColor(R.color.blue4)
        };
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.notification_icon).setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Cấu hình Navigation
        setupNavigation(); // Gọi hàm setupNavigation để cấu hình NavController và AppBar

        // Cấu hình BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.nav_view);
        try {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
            NavigationUI.setupWithNavController(navView, navController);
        } catch (Exception e) {
            Log.e("NavigationSetup", "Error setting up BottomNavigationView", e);
        }


    }

    private void setupNavigation() {
        // Hàm cấu hình navigation với xử lý ngoại lệ
        try {
            // Tìm kiếm NavHostFragment trong layout
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_products);
            if (navHostFragment == null) {
                throw new IllegalStateException("NavHostFragment is null. Check your XML configuration.");
            }

            // Lấy NavController từ NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Cấu hình AppBarConfiguration cho các destination
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_profile,
                    R.id.navigation_map,
                    R.id.navigation_eye_check,
                    R.id.navigation_account
            ).build();

            // Thiết lập AppBar với NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Thiết lập BottomNavigationView với NavController
            NavigationUI.setupWithNavController(binding.navView, navController);

            Log.d("NavigationSetup", "Navigation setup successfully.");
        } catch (Exception e) {
            // Ghi log lỗi nếu xảy ra vấn đề
            Log.e("NavigationSetup", "Error setting up navigation", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý sự kiện nút "Up" trong AppBar
        NavController navController = null;
        try {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
        } catch (Exception e) {
            Log.e("NavigateUp", "Error finding NavController", e);
        }

        if (navController != null) {
            return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).build())
                    || super.onSupportNavigateUp();
        } else {
            return super.onSupportNavigateUp();
        }
    }

    private void launchProfileActivity() {
        // Hàm cũ mở ProfileActivity
        Intent profileIntent = new Intent(this, AccountFragment.class);
        startActivity(profileIntent);
    }
}
