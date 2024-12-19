package com.example.glass_project.product.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.glass_project.R;
import com.example.glass_project.databinding.ActivityProductsBinding;
import com.example.glass_project.product.ui.notifications.NotificationsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProductsBinding binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        findViewById(R.id.notification_icon).setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Configure navigation and setup BottomNavigationView
        setupNavigation();

        // Kiểm tra Intent để điều hướng
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("navigate_to")) {
            int destinationId = intent.getIntExtra("navigate_to", -1);
            if (destinationId != -1) {
                BottomNavigationView navView = findViewById(R.id.nav_view);
                navView.setSelectedItemId( R.id.navigation_map); // Chọn mục cần điều hướng
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        int navigateTo = getIntent().getIntExtra("navigate_to", -1);
        if (navigateTo != -1) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
            navController.navigate(navigateTo);
        }
    }


    private void setupNavigation() {
        try {
            // Ensure the NavHostFragment is found in the layout
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_products);
            if (navHostFragment == null) {
                throw new IllegalStateException("NavHostFragment is null. Check your XML configuration.");
            }

            // Get the NavController from NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Configure AppBar with NavController
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_profile,
                    R.id.navigation_map,
                    R.id.navigation_eye_check,
                    R.id.navigation_account
            ).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Set up BottomNavigationView with NavController
            BottomNavigationView navView = findViewById(R.id.nav_view);
            NavigationUI.setupWithNavController(navView, navController);

            Log.d("NavigationSetup", "Navigation setup successfully.");
        } catch (Exception e) {
            Log.e("NavigationSetup", "Error setting up navigation", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up navigation using NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).build())
                || super.onSupportNavigateUp();
    }
}
