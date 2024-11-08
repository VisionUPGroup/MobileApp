package com.example.glass_project.product.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.config.repositories.EyeGlassRepositories;
import com.example.glass_project.config.services.EyeGlassServices;
import com.example.glass_project.data.adapter.BannerAdapter;
import com.example.glass_project.data.adapter.GlassAdapter;
import com.example.glass_project.data.adapter.OrderHistoryAdapter;
import com.example.glass_project.data.model.EyeGlass;
import com.example.glass_project.data.model.ResponseData;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.data.model.order.OrderHistoryResponse;
import com.example.glass_project.databinding.FragmentHomeBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private ViewPager2 bannerViewPager;
    private int currentBannerPosition = 0;

    // Variables for order history
    private RecyclerView recyclerViewOrderHistory;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<OrderHistoryItem> orderHistoryItems = new ArrayList<>();
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EyeGlassServices eyeGlassServices = EyeGlassRepositories.getEyeGlassServices();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Display greeting with username
        TextView greetingTextView = binding.greetingText;
        String greetingMessage = getGreetingMessage();
        greetingTextView.setText(greetingMessage + username);

        // Set up ViewPager2 for banners
        setupBanner();

        // Initialize RecyclerView for order history
        setupOrderHistoryRecyclerView();

        // Load GIFs into icons using Glide
        loadIconsWithGlide();

        // Fetch glasses for the RecyclerView (existing code)
        fetchEyeGlasses(eyeGlassServices);

        // Fetch order history data with only the three most recent items
        fetchOrderHistory();

        return root;
    }

    private void setupBanner() {
        bannerViewPager = binding.bannerViewpager;
        List<String> bannerUrls = new ArrayList<>();
        bannerUrls.add("https://l450v.alamy.com/450v/2h7p1ek/eyeglasses-sale-banner-concept-optical-glasses-with-tropical-plants-2h7p1ek.jpg");
        bannerUrls.add("https://thumbs.dreamstime.com/z/eyeglasses-sale-banner-concept-optical-glasses-pastel-color-background-154221234.jpg");
        bannerUrls.add("https://st4.depositphotos.com/11133046/28827/v/450/depositphotos_288270776-stock-illustration-eyeglasses-banner-concept-with-palm.jpg");

        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), bannerUrls);
        bannerViewPager.setAdapter(bannerAdapter);

        // Set auto-scroll for banners
        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = () -> {
            currentBannerPosition++;
            if (currentBannerPosition >= bannerUrls.size()) {
                currentBannerPosition = 0;
            }
            bannerViewPager.setCurrentItem(currentBannerPosition, true);
            bannerHandler.postDelayed(bannerRunnable, 5000);
        };
        bannerHandler.postDelayed(bannerRunnable, 5000);
    }

    private void setupOrderHistoryRecyclerView() {
        recyclerViewOrderHistory = binding.recyclerViewOrderHistory;
        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), orderHistoryItems);
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);
    }

    private void loadIconsWithGlide() {
        Glide.with(this).asGif().load(R.drawable.report).into(binding.courseImageView);
        Glide.with(this).asGif().load(R.drawable.optometrist).into(binding.practiceImageView);
        Glide.with(this).asGif().load(R.drawable.map).into(binding.shoppingImageView);
        Glide.with(this).asGif().load(R.drawable.onlineorder).into(binding.rankingImageView);
    }

    private void fetchOrderHistory() {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("id", "");
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/orders?AccountID=" + accountId
                        + "&PageIndex=1&PageSize=3&Descending=true");

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

                    OrderHistoryResponse orderHistoryResponse = new Gson().fromJson(response.toString(), OrderHistoryResponse.class);
                    List<OrderHistoryItem> newItems = orderHistoryResponse.getData();

                    requireActivity().runOnUiThread(() -> {
                        orderHistoryItems.clear();
                        orderHistoryItems.addAll(newItems);
                        orderHistoryAdapter.notifyDataSetChanged();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show()
                    );
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchOrderHistory: ", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void fetchEyeGlasses(EyeGlassServices eyeGlassServices) {
        Call<ResponseData> call = eyeGlassServices.getGlasses();
        call.enqueue(new retrofit2.Callback<ResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseData> call, @NonNull retrofit2.Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EyeGlass> eyeGlasses = response.body().getData();
                    GlassAdapter glassAdapter = new GlassAdapter(getContext(), eyeGlasses);
                    // Uncomment the line below to set the adapter if you have a RecyclerView for glasses
                    // binding.kioskRecyclerView.setAdapter(glassAdapter);
                } else {
                    Log.e("API Error", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseData> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage(), t);
            }
        });
    }

    private String getGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return "Chào buổi sáng, ";
        } else if (hour >= 12 && hour < 18) {
            return "Chào buổi chiều, ";
        } else {
            return "Chào buổi tối, ";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacks(bannerRunnable);
        binding = null;
    }
}
