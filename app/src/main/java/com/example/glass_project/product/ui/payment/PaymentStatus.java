package com.example.glass_project.product.ui.payment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.glass_project.R;
import com.example.glass_project.config.OrderService;
import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.model.Order;
import com.example.glass_project.product.ProductsActivity;
import com.example.glass_project.product.ui.home.HomeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentStatus extends AppCompatActivity {
    Button btnContinueShopping;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentStatus.this, ProductsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            TextView txtStatusOrder = findViewById(R.id.txtStatusOrder);
            TextView txtOrderId = findViewById(R.id.txtOrderId);
            String vnpResponseCode = data.getQueryParameter("vnp_ResponseCode");
            String path = data.getPath();
            String[] segments = path.split("/");
            if (segments.length > 1) {
                String number = segments[1];
                txtOrderId.setText(number);
                if ("00".equals(vnpResponseCode)) {
                    UpdateOrder(Integer.parseInt(number));
                    txtStatusOrder.setText("Payment Successful");
                    txtStatusOrder.setTextColor(Color.parseColor("#1BB84B"));
                    Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
                } else {
                    txtStatusOrder.setText("Payment Failed");
                    txtStatusOrder.setTextColor(Color.parseColor("#B81B1B"));
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void UpdateOrder(int orderId){
        try{
            Order order = new Order();
            order.setProcess(1);
            order.setId(orderId);
            Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
            OrderService orderService = retrofit.create(OrderService.class);
            Call<Order> call = orderService.update(order);
            call.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if(response.body() != null){
                        Toast.makeText(PaymentStatus.this, "OrderUpdate Successful", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(PaymentStatus.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex){
            Toast.makeText(PaymentStatus.this, "Can't find orderID", Toast.LENGTH_LONG).show();
        }
    }
}