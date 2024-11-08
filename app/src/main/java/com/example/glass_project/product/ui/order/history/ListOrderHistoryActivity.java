package com.example.glass_project.product.ui.order.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.OrderHistoryAdapter;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.data.model.order.OrderHistoryResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListOrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderHistoryItem> orderHistoryItems = new ArrayList<>();
    private static final String TAG = "ListOrderHistory";

    private boolean isLoading = false; // Biến để kiểm soát trạng thái tải
    private int currentPage = 1; // Trang hiện tại
    private final int pageSize = 5; // Số mục trên mỗi trang

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order_history);

        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewOrderHistory.setLayoutManager(layoutManager);

        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistoryItems);
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);

        // Lắng nghe sự kiện cuộn tới cuối danh sách
        recyclerViewOrderHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Kiểm tra nếu đã cuộn tới cuối danh sách và không đang tải
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    currentPage++; // Tăng trang hiện tại
                    fetchOrderHistory(currentPage); // Tải thêm dữ liệu
                }
            }
        });

        fetchOrderHistory(currentPage);
    }

    private void fetchOrderHistory(int page) {
        isLoading = true; // Bắt đầu tải dữ liệu
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("id", "");
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/orders?AccountID=" + accountId
                        + "&PageIndex=" + page + "&PageSize=" + pageSize + "&Descending=true");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    OrderHistoryResponse orderHistoryResponse = new Gson().fromJson(response.toString(), OrderHistoryResponse.class);
                    List<OrderHistoryItem> newItems = orderHistoryResponse.getData();

                    runOnUiThread(() -> {
                        orderHistoryItems.addAll(newItems); // Thêm dữ liệu mới vào danh sách
                        orderHistoryAdapter.notifyDataSetChanged();
                        isLoading = false; // Kết thúc tải
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show());
                    isLoading = false;
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchOrderHistory: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show());
                isLoading = false;
            }
        }).start();
    }
}
