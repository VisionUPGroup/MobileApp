package com.example.glass_project.product.ui.Report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.ReportAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);  // Replace with your layout resource

        // Initialize views
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerType = findViewById(R.id.spinner_type);
        recyclerViewReports = findViewById(R.id.recycler_view_reports);
        noDataView = findViewById(R.id.noDataView);

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
        // Set up Spinner item listeners
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedStatus = (String) parentView.getItemAtPosition(position);
                currentPage = 1;  // Reset page when filter changes
                fetchReports();  // Reload reports with the new filter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedStatus = "Chưa chọn";
                fetchReports();
            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedType = (String) parentView.getItemAtPosition(position);
                currentPage = 1;  // Reset page when filter changes
                fetchReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedType = "Chưa chọn";
                fetchReports();
            }
        });

        // Initialize report data list
        reportDataList = new ArrayList<>();

        // Set up RecyclerView
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, reportDataList);
        recyclerViewReports.setAdapter(reportAdapter);

        // Load reports on activity start
        fetchReports();
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

    private void fetchReports() {
        if (isLoading) return;

        isLoading = true;

        // Get the access token and account ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("id", "");
        String accessToken = sharedPreferences.getString("accessToken", "");

        if (accountId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        // Build the API URL
        StringBuilder apiUrl = new StringBuilder(baseUrl.BASE_URL + "/api/reports?PageIndex=" + currentPage + "&PageSize=" + pageSize + "&Descending=true");

        // Add filters based on user selection
        if (!selectedStatus.equals("Chưa chọn")) {
            apiUrl.append("&Status=" + getStatusValue(selectedStatus));
        }
        if (!selectedType.equals("Chưa chọn")) {
            apiUrl.append("&Type=" + getTypeValue(selectedType));
        }

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
                    totalCount = reportResponse.getTotalCount();
                    runOnUiThread(() -> {
                        if (currentPage == 1) {
                            reportDataList.clear();  // Clear previous data for the new page
                        }

                        if (newReportData.isEmpty()) {
                            recyclerViewReports.setVisibility(View.GONE);
                            noDataView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewReports.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                        }

                        reportDataList.addAll(newReportData);
                        reportAdapter.notifyDataSetChanged();
                        isLoading = false;
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching reports", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu báo cáo.", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        }).start();
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
