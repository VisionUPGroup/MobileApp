package com.example.glass_project.product.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.config.services.MyFirebaseMessagingService;
import com.example.glass_project.product.ui.notifications.NotificationsActivity;
import com.example.glass_project.product.ui.order.history.ListOrderHistoryActivity;
import com.example.glass_project.product.ui.report.ListReportActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccountFragment extends Fragment {
    private Switch languageSwitch;
    private TextView usernameView, emailView, phoneView;
    private SharedPreferences sharedPreferences;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        usernameView = view.findViewById(R.id.username);
        emailView = view.findViewById(R.id.email);
        phoneView = view.findViewById(R.id.phone);
        TextView userOrdersButton = view.findViewById(R.id.user_order);
        TextView editProfileButton = view.findViewById(R.id.edit_profile);
        TextView changePasswordTextView = view.findViewById(R.id.changepassword);
        TextView exchangelassTextView = view.findViewById(R.id.exchangelass);
        Button logoutButton = view.findViewById(R.id.btn_sign_out);
        TextView notificationsTextView = view.findViewById(R.id.notifications);
        TextView report = view.findViewById(R.id.report);
        languageSwitch = view.findViewById(R.id.language_switch);

        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Fetch and display user data
        fetchUserData();

        // Language switch handling
        String currentLanguage = sharedPreferences.getString("Language", "en");
        languageSwitch.setChecked(currentLanguage.equals("vi"));

        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                changeLanguage(requireContext(), "vi");
            } else {
                changeLanguage(requireContext(), "en");
            }
        });

        editProfileButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        changePasswordTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));
        notificationsTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationsActivity.class)));
        exchangelassTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ExchangeActivity.class)));
        report.setOnClickListener(v -> startActivity(new Intent(getActivity(), ListReportActivity.class)));
        logoutButton.setOnClickListener(v -> signOutAndStartSignInActivity());
        userOrdersButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), ListOrderHistoryActivity.class)));

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Always fetch user data when the fragment is visible
        fetchUserData();
    }
    private void fetchUserData() {
        new Thread(() -> {
            try {
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = Config.getBaseUrl();
                URL url = new URL(BaseUrl+"/api/accounts/me");
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
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    String username = jsonResponse.getString("username");
                    String email = jsonResponse.getString("email");
                    String phone = jsonResponse.getString("phoneNumber");

                    // Update SharedPreferences with the fetched data
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.apply();

                    // Update UI with fetched data
                    requireActivity().runOnUiThread(() -> {
                        usernameView.setText(username);
                        emailView.setText(email);
                        phoneView.setText(phone);
                    });
                } else {
                    // Handle errors
                    requireActivity().runOnUiThread(() -> {
                        usernameView.setText("Error fetching data");
                        emailView.setText("");
                        phoneView.setText("");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    usernameView.setText("Error fetching data");
                    emailView.setText("");
                    phoneView.setText("");
                });
            }
        }).start();
    }

    public void changeLanguage(Context context, String languageCode) {
        // Change locale and update SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Language", languageCode);
        editor.apply();

        // Restart the activity to apply changes
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        requireActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void signOutAndStartSignInActivity() {
        // Clear user session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String accountId = sharedPreferences.getString("id", "");
        String deviceToken = sharedPreferences.getString("deviceToken", "");
        String accessToken = sharedPreferences.getString("accessToken", "");

        if (!accountId.isEmpty() && !deviceToken.isEmpty()) {
            new MyFirebaseMessagingService().deleteNotification(accessToken, accountId, deviceToken);
        }
        editor.clear();
        editor.apply();

        // Start MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
