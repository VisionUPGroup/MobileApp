package com.example.glass_project.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.databinding.ActivityProductsBinding;
import com.example.glass_project.product.ui.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProductsActivity extends AppCompatActivity {

    private ActivityProductsBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if (!hasToken()) {
            // Redirect to login activity if token is not available
            Log.d("ProductsActivity", "Token not found, redirecting to MainActivity");
            Intent loginIntent = new Intent(this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
            startActivity(loginIntent);
            finish();
            return;
        }

        setupNavigation();

        ImageView userIcon = findViewById(R.id.user_icon);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchProfileActivity();
            }
        });
    }

    private boolean hasToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("Tên_Tệp_Lưu_Token", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null) != null;
    }

    private void setupNavigation() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_products);
        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).build())
                || super.onSupportNavigateUp();
    }

    private void launchProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private boolean isLoggedIn() {
        // Check if token exists in SharedPreferences
        return sharedPreferences.contains("token");
    }
}