package com.example.glass_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.glass_project.auth.ViewFragmentApdater;
import com.example.glass_project.product.ProductsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user session exists
        if (hasUserDetails()) {
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

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Login");
                            break;
                        case 1:
                            tab.setText("Register");
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
}