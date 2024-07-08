package com.example.glass_project.product.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.glass_project.R;

public class ShipInfo extends AppCompatActivity {
    Button btnSave;
    EditText edtAddress;
    EditText edtCountry;
    EditText edtDistrict;
    EditText edtCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ship_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSave = findViewById(R.id.btnSaveDeliveryInfo);
        edtAddress = findViewById(R.id.edtAddress);
        edtCountry = findViewById(R.id.edtCountry);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtCity = findViewById(R.id.edtCity);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("shipinfo", edtAddress.getText() + ", " + edtDistrict.getText() + ", " + edtCity.getText() + ", " + edtCountry.getText());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}