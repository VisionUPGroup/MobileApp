package com.example.glass_project.product.ui.order.history;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.OrderDetailAdapter;
import com.example.glass_project.data.adapter.RatingAdapter;
import com.example.glass_project.data.model.kiosk.Kiosk;
import com.example.glass_project.data.model.order.OrderDetail;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.data.model.order.OrderPaymentDetail;
import com.example.glass_project.data.model.order.ProductGlass;
import com.example.glass_project.data.model.order.ProductGlasses;
import com.example.glass_project.data.model.rating.RatingEyeGlass;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderDetails;
    private OrderDetailAdapter orderDetailAdapter;
    private List<OrderDetail> orderDetailsList = new ArrayList<>();
    private OrderPaymentDetail orderPaymentDetail; // Store payment details
    private TextView txtTotalAmount, txtTotalPaid, txtRemainingAmount;
    private TextView txtReceiverAddress, txtKiosks, txtIsDeposit;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private static final String TAG = "OrderDetailActivity";
    Button buttonReview;
    ImageView icon_pending, icon_processing, icon_shipping, icon_delivered, icon_completed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerViewOrderDetails = findViewById(R.id.recyclerViewOrderDetails);
        recyclerViewOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        buttonReview = findViewById(R.id.button_review);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtTotalPaid = findViewById(R.id.txtTotalPaid);
        txtRemainingAmount = findViewById(R.id.txtRemainingAmount);
        txtReceiverAddress = findViewById(R.id.txtReceiverAddress);
        txtKiosks = findViewById(R.id.txtKiosks);
        txtIsDeposit = findViewById(R.id.txtIsDeposit);
        icon_pending = findViewById(R.id.icon_pending);
        icon_processing = findViewById(R.id.icon_processing);
        icon_shipping = findViewById(R.id.icon_shipping);
        icon_delivered = findViewById(R.id.icon_delivered);
        icon_completed = findViewById(R.id.icon_completed);
        int orderId = getIntent().getIntExtra("orderId", -1);
        fetchOrderDetails(orderId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonReview.setOnClickListener(v -> showRatingDialog());
        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void showRatingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rating);
        dialog.setCancelable(true);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewRatings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách sản phẩm từ orderDetailsList
        List<RatingEyeGlass> ratingList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailsList) {
            ProductGlass productGlass = orderDetail.getProductGlass();
            ratingList.add(new RatingEyeGlass(
                    productGlass.getEyeGlass().getId(),
                    productGlass.getEyeGlass().getName(),
                    productGlass.getEyeGlass().getEyeGlassImages().get(0).getUrl(),
                    0 // Default rating
            ));
        }

        // Gán Adapter
        RatingAdapter adapter = new RatingAdapter(this, ratingList);
        recyclerView.setAdapter(adapter);

        // Nút Submit Rating
        Button btnSubmit = dialog.findViewById(R.id.btnSubmitRating);
        btnSubmit.setOnClickListener(v -> {
            submitRatings(ratingList);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitRatings(List<RatingEyeGlass> ratingList) {
        new Thread(() -> {
            for (RatingEyeGlass rating : ratingList) {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    String accessToken = sharedPreferences.getString("accessToken", "");

                    URL url = new URL(baseUrl.BASE_URL + "/api/rating-eyeglasses");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    connection.setRequestProperty("Content-Type", "application/json");

                    String jsonBody = new Gson().toJson(rating);
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(jsonBody.getBytes());

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "Rating submitted for: " + rating.getEyeGlassName());
                    } else {
                        Log.e(TAG, "Failed to submit rating for: " + rating.getEyeGlassName());
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Error submitting rating", e);
                }
            }
        }).start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý sự kiện khi nhấn vào nút quay lại
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Fetch order details
    private void fetchOrderDetails(int orderId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                URL url = new URL(baseUrl.BASE_URL + "/api/accounts/orders/" + orderId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    OrderHistoryItem orderHistoryItem = new Gson().fromJson(response.toString(), OrderHistoryItem.class);
                    orderDetailsList = orderHistoryItem.getOrderDetails();

                    runOnUiThread(() -> {
                        // Check if there's a receiver address
                        if (orderHistoryItem.getReceiverAddress() != null && !orderHistoryItem.getReceiverAddress().isEmpty()) {
                            // Display receiver address and hide kiosk information
                            txtReceiverAddress.setText("Địa chỉ: " + orderHistoryItem.getReceiverAddress());
                            txtKiosks.setVisibility(View.GONE);
                        } else {
                            // If no receiver address, fetch and display kiosk information
                            txtReceiverAddress.setVisibility(View.GONE);
                            fetchKioskDetails(orderHistoryItem.getKiosks());
                        }
                        int orderStatus = orderHistoryItem.getProcess(); // Lấy giá trị process từ API
                        updateProgressBar(orderStatus);

                        txtIsDeposit.setText("Tiền cọc: " + (orderHistoryItem.isDeposit() ? "Có" : "Không"));
                        fetchPaymentDetails(orderId); // Fetch payment details after order details
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load order details.", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchOrderDetails: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading order details.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void updateProgressBar(int status) {
        int pendingColor = ContextCompat.getColor(this, R.color.blue); // Màu cho trạng thái Pending
        int processingColor = ContextCompat.getColor(this, R.color.blue1); // Màu cho trạng thái Processing
        int shippingColor = ContextCompat.getColor(this, R.color.blue2); // Màu cho trạng thái Shipping
        int deliveredColor = ContextCompat.getColor(this, R.color.blue3); // Màu cho trạng thái Delivered
        int completedColor = ContextCompat.getColor(this, R.color.green); // Màu cho trạng thái Completed
        int canceledColor = ContextCompat.getColor(this, R.color.canceledColor); // Màu cho trạng thái Canceled
        int inactiveColor = ContextCompat.getColor(this, R.color.gray); // Màu cho trạng thái chưa hoàn thành

        // Đặt màu nền mặc định cho các biểu tượng và thanh nối
        icon_pending.setColorFilter(inactiveColor);
        icon_processing.setColorFilter(inactiveColor);
        icon_shipping.setColorFilter(inactiveColor);
        icon_delivered.setColorFilter(inactiveColor);
        icon_completed.setColorFilter(inactiveColor);

        findViewById(R.id.view_progress_1).setBackgroundColor(inactiveColor);
        findViewById(R.id.view_progress_2).setBackgroundColor(inactiveColor);
        findViewById(R.id.view_progress_3).setBackgroundColor(inactiveColor);
        findViewById(R.id.view_progress_4).setBackgroundColor(inactiveColor);

        // Cập nhật màu sắc dựa trên trạng thái
        switch (status) {
            case 0: // Pending
                icon_pending.setColorFilter(pendingColor);
                break;
            case 1: // Processing
                icon_pending.setColorFilter(pendingColor);
                icon_processing.setColorFilter(processingColor);
                findViewById(R.id.view_progress_1).setBackgroundColor(processingColor);
                break;
            case 2: // Shipping
                icon_pending.setColorFilter(pendingColor);
                icon_processing.setColorFilter(processingColor);
                icon_shipping.setColorFilter(shippingColor);
                findViewById(R.id.view_progress_1).setBackgroundColor(processingColor);
                findViewById(R.id.view_progress_2).setBackgroundColor(shippingColor);
                break;
            case 3: // Delivered
                icon_pending.setColorFilter(pendingColor);
                icon_processing.setColorFilter(processingColor);
                icon_shipping.setColorFilter(shippingColor);
                icon_delivered.setColorFilter(deliveredColor);
                findViewById(R.id.view_progress_1).setBackgroundColor(processingColor);
                findViewById(R.id.view_progress_2).setBackgroundColor(shippingColor);
                findViewById(R.id.view_progress_3).setBackgroundColor(deliveredColor);
                break;
            case 4: // Completed
                icon_pending.setColorFilter(pendingColor);
                icon_processing.setColorFilter(processingColor);
                icon_shipping.setColorFilter(shippingColor);
                icon_delivered.setColorFilter(deliveredColor);
                icon_completed.setColorFilter(completedColor);
                findViewById(R.id.view_progress_1).setBackgroundColor(processingColor);
                findViewById(R.id.view_progress_2).setBackgroundColor(shippingColor);
                findViewById(R.id.view_progress_3).setBackgroundColor(deliveredColor);
                findViewById(R.id.view_progress_4).setBackgroundColor(completedColor);
                buttonReview.setVisibility(View.VISIBLE);
                break;
            case 5: // Cancelled
                // Cập nhật tất cả màu sắc thành màu canceledColor
                icon_pending.setColorFilter(canceledColor);
                icon_processing.setColorFilter(canceledColor);
                icon_shipping.setColorFilter(canceledColor);
                icon_delivered.setColorFilter(canceledColor);
                icon_completed.setColorFilter(canceledColor);
                findViewById(R.id.view_progress_1).setBackgroundColor(canceledColor);
                findViewById(R.id.view_progress_2).setBackgroundColor(canceledColor);
                findViewById(R.id.view_progress_3).setBackgroundColor(canceledColor);
                findViewById(R.id.view_progress_4).setBackgroundColor(canceledColor);
                buttonReview.setVisibility(View.GONE); // Ẩn nút Review nếu trạng thái là Cancelled
                break;
        }
    }




    // Fetch kiosk details based on kiosk ID
    private void fetchKioskDetails(int kioskId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                URL url = new URL(baseUrl.BASE_URL + "/api/kiosks/" + kioskId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Kiosk kiosk = new Gson().fromJson(response.toString(), Kiosk.class);

                    runOnUiThread(() -> {
                        // Update UI with kiosk name and address
                        txtKiosks.setText("Ki-ốt: " + kiosk.getName() + "\nĐịa chỉ Ki-ốt: " + kiosk.getAddress());
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load kiosk details.", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchKioskDetails: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading kiosk details.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Fetch payment details
    private void fetchPaymentDetails(int orderId) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                URL url = new URL(baseUrl.BASE_URL + "/api/orders/payment/" + orderId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    orderPaymentDetail = new Gson().fromJson(response.toString(), OrderPaymentDetail.class);

                    runOnUiThread(() -> {
                        // Set payment details in UI with DecimalFormat
                        txtTotalAmount.setText("đ" + decimalFormat.format(orderPaymentDetail.getTotalAmount()));
                        txtTotalPaid.setText("đ" + decimalFormat.format(orderPaymentDetail.getTotalPaid()));
                        txtRemainingAmount.setText("đ" + decimalFormat.format(orderPaymentDetail.getRemainingAmount()));

                        // Fetch ProductGlass details after payment details are fetched
                        fetchProductGlassDetails();
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load payment details.", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchPaymentDetails: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading payment details.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Fetch ProductGlass details for each OrderDetail
    private void fetchProductGlassDetails() {
        CountDownLatch latch = new CountDownLatch(orderDetailsList.size());
        for (OrderDetail orderDetail : orderDetailsList) {
            int productGlassId = orderDetail.getProductGlass().getId();

            new Thread(() -> {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    String accessToken = sharedPreferences.getString("accessToken", "");

                    URL url = new URL(baseUrl.BASE_URL + "/api/product-glasses/" + productGlassId);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        ProductGlasses productGlasses = new Gson().fromJson(response.toString(), ProductGlasses.class);
                        orderDetail.getProductGlass().setProductGlassDetail(productGlasses);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "fetchProductGlassDetails: ", e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        new Thread(() -> {
            try {
                latch.await(); // Wait for all product glass details to be fetched
                runOnUiThread(() -> {
                    // Initialize adapter after all ProductGlass details are loaded
                    orderDetailAdapter = new OrderDetailAdapter(this, orderDetailsList, orderPaymentDetail);
                    recyclerViewOrderDetails.setAdapter(orderDetailAdapter);
                });
            } catch (InterruptedException e) {
                Log.e(TAG, "fetchProductGlassDetails: latch await interrupted", e);
            }
        }).start();
    }
}
