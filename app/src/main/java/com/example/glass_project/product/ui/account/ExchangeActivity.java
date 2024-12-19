package com.example.glass_project.product.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.ExchangeAdapter;
import com.example.glass_project.data.model.other.ExchangeItem;
import com.example.glass_project.data.model.other.ExchangeResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExchangeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View noDataView;  // Updated to View since noDataView is likely a container layout
    private ProgressBar progressBar;
    private ExchangeAdapter adapter;
    private List<ExchangeItem> exchangeItems = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private final int pageSize = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        recyclerView = findViewById(R.id.recyclerViewExchange);
        noDataView = findViewById(R.id.noDataView);  // Correct type
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExchangeAdapter(this, exchangeItems);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerViewScrollListener();
        fetchExchangeData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            currentPage++;
                            fetchExchangeData();
                        }
                    }
                }
            }
        });
    }

    private void fetchExchangeData() {
        if (isLoading) return;

        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("id", "");
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show());
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                String BaseUrl = Config.getBaseUrl();
                URL url = new URL(BaseUrl + "/api/accounts/exchange-eyeglasses?AccountID=" + accountId
                        + "&PageIndex=" + currentPage + "&PageSize=" + pageSize + "&Descending=false");

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

                    ExchangeResponse exchangeResponse = new Gson().fromJson(response.toString(), ExchangeResponse.class);
                    List<ExchangeItem> items = exchangeResponse.getData();

                    runOnUiThread(() -> {
                        if (currentPage == 1 && items.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noDataView.setVisibility(View.VISIBLE);
                            isLastPage = true;
                        } else {
                            if (items.size() < pageSize) {
                                isLastPage = true;
                            }

                            exchangeItems.addAll(items);
                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    });
                } else {
                    runOnUiThread(() -> {
                        recyclerView.setVisibility(View.GONE);
                        noDataView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    });
                }

                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> {
                    recyclerView.setVisibility(View.GONE);
                    noDataView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
                e.printStackTrace();
            }
        }).start();
    }
}

