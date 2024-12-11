package com.example.glass_project.product.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.glass_project.R;
import com.example.glass_project.config.baseUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editEmail, editUsername, editPhone;
    private TextView roleTextView;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editEmail = findViewById(R.id.edit_email);
        editUsername = findViewById(R.id.edit_username);
        editPhone = findViewById(R.id.edit_phone);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        // Gọi API để lấy thông tin tài khoản chi tiết
        new FetchProfileTask().execute();

        // Lưu thay đổi khi người dùng nhấn nút
        btnSaveChanges.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String username = editUsername.getText().toString();
            String phone = editPhone.getText().toString();
            new UpdateProfileTask(email, username, phone).execute();
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    private class FetchProfileTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/me");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("accept", "*/*");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return new JSONObject(response.toString());
                }

            } catch (Exception e) {
                Log.e("EditProfileActivity", "Error fetching profile", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject profileData) {
            if (profileData != null) {
                try {
                    // Hiển thị thông tin tài khoản từ JSON
                    editEmail.setText(profileData.optString("email", ""));
                    editUsername.setText(profileData.optString("username", ""));
                    editPhone.setText(profileData.optString("phoneNumber", ""));

                    // Kiểm tra sự tồn tại của đối tượng "role" và hiển thị "name"
                    if (profileData.has("role") && !profileData.isNull("role")) {
                        JSONObject role = profileData.getJSONObject("role");
                    } else {
                        roleTextView.setText("Role not available");
                    }

                } catch (Exception e) {
                    Log.e("EditProfileActivity", "Error parsing profile data", e);
                    Toast.makeText(EditProfileActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditProfileActivity.this, "Error fetching profile data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class UpdateProfileTask extends AsyncTask<Void, Void, String> {
        private String email, username, phone;

        UpdateProfileTask(String email, String username, String phone) {
            this.email = email;
            this.username = username;
            this.phone = phone;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                int userId = Integer.parseInt(sharedPreferences.getString("id", "-1"));

                if (userId == -1 || accessToken.isEmpty()) {
                    return "User session invalid. Please log in again.";
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userId);
                jsonObject.put("email", email);
                jsonObject.put("username", username);
                jsonObject.put("phoneNumber", phone);

                urlConnection.getOutputStream().write(jsonObject.toString().getBytes());

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "success";
                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    // Read the error response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }

                    // Check if the error message contains the specific text
                    if (errorResponse.toString().contains("[\"Username, Email, or PhoneNumber already exists.\"]")) {
                        return "Tên đăng nhập hoặc số điện thoại đã tồn tại.";
                    } else {
                        return "Không cập nhật được hồ sơ. Vui lòng kiểm tra thông tin của bạn..";
                    }
                } else {
                    return "Unexpected error occurred. Response code: " + responseCode;
                }

            } catch (Exception e) {
                Log.e("EditProfileActivity", "Error updating profile", e);
                return "Error updating profile. Please try again later.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if ("success".equals(result)) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Show specific error message if update failed
                Toast.makeText(EditProfileActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
