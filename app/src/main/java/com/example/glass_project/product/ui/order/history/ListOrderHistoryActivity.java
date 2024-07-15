package com.example.glass_project.product.ui.order.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.example.glass_project.data.adapter.OrderHistoryAdapter;
import com.example.glass_project.data.model.OrderHistoryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListOrderHistoryActivity extends AppCompatActivity {

    private ListView listViewOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderHistoryItem> orderHistoryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order_history);

        listViewOrderHistory = findViewById(R.id.listViewOrderHistory);
        orderHistoryItems = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistoryItems);
        listViewOrderHistory.setAdapter(orderHistoryAdapter);
        TextView textViewEmpty = findViewById(R.id.textViewEmpty);
        // Get accountId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("userId", "");

        // Call your API to fetch order history data
        new FetchOrderHistoryTask().execute(accountId);
    }


    private class FetchOrderHistoryTask extends AsyncTask<String, Void, List<OrderHistoryItem>> {

        @Override
        protected List<OrderHistoryItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String orderHistoryJsonStr;
            String accountId = params[0]; // Get accountId from params

            try {
                URL url = new URL("https://visionup.azurewebsites.net/api/Order/account/" + accountId + "/mobile");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                orderHistoryJsonStr = buffer.toString();

                // Process JSON response
                Gson gson = new Gson();
                Type listType = new TypeToken<List<OrderHistoryItem>>() {}.getType();
                return gson.fromJson(orderHistoryJsonStr, listType);

            } catch (IOException e) {
                // Log error
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<OrderHistoryItem> orders) {
            if (orders != null && !orders.isEmpty()) {
                orderHistoryItems.addAll(orders);
                orderHistoryAdapter.notifyDataSetChanged();
                listViewOrderHistory.setVisibility(View.VISIBLE);
                findViewById(R.id.textViewEmpty).setVisibility(View.GONE);
            } else {
                // Handle case where orders list is empty or null
                listViewOrderHistory.setVisibility(View.GONE);
                findViewById(R.id.textViewEmpty).setVisibility(View.VISIBLE);
            }
        }

    }
}
