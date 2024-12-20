package com.example.glass_project.product.ui.order.history;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
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
import java.util.Calendar;
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
    private RelativeLayout progressBar;
    private EditText editTextID;
    private TextView textViewFromDate, textViewToDate;
    private Button buttonSearch;

    private int fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
    private boolean isSearchClicked = false;


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
        progressBar = findViewById(R.id.progressBar);


        editTextID = findViewById(R.id.editTextID);
        textViewFromDate = findViewById(R.id.textViewFromDate);
        textViewToDate = findViewById(R.id.textViewToDate);
        buttonSearch = findViewById(R.id.buttonSearch);

        // Khởi tạo ngày mặc định cho FromDate và ToDate
        Calendar calendar = Calendar.getInstance();
        fromYear = calendar.get(Calendar.YEAR);
        fromMonth = calendar.get(Calendar.MONTH);
        fromDay = calendar.get(Calendar.DAY_OF_MONTH);
        toYear = calendar.get(Calendar.YEAR);
        toMonth = calendar.get(Calendar.MONTH);
        toDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Thiết lập sự kiện click cho TextView để mở DatePickerDialog
        textViewFromDate.setOnClickListener(v -> showDatePickerDialog(true));  // From Date
        textViewToDate.setOnClickListener(v -> showDatePickerDialog(false));  // To Date

        // Thiết lập sự kiện cho nút Search
        buttonSearch.setOnClickListener(v -> {
            String id = editTextID.getText().toString().trim();
            String fromDate = textViewFromDate.getText().toString().isEmpty() ? "" : textViewFromDate.getText().toString();
            String toDate = textViewToDate.getText().toString().isEmpty() ? "" : textViewToDate.getText().toString();

            // Reset page number and search status
            currentPage = 1;
            isSearchClicked = true;

            // Clear existing data before fetching new results
            orderHistoryItems.clear();
            orderHistoryAdapter.notifyDataSetChanged();

            // Call fetchOrderHistory with updated parameters
            fetchOrderHistory(selectedProcess, currentPage, id, fromDate, toDate);
        });

        Button buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(v -> {
            // Clear the FromDate and ToDate fields
            textViewFromDate.setText("");
            textViewToDate.setText("");
            editTextID.setText("");
            String id = editTextID.getText().toString().trim();
            String fromDate = textViewFromDate.getText().toString().isEmpty() ? "" : textViewFromDate.getText().toString();
            String toDate = textViewToDate.getText().toString().isEmpty() ? "" : textViewToDate.getText().toString();
            fetchOrderHistory(selectedProcess, currentPage, id, fromDate, toDate);
            Toast.makeText(this, "Đã xóa bộ lọc ngày.", Toast.LENGTH_SHORT).show();
        });


        recyclerViewOrderHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItemPosition = -1;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Ensure conditions for loading more data are correct
                    if (dy > 0 &&!isLoading && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) {
                        lastVisibleItemPosition = firstVisibleItemPosition;
                        currentPage++;

                        String id = editTextID.getText().toString().trim();
                        String fromDate = textViewFromDate.getText().toString().isEmpty() ? "" : textViewFromDate.getText().toString();
                        String toDate = textViewToDate.getText().toString().isEmpty() ? "" : textViewToDate.getText().toString();

                        // Fetch next page of data
                        fetchOrderHistory(selectedProcess, currentPage, id, fromDate, toDate);
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
            if (!selectedProcess.equals(process)) {
                selectedProcess = processMap.get(process); // Cập nhật trạng thái được chọn dưới dạng mã số
                processAdapter.setSelectedProcess(process);  // Cập nhật lại giao diện của Process
                editTextID.setText("");
                textViewFromDate.setText("");
                textViewToDate.setText("");
                currentPage = 1;  // Đặt lại trang về 1
                orderHistoryItems.clear();  // Xóa dữ liệu cũ
                orderHistoryAdapter.notifyDataSetChanged();  // Làm mới giao diện

                fetchOrderHistory(selectedProcess, currentPage, "", "", "");  // Gọi API để tải lại dữ liệu
            }
        });
        recyclerViewProcess.setAdapter(processAdapter);
        processAdapter.setSelectedProcess(processList.get(0));
        // Fetch data for the initial process
        fetchOrderHistory(selectedProcess, currentPage, "", "", "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    private void showDatePickerDialog(final boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = isFromDate ? fromYear : toYear;
        int month = isFromDate ? fromMonth : toMonth;
        int day = isFromDate ? fromDay : toDay;

        // Thiết lập giới hạn ngày tối đa
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.DAY_OF_MONTH, +1); // Ngày hiện tại trừ 1

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth);

            if (isFromDate) {
                // Kiểm tra ngày chọn nhỏ hơn ngày hiện tại - 1
                if (selectedDate.after(maxCalendar)) {
                    Toast.makeText(this, "Dến ngày phải nhỏ hơn ngày hiện tại 1 ngày.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar today = Calendar.getInstance();
                if (!selectedDate.before(today)) {
                    Toast.makeText(this, "Từ ngày phải nhỏ hơn ngày hiện tại.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật giá trị From Date
                fromYear = year1;
                fromMonth = monthOfYear;
                fromDay = dayOfMonth;
                textViewFromDate.setText(String.format("%d-%d-%d", fromYear, fromMonth + 1, fromDay));
            } else {
                Calendar fromDate = Calendar.getInstance();
                fromDate.set(fromYear, fromMonth, fromDay);

                // Kiểm tra ngày chọn nhỏ hơn ngày hiện tại - 1
                if (selectedDate.after(maxCalendar)) {
                    Toast.makeText(this, "To Date phải nhỏ hơn ngày hiện tại 1 ngày.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra To Date lớn hơn From Date
                if (selectedDate.before(fromDate)) {
                    Toast.makeText(this, "To Date phải lớn hơn hoặc bằng From Date.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật giá trị To Date
                toYear = year1;
                toMonth = monthOfYear;
                toDay = dayOfMonth;
                textViewToDate.setText(String.format("%d-%d-%d", toYear, toMonth + 1, toDay));
            }
        }, year, month, day);
        Calendar maxCalendar1 = Calendar.getInstance();
        maxCalendar1.add(Calendar.DAY_OF_MONTH, +0);
        // Giới hạn ngày tối đa và tối thiểu
        if (isFromDate) {
            datePickerDialog.getDatePicker().setMaxDate(maxCalendar1.getTimeInMillis());
        } else {
            Calendar fromDate = Calendar.getInstance();
            fromDate.set(fromYear, fromMonth, fromDay);
            datePickerDialog.getDatePicker().setMinDate(fromDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(maxCalendar1.getTimeInMillis());
        }

        datePickerDialog.show();
    }



    private void fetchOrderHistory(String process, int page, String id, String fromDate, String toDate) {
        if (isLoading) return;
        isLoading = true;
        showLoading(true); // Show loading indicator

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("id", "");
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String BaseUrl = Config.getBaseUrl();
                URL url = new URL(BaseUrl + "/api/accounts/orders?AccountID=" + accountId
                        + "&Process=" + process
                        + "&PageIndex=" + page
                        + "&PageSize=" + pageSize
                        + "&ID=" + id
                        + "&FromDate=" + fromDate
                        + "&ToDate=" + toDate
                        + "&Descending=true");

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
                        showLoading(false);
                        if (page == 1) {
                            orderHistoryItems.clear();
                            if (newItems.isEmpty()) {
                                recyclerViewOrderHistory.setVisibility(View.GONE);
                                noDataView.setVisibility(View.VISIBLE);
                            } else {
                                recyclerViewOrderHistory.setVisibility(View.VISIBLE);
                                noDataView.setVisibility(View.GONE);
                            }
                        }

                        orderHistoryItems.addAll(newItems);
                        orderHistoryAdapter.notifyDataSetChanged();
                        isLoading = false;
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
    private final Map<String, String> processMap = new HashMap<String, String>() {{
        put("Đang chờ xử lý", "0");
        put("Đang xử lý", "1");
        put("Đang giao hàng", "2");
        put("Đã giao hàng", "3");
        put("Hoàn thành", "4");
        put("Đã hủy", "5");
    }};
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (!isLoading && orderHistoryItems.isEmpty() && noDataView != null) {
            recyclerViewOrderHistory.setVisibility(View.GONE);
            noDataView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewOrderHistory.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            if (noDataView != null) noDataView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
