package com.example.glass_project.product.ui.order.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.OrderHistoryAdapter;
import com.example.glass_project.data.adapter.ProcessAdapter;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.data.model.order.OrderHistoryResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderHistory;
    private RecyclerView recyclerViewProcess;
    private OrderHistoryAdapter orderHistoryAdapter;
    private ProcessAdapter processAdapter; // Declare processAdapter here
    private List<OrderHistoryItem> orderHistoryItems = new ArrayList<>();
    private static final String TAG = "ListOrderHistory";

    private boolean isLoading = false; // Control loading state
    private int currentPage = 1; // Current page
    private final int pageSize = 5; // Items per page
    private String selectedProcess = "Pending"; // Default process is Pending

    private View noDataView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order_history);

        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        recyclerViewProcess = findViewById(R.id.recyclerViewProcess);
        noDataView = findViewById(R.id.noDataView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewOrderHistory.setLayoutManager(layoutManager);
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistoryItems);
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);

        recyclerViewOrderHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Kiểm tra điều kiện để tải thêm dữ liệu
                    if (!isLoading && (firstVisibleItemPosition + visibleItemCount >= totalItemCount) && dy > 0) {
                        currentPage++; // Tăng số trang
                        fetchOrderHistory(selectedProcess, currentPage); // Gọi API để tải thêm dữ liệu
                    }
                }
            }
        });


        // Configure RecyclerView for Process list (horizontal layout)
        LinearLayoutManager processLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProcess.setLayoutManager(processLayoutManager);

        // List of processes
        List<String> processList = new ArrayList<>();
        processList.add("Đang chờ xử lý");
        processList.add("Đang xử lý");
        processList.add("Đang giao hàng");
        processList.add("Đã giao hàng");
        processList.add("Hoàn thành");
        processList.add("Đã hủy");

        // Initialize processAdapter
        processAdapter = new ProcessAdapter(processList, process -> {
            if (!selectedProcess.equals(process)) { // Chỉ xử lý khi chọn trạng thái khác
                selectedProcess = process; // Lưu trạng thái tiếng Việt
                processAdapter.setSelectedProcess(selectedProcess); // Cập nhật giao diện Process

                currentPage = 1; // Đặt lại trang về 1
                orderHistoryItems.clear(); // Xóa danh sách cũ
                orderHistoryAdapter.notifyDataSetChanged(); // Làm mới giao diện ngay lập tức

                String englishProcess = processMap.get(process); // Lấy tên Process tiếng Anh
                if (englishProcess != null) {
                    fetchOrderHistory(englishProcess, currentPage); // Gọi API với tên Process tiếng Anh
                } else {
                    Toast.makeText(this, "Trạng thái không hợp lệ.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        recyclerViewProcess.setAdapter(processAdapter);

        // Set initial process as selected
        processAdapter.setSelectedProcess(selectedProcess);

        // Fetch data for the initial process
        fetchOrderHistory(selectedProcess, currentPage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private final Map<String, String> processMap = new HashMap<String, String>() {{
        put("Đang chờ xử lý", "0");
        put("Đang xử lý", "1");
        put("Đang giao hàng", "2");
        put("Đã giao hàng", "3");
        put("Hoàn thành", "4");
        put("Đã hủy", "5");
    }};

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void fetchOrderHistory(String process, int page) {
        isLoading = true; // Đặt trạng thái đang tải
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
                        + "&Process=" + process + "&PageIndex=" + page + "&PageSize=" + pageSize + "&Descending=true");

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
                        if (page == 1) {
                            orderHistoryItems.clear();
                            if (newItems.isEmpty()) {
                                Log.d(TAG, "No data available, showing noDataView");
                                recyclerViewOrderHistory.setVisibility(View.GONE);
                                noDataView.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "Data available, hiding noDataView");
                                recyclerViewOrderHistory.setVisibility(View.VISIBLE);
                                noDataView.setVisibility(View.GONE);
                            }
                        }

                        orderHistoryItems.addAll(newItems); // Thêm dữ liệu mới
                        orderHistoryAdapter.notifyDataSetChanged(); // Cập nhật giao diện
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
