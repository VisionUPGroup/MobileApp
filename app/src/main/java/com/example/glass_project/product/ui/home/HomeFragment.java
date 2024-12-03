package com.example.glass_project.product.ui.home;

import android.content.Context;
import android.content.Intent;
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
import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.config.services.MyFirebaseMessagingService;
import com.example.glass_project.data.adapter.BannerAdapter;
import com.example.glass_project.data.adapter.OrderHistoryAdapter;
import com.example.glass_project.data.model.order.OrderHistoryItem;
import com.example.glass_project.data.model.order.OrderHistoryResponse;
import com.example.glass_project.databinding.FragmentHomeBinding;
import com.example.glass_project.product.ui.order.history.ListOrderHistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private View noDataView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        fetchFirebaseToken();
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

        // Fetch order history data with only the three most recent items
        fetchOrderHistory();
        setupLinearLayoutClickEvents();

        return root;
    }
    private void fetchFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Lấy Device Token
                    String deviceToken = task.getResult();
                    Log.d(TAG, "Device Token: " + deviceToken);

                    // Kiểm tra và gửi Device Token lên server
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("deviceToken", deviceToken);
                    editor.apply();  // Apply changes asynchronously
                    String accountId = sharedPreferences.getString("id", null);
                    if (accountId != null) {
                        sendDeviceTokenToServerDirect(accountId, deviceToken);
                    } else {
                        Log.e(TAG, "Account ID is null. Cannot send device token.");
                    }
                });
    }

    // Gửi Device Token lên API trực tiếp
    private void sendDeviceTokenToServerDirect(String accountId, String deviceToken) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                String BaseUrl = baseUrl.BASE_URL;// Tạo kết nối tới API
                String apiUrl = BaseUrl +"/api/notifications/device-tokens";

                // Tạo JSON request body
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accountId", accountId);
                jsonObject.put("deviceToken", deviceToken);
                jsonObject.put("deviceInfo", "Android - " + android.os.Build.MODEL);

                // Mở kết nối
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "*/*");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Ghi dữ liệu vào body request
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Đọc phản hồi từ server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("API", "Response: " + response.toString());
                    }
                } else {
                    Log.e("API", "Error: Response code " + responseCode);
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = br.readLine()) != null) {
                            errorResponse.append(errorLine.trim());
                        }
                        Log.e("API", "Error Response: " + errorResponse.toString());
                    }
                }

            } catch (Exception e) {
                Log.e("API", "Exception: " + e.getMessage(), e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }



    private void setupLinearLayoutClickEvents() {
        // Lấy BottomNavigationView từ Activity
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        binding.profileLinearLayout.setOnClickListener(v -> {
            // Chuyển đến mục "Profile"
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        });

        binding.testLinearLayout.setOnClickListener(v -> {
            // Chuyển đến mục "Eye Check"
            bottomNavigationView.setSelectedItemId(R.id.navigation_eye_check);
        });

        binding.mapLinearLayout.setOnClickListener(v -> {
            // Chuyển đến mục "Map"
            bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        });

        binding.historyOrderLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListOrderHistoryActivity.class);
            startActivity(intent);
        });
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
            if (!isAdded()) {
                Log.e(TAG, "Fragment not attached to Activity. Aborting fetchOrderHistory.");
                return;
            }

            try {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("id", "");
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show()
                    );
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
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

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            orderHistoryItems.clear();
                            orderHistoryItems.addAll(newItems);
                            orderHistoryAdapter.notifyDataSetChanged();
                        });
                    }
                } else {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Lỗi kết nối: " + responseCode, Toast.LENGTH_SHORT).show();
                            signOutAndStartSignInActivityFromHomeFragment(); // Gọi phương thức từ AccountFragment
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "fetchOrderHistory: ", e);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show()

                    );
                }
            }
        }).start();
    }
    private void signOutAndStartSignInActivityFromHomeFragment() {
        // Lấy reference của AccountFragment từ FragmentManager
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String accountId = sharedPreferences.getString("id", "");
        String deviceToken = sharedPreferences.getString("deviceToken", "");

        if (!accountId.isEmpty() && !deviceToken.isEmpty()) {
            new MyFirebaseMessagingService().deleteNotification(accountId, deviceToken);
        }
        editor.clear();
        editor.apply();

        // Chuyển về MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Kết thúc Activity hiện tại để không quay lại được
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
    public void onPause() {
        super.onPause();
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //clearExamData();
        if (bannerHandler != null) {
            bannerHandler.postDelayed(bannerRunnable, 5000);
        }
    }
    private void clearExamData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("numberOfTest_left");
        editor.remove("myopia_left");
        editor.remove("numberOfTest_right");
        editor.remove("myopia_right");
        editor.apply();
        SharedPreferences sharedPreference = requireContext().getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedPreference.edit();
        editors.remove("ExamData_left");
        editors.remove("ExamData_right");
        editors.apply();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        binding = null;
    }

}
