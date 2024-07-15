package com.example.glass_project.product.ui.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.glass_project.DTO.CartDTO.CartDetailResponse;
import com.example.glass_project.DTO.CartDTO.CartSummaryResponse;
import com.example.glass_project.DTO.OrderDTO.OrderResponse;
import com.example.glass_project.DTO.OrderDTO.OrderWithOrderDetailRequest;
import com.example.glass_project.DTO.OrderDetailDTO.OrderDetailRequest;
import com.example.glass_project.DTO.PaymentDTO.PaymentGetLinkRequest;
import com.example.glass_project.DTO.PaymentDTO.PaymentGetLinkResponse;
import com.example.glass_project.R;
import com.example.glass_project.config.CartService;
import com.example.glass_project.config.OrderService;
import com.example.glass_project.config.PaymentService;
import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.repositories.CartRepositories;
import com.example.glass_project.config.repositories.OrderRepositories;
import com.example.glass_project.config.services.CartServices;
import com.example.glass_project.model.Order;
import com.example.glass_project.model.ProductGlass;
import com.google.gson.Gson;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderSummary extends AppCompatActivity {
    ListView listViewGlass;
    ImageView btnEditShipping;
    TextView txtAddress;
    Button btnConfirmAllItems;
    Toolbar toolbar;
    OrderService orderService;
    private ActivityResultLauncher<Intent> shipInfoResultLauncher;
    CartSummaryResponse cartSummaryResponse;
    ArrayList<CartDetailResponse> itemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        orderService = OrderRepositories.getOrderServices();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listViewGlass = (ListView) findViewById(R.id.lsViewGlass);
        btnEditShipping = (ImageView) findViewById(R.id.btnEditShipping);
        txtAddress = (TextView) findViewById(R.id.tvDeliveryAddress);
        btnConfirmAllItems = (Button) findViewById(R.id.btnConfirmAllItems);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", 0);
        String accountId = sharedPreferences.getString("id", null);
        fetchItems(Integer.parseInt(accountId));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        shipInfoResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            txtAddress.setText(result.getData().getStringExtra("shipinfo"));
                        }
                    }
                }
        );
        btnEditShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderSummary.this, ShipInfo.class);
                shipInfoResultLauncher.launch(intent);
            }
        });

        btnConfirmAllItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.isEmpty()) {
                    Toast.makeText(OrderSummary.this, "Not have item", Toast.LENGTH_LONG).show();
                    return;
                }
                if (txtAddress.getText().toString().trim().isEmpty()){
                    Toast.makeText(OrderSummary.this, "Not have delivery information", Toast.LENGTH_LONG).show();
                    return;
                }
                createOrderProduct();
            }
        });
    }

    private void createOrderProduct(){
        try{
            List<OrderDetailRequest> orderDetailRequests = new ArrayList<>();
            for (CartDetailResponse cartDetailResponse : itemList) {
                OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
                orderDetailRequest.setProductGlassId(cartDetailResponse.getProductGlassID());
                orderDetailRequest.setQuantity(cartDetailResponse.getQuantity());
                orderDetailRequest.setStatus(true);
                orderDetailRequests.add(orderDetailRequest);
            }
            OrderWithOrderDetailRequest orderWithOrderDetailRequest = new OrderWithOrderDetailRequest();
            orderWithOrderDetailRequest.setAccountId(itemList.get(0).getAccountID());
            orderWithOrderDetailRequest.setReceiverAddress(txtAddress.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            String formattedDate = sdf.format(date);
            orderWithOrderDetailRequest.setOrderDate(formattedDate);
            orderWithOrderDetailRequest.setTotal(cartSummaryResponse.getTotalPrice().doubleValue());
            orderWithOrderDetailRequest.setStatus(true);
            orderWithOrderDetailRequest.setOrderDetails(orderDetailRequests);
            Call<OrderResponse> call = orderService.create(orderWithOrderDetailRequest);
            call.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if(response.body() != null){
                        payment(response.body().getTotal().doubleValue(), response.body().getAccountID(), response.body().getId());
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Log.e("Error", t.getMessage());
                    Toast.makeText(OrderSummary.this, "Save fail", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex){
            Toast.makeText(OrderSummary.this, "Save fail", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchItems(int accountId){
        try{
            itemList.clear();
            Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
            CartService cartService = retrofit.create(CartService.class);
            Call<CartSummaryResponse> call = cartService.getbyaccountid(accountId);
            call.enqueue(new Callback<CartSummaryResponse>() {
                @Override
                public void onResponse(Call<CartSummaryResponse> call, Response<CartSummaryResponse> response) {
                    cartSummaryResponse = response.body();
                    if(cartSummaryResponse == null){
                        return;
                    }
                    for (CartDetailResponse cartDetailResponse : cartSummaryResponse.getCartDetails()){
                        itemList.add(cartDetailResponse);
                    }
                    TextView txtOrderTotal = findViewById(R.id.tvOrderTotalAll);
                    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                    txtOrderTotal.setText("Order Total: " + formatter.format(cartSummaryResponse.getTotalPrice()) + " VND");
                    CustomAdapter adapter = new CustomAdapter(OrderSummary.this, itemList);
                    listViewGlass.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<CartSummaryResponse> call, Throwable t) {
                    Toast.makeText(OrderSummary.this, "Get fail", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex){
            Log.d("Loi", ex.getMessage());
        }
    }

    private void payment(double amount, int accoundId, int orderId) {
        try{
            Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
            PaymentService paymentService = retrofit.create(PaymentService.class);
            PaymentGetLinkRequest paymentGetLinkRequest = new PaymentGetLinkRequest(amount, accoundId, orderId);
            Call<PaymentGetLinkResponse> call = paymentService.createPaymentUrl("https://visionup.id.vn/" + orderId, paymentGetLinkRequest);
            call.enqueue(new Callback<PaymentGetLinkResponse>() {
                @Override
                public void onResponse(Call<PaymentGetLinkResponse> call, Response<PaymentGetLinkResponse> response) {
                    if(response.body() != null){
                        Log.e("Error", response.body().get_data().getPaymentUrl());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.body().get_data().getPaymentUrl()));
                        startActivity(browserIntent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PaymentGetLinkResponse> call, Throwable t) {
                    Log.e("Error", t.getMessage());
                    Toast.makeText(OrderSummary.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex){
            Toast.makeText(OrderSummary.this, "Can't pay", Toast.LENGTH_LONG).show();
        }
    }
}