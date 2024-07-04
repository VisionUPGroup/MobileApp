package com.example.glass_project.detail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;

public class DetailGlassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_glass);
        int glassId = getIntent().getIntExtra("glass_id", -1);
        // Fetch the glass details using the ID (glassId)
        // For simplicity, we use dummy data here
        String glassName = "Glass " + glassId;
        int glassPrice = glassId * 10;


        TextView nameView = findViewById(R.id.glass_name);
        TextView priceView = findViewById(R.id.glass_price);

        nameView.setText(glassName);
        priceView.setText("Price: $" + glassPrice);

    }

}
