package com.example.glass_project.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.glass_project.R;
import com.example.glass_project.config.repositories.CartRepositories;
import com.example.glass_project.config.services.CartServices;
import com.example.glass_project.data.model.request.CartRequest;
import com.example.glass_project.data.model.response.CartResponse;
import com.example.glass_project.product.ProductsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPrescriptionActivity extends AppCompatActivity {

    EditText etODSphere, etODCylinder, etODAxis, etOSSphere, etOSCylinder, etOSAxis, etAddOD, etAddOS, etPD;
    int lensID;
    String accountID, glassID;
    CartServices cartServices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.glass_project.R.layout.activity_enter_prescription);

        Toolbar toolbar = findViewById(R.id.toolbar);
        cartServices = CartRepositories.cartServices();

        lensID = getIntent().getIntExtra("lens_id", -1);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences glassPreferences = getSharedPreferences("GlassDetails", Context.MODE_PRIVATE);

        accountID = sharedPreferences.getString("id", null);
        glassID = glassPreferences.getString("glass_id", null);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //OD
        etODSphere = findViewById(R.id.et_sph);
        etODCylinder = findViewById(R.id.et_cyl);
        etODAxis = findViewById(R.id.et_axis);

        //OS
        etOSSphere = findViewById(R.id.et_sph_os);
        etOSCylinder = findViewById(R.id.et_cyl_os);
        etOSAxis = findViewById(R.id.et_axis_os);

        //ADD
        etAddOD = findViewById(R.id.add_od);
        etAddOS = findViewById(R.id.add_os);

        //PD
        etPD = findViewById(R.id.et_qty);

        //Submit
        Button btnSubmit = findViewById(R.id.btn_add_to_cart);
        btnSubmit.setOnClickListener(v -> onSubmit());

    }

    private void onSubmit() {
        String odSphere = etODSphere.getText().toString();
        String odCylinder = etODCylinder.getText().toString();
        String odAxis = etODAxis.getText().toString();

        String osSphere = etOSSphere.getText().toString();
        String osCylinder = etOSCylinder.getText().toString();
        String osAxis = etOSAxis.getText().toString();

        String addOD = etAddOD.getText().toString();
        String addOS = etAddOS.getText().toString();

        String pd = etPD.getText().toString();

        if (odSphere.isEmpty() || odCylinder.isEmpty() || odAxis.isEmpty() || osSphere.isEmpty() || osCylinder.isEmpty() || osAxis.isEmpty() || addOD.isEmpty() || addOS.isEmpty() || pd.isEmpty()) {
            Toast.makeText(EnterPrescriptionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }

        CartRequest cartRequest = new CartRequest();

        cartRequest.setAccountID(Integer.parseInt(accountID));
        cartRequest.setEyeGlassID(Integer.parseInt(glassID));
        cartRequest.setLeftLenID(lensID);
        cartRequest.setRightLenID(lensID);
        cartRequest.setProfileMeasurementID(1);
        cartRequest.setSphereOD(Integer.parseInt(odSphere));
        cartRequest.setCylinderOD(Integer.parseInt(odCylinder));
        cartRequest.setAxisOD(Integer.parseInt(odAxis));
        cartRequest.setSphereOS(Integer.parseInt(osSphere));
        cartRequest.setCylinderOS(Integer.parseInt(osCylinder));
        cartRequest.setAxisOS(Integer.parseInt(osAxis));
        cartRequest.setAddOD(Integer.parseInt(addOD));
        cartRequest.setAddOS(Integer.parseInt(addOS));
        cartRequest.setPd(Integer.parseInt(pd));

        Log.d("CartRequest", "onSubmit: " + cartRequest.toString());

        Call<CartResponse> call = cartServices.addToCart(cartRequest);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EnterPrescriptionActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EnterPrescriptionActivity.this, ProductsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EnterPrescriptionActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                Toast.makeText(EnterPrescriptionActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
