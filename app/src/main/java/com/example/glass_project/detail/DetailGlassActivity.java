package com.example.glass_project.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.glass_project.R;
import com.example.glass_project.config.ApiService;
import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.data.adapter.ImageSliderAdapter;
import com.example.glass_project.data.model.EyeGlass;
import com.example.glass_project.data.model.EyeGlassImage;
import com.example.glass_project.data.model.EyeGlassType;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class DetailGlassActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;
    private LinearLayoutCompat ratingStars;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_glass);


        // Get the glass name and price from the intent
        viewPager = findViewById(R.id.viewPager);
        TextView nameView = findViewById(R.id.glass_name);
        TextView priceView = findViewById(R.id.glass_price);
        TextView typeView = findViewById(R.id.glass_type);
        ratingStars = findViewById(R.id.ratingStars);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Hiển thị nút back trên toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Handle the back button click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Intent is used to    pass data between activities
        int glassId = getIntent().getIntExtra("glass_id", -1);
        int GlassType = getIntent().getIntExtra("glass_type", -1);

        ApiService apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        Call<EyeGlass> eyeGlassCall = apiService.getGlassById(glassId);
        Call<List<EyeGlassImage>> eyeGlassImageCall = apiService.getGlassImageById(glassId);
        Call<EyeGlassType> eyeGlassTypeCall = apiService.getGlassTypeById(GlassType);

        eyeGlassImageCall.enqueue(new retrofit2.Callback<List<EyeGlassImage>>() {
            @Override
            public void onResponse(@NonNull Call<List<EyeGlassImage>> call, @NonNull retrofit2.Response<List<EyeGlassImage>> response) {
                List<EyeGlassImage> eyeGlassImages = response.body();
                if (eyeGlassImages != null) {
                    adapter = new ImageSliderAdapter(DetailGlassActivity.this, eyeGlassImages);
                    viewPager.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EyeGlassImage>> call, @NonNull Throwable t) {
                Log.e("DetailGlassActivity", "Error getting glass images by id", t);
            }
        });

        eyeGlassCall.enqueue(new retrofit2.Callback<EyeGlass>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EyeGlass> call, @NonNull retrofit2.Response<EyeGlass> response) {
                EyeGlass eyeGlass = response.body();
                assert eyeGlass != null;
                nameView.setText(eyeGlass.getName());
                priceView.setText("$ " + eyeGlass.getPrice());
                setRatingStars(eyeGlass.getRate());
            }

            @Override
            public void onFailure(@NonNull Call<EyeGlass> call, @NonNull Throwable t) {
                Log.e("DetailGlassActivity", "Error getting glass by id", t);
            }
        });

        eyeGlassTypeCall.enqueue(new retrofit2.Callback<EyeGlassType>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EyeGlassType> call, @NonNull retrofit2.Response<EyeGlassType> response) {
                EyeGlassType eyeGlassType = response.body();
                assert eyeGlassType != null;
                typeView.setText(eyeGlassType.getGlassType().toUpperCase());
            }

            @Override
            public void onFailure(@NonNull Call<EyeGlassType> call, @NonNull Throwable t) {
                Log.e("DetailGlassActivity", "Error getting glass type by id", t);
            }
        });

    }

    private void setRatingStars(int rating) {
        ratingStars.removeAllViews(); // Clear previous stars

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
            star.setLayoutParams(layoutParams);

            // Example: Setting star image based on rating
            if (i < rating) {
                star.setImageResource(R.drawable.ic_star_filled); // Replace with your star filled drawable
            } else {
                star.setImageResource(R.drawable.ic_star_outline); // Replace with your star outline drawable
            }

            ratingStars.addView(star);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Additional custom behavior if needed
    }

}
