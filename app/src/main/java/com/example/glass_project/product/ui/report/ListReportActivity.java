package com.example.glass_project.product.ui.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.ReportAdapter;
import com.example.glass_project.data.model.order.OrderPaymentDetail;
import com.example.glass_project.data.model.order.ProductGlassPayment;
import com.example.glass_project.data.model.report.ReportData;
import com.example.glass_project.data.model.report.ReportResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListReportActivity extends AppCompatActivity {

    private static final String TAG = "ListReportActivity";

    private Spinner spinnerStatus, spinnerType;
    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerViewReports;
    private View noDataView;
    private String selectedStatus = "Chưa chọn";
    private String selectedType = "Chưa chọn";
    private ReportAdapter reportAdapter;
    private List<ReportData> reportDataList;

    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 5;
    private int totalCount = 0;
    private View progressBar;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerType = findViewById(R.id.spinner_type);
        searchInput = findViewById(R.id.search_input); // New input field
        searchButton = findViewById(R.id.search_button); // New search button
        recyclerViewReports = findViewById(R.id.recycler_view_reports);
        noDataView = findViewById(R.id.noDataView);
        progressBar = findViewById(R.id.progressBar);

        // Set up Spinner for status and type
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getStatusList());
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getTypeList());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedStatus = (String) parentView.getItemAtPosition(position);
                currentPage = 1;  // Reset page when filter changes
                fetchReports(null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedStatus = "Chưa chọn";
                fetchReports(null);

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedType = (String) parentView.getItemAtPosition(position);
                currentPage = 1;  // Reset page when filter changes
                fetchReports(null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedType = "Chưa chọn";
                fetchReports(null);

            }
        });

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            fetchReportsAndDetails(query);
        });


        // Initialize report data list
        reportDataList = new ArrayList<>();

        // Set up RecyclerView
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, reportDataList);
        recyclerViewReports.setAdapter(reportAdapter);
        setupRecyclerViewScrollListener();

        // Load reports on activity start
        fetchReports(null);

    }
    public void fetchProductGlassPayment(int orderId, TextView textViewProductID, TextView textViewLeftEye, TextView textViewRightEye, TextView textviewProductName) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Access token is missing. Please log in again.", Toast.LENGTH_SHORT).show());
                    return;
                }

                URL url = new URL(Config.getBaseUrl() + "/api/orders/payment/" + orderId);
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
                    reader.close();

                    OrderPaymentDetail orderPaymentDetail = new Gson().fromJson(response.toString(), OrderPaymentDetail.class);

                    updateProductGlassDetails(orderPaymentDetail, textViewProductID, textViewLeftEye, textViewRightEye,textviewProductName);
                } else {
                    handleProductGlassError(textViewProductID, textViewLeftEye, textViewRightEye,textviewProductName, "Server returned: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchProductGlassPayment: Error fetching product glass details", e);
                handleProductGlassError(textViewProductID, textViewLeftEye, textViewRightEye,textviewProductName, "Error loading product glass details.");
            }
        }).start();
    }


    private void updateProductGlassDetails(OrderPaymentDetail orderPaymentDetail, TextView textViewProductID, TextView textViewLeftEye, TextView textViewRightEye,TextView textviewProductName) {
        runOnUiThread(() -> {
            if (orderPaymentDetail != null && orderPaymentDetail.getProductGlassPayments() != null && !orderPaymentDetail.getProductGlassPayments().isEmpty()) {
                ProductGlassPayment product = orderPaymentDetail.getProductGlassPayments().get(0); // Assuming one product for simplicity
                textviewProductName.setText("Tên Gọng: " + product.getEyeGlass().getName());
                textViewProductID.setText("Mã Sản Phẩm: " + product.getProductGlassID());
                textViewLeftEye.setText("Len Trái: " + (product.getLeftLens() != null ? product.getLeftLens().getLensName() : "N/A"));
                textViewRightEye.setText("Len Phải: " + (product.getRightLens() != null ? product.getRightLens().getLensName() : "N/A"));
            } else {
                textViewProductID.setText("Mã Sản Phẩm: N/A");
                textViewLeftEye.setText("Len Trái: N/A");
                textViewRightEye.setText("Len Phải: N/A");
            }
        });
    }


    private void handleProductGlassError(TextView textViewProductID, TextView textViewLeftEye, TextView textViewRightEye,TextView textviewProductName, String errorMessage) {
        runOnUiThread(() -> {
            textViewProductID.setText("Product ID: N/A");
            textViewLeftEye.setText("Left Lens: N/A");
            textViewRightEye.setText("Right Lens: N/A");
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
    private void setupRecyclerViewScrollListener() {
        recyclerViewReports.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPage++;  // Load next page
                            searchButton.setOnClickListener(v -> {
                                String query = searchInput.getText().toString().trim();
                                fetchReportsAndDetails(query);
                            });

                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<String> getStatusList() {
        List<String> statusList = new ArrayList<>();
        statusList.add("Chưa chọn");
        statusList.add("Yêu cầu");
        statusList.add("Bị từ chối");
        statusList.add("Được chấp nhận");
        return statusList;
    }

    private List<String> getTypeList() {
        List<String> typeList = new ArrayList<>();
        typeList.add("Chưa chọn");
        typeList.add("Vấn đề sản phẩm");
        typeList.add("Vấn đề giao hàng");
        typeList.add("Vấn đề khách hàng");
        typeList.add("Dịch vụ khách hàng");
        typeList.add("Khác");
        return typeList;
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

    }
    private void handleNoData() {
        reportDataList.clear();
        reportAdapter.notifyDataSetChanged();
        recyclerViewReports.setVisibility(View.GONE);
        noDataView.setVisibility(View.VISIBLE);
        showLoading(false);
        isLoading = false;
    }


    private void fetchReports(String query) {

        isLoading = true;
        showLoading(true);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            handleNoData();
            showLoading(false);
            isLoading = false;
            return;
        }

        StringBuilder apiUrl = new StringBuilder(Config.getBaseUrl() + "/api/reports?PageIndex=" + currentPage + "&PageSize=" + pageSize + "&Descending=true");
        if (!selectedStatus.equals("Chưa chọn")) apiUrl.append("&Status=").append(getStatusValue(selectedStatus));
        if (!selectedType.equals("Chưa chọn")) apiUrl.append("&Type=").append(getTypeValue(selectedType));
        if (query != null && !query.isEmpty()) apiUrl.append("&OrderID=").append(query);

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl.toString());
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

                    ReportResponse reportResponse = new Gson().fromJson(response.toString(), ReportResponse.class);
                    List<ReportData> newReportData = reportResponse.getData();

                    runOnUiThread(() -> {
                        // Kiểm tra nếu `data` rỗng hoặc `totalCount == 0`
                        if (newReportData == null || newReportData.isEmpty()) {
                            handleNoData();
                            noDataView.setVisibility(View.VISIBLE);
                        } else {
                            if (currentPage == 1) reportDataList.clear();
                            reportDataList.addAll(newReportData);

                            recyclerViewReports.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                            reportAdapter.notifyDataSetChanged();
                        }

                        showLoading(false);
                        isLoading = false;

                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show();
                        handleNoData();
                        showLoading(false);
                        isLoading = false;
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching reports", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu báo cáo.", Toast.LENGTH_SHORT).show();
                    handleNoData();
                    showLoading(false);
                    isLoading = false;
                });
            }
        }).start();
    }





    private void fetchReportDetails(int reportId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            handleNoData();
            showLoading(false);
            return;
        }

        String apiUrl = Config.getBaseUrl() + "/api/reports/" + reportId;

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
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

                    ReportData reportDetails = new Gson().fromJson(response.toString(), ReportData.class);

                    runOnUiThread(() -> {
                        reportDataList.clear();
                        reportDataList.add(reportDetails);
                        reportAdapter.notifyDataSetChanged();
                        recyclerViewReports.setVisibility(View.VISIBLE);
                        noDataView.setVisibility(View.GONE);
                        showLoading(false);
                        isLoading = false;
                    });
                } else {
                    // Nếu lỗi HTTP, chuyển sang fetchReports
                    runOnUiThread(() -> {
                        Log.e(TAG, "fetchReportDetails: HTTP Error " + responseCode);
                         // Thử tìm dữ liệu bằng fetchReports
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching report details", e);
                runOnUiThread(() -> fetchReports(String.valueOf(reportId))); // Chuyển sang fetchReports khi có lỗi
            }
        }).start();
    }






    private void fetchReportsAndDetails(String query) {
        if (isLoading) return;

        isLoading = true;
        showLoading(true);

        if (!query.isEmpty()) {
            try {
                int reportId = Integer.parseInt(query);
                fetchReports(query);
                fetchReportDetails(reportId); // Thử tìm chi tiết trước

            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID báo cáo không hợp lệ.", Toast.LENGTH_SHORT).show();
                handleNoData();
                showLoading(false);
                isLoading = false;
            }
        } else {
            fetchReports(null); // Nếu không có query, chỉ tải danh sách
        }
    }




    private int getStatusValue(String status) {
        switch (status) {
            case "Yêu cầu": return 0;
            case "Bị từ chối": return 1;
            case "Được chấp nhận": return 2;
            default: return -1;
        }
    }

    private int getTypeValue(String type) {
        switch (type) {
            case "Vấn đề sản phẩm": return 0;
            case "Vấn đề giao hàng": return 1;
            case "Vấn đề khách hàng": return 2;
            case "Dịch vụ khách hàng": return 3;
            case "Khác": return 4;
            default: return -1;
        }
    }
}
