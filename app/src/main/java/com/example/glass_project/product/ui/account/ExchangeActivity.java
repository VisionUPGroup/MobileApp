package com.example.glass_project.product.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
    private TextView noDataView;
    private ExchangeAdapter adapter;
    private List<ExchangeItem> exchangeItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        recyclerView = findViewById(R.id.recyclerViewExchange);
        noDataView = findViewById(R.id.noDataView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExchangeAdapter(this, exchangeItems);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fetchExchangeData();
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
    private void fetchExchangeData() {
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
                URL url = new URL(BaseUrl + "/api/accounts/exchange-eyeglasses?AccountID=" + accountId
                        + "&PageIndex=1&PageSize=10&Descending=false");

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

                    // Parse JSON vào ExchangeResponse
                    ExchangeResponse exchangeResponse = new Gson().fromJson(response.toString(), ExchangeResponse.class);

                    // Lấy danh sách data
                    List<ExchangeItem> items = exchangeResponse.getData();

                    runOnUiThread(() -> {
                        if (items.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noDataView.setVisibility(View.VISIBLE);
                        } else {
                            exchangeItems.clear();
                            exchangeItems.addAll(items);
                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        recyclerView.setVisibility(View.GONE);
                        noDataView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }

                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> {
                    recyclerView.setVisibility(View.GONE);
                    noDataView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        }).start();
    }


}
