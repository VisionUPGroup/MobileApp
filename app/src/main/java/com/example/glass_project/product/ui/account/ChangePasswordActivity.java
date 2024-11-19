package com.example.glass_project.product.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassword, newPassword, confirmNewPassword;
    private ImageView imgToggleOldPassword, imgToggleNewPassword, imgToggleConfirmPassword;
    private boolean isOldPasswordVisible = false, isNewPasswordVisible = false, isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.edit_old_password);
        newPassword = findViewById(R.id.edit_new_password);
        confirmNewPassword = findViewById(R.id.edit_confirm_password);
        Button btnChangePassword = findViewById(R.id.btn_change_password);

        imgToggleOldPassword = findViewById(R.id.imgToggleOldPassword);
        imgToggleNewPassword = findViewById(R.id.imgToggleNewPassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);

        setupTogglePasswordVisibility();
        btnChangePassword.setOnClickListener(v -> {
            String oldPwd = oldPassword.getText().toString();
            String newPwd = newPassword.getText().toString();
            String confirmPwd = confirmNewPassword.getText().toString();

            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            } else {
                new ChangePasswordTask(oldPwd, newPwd).execute();
            }
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

    private void setupTogglePasswordVisibility() {
        imgToggleOldPassword.setOnClickListener(v -> {
            isOldPasswordVisible = togglePasswordVisibility(oldPassword, imgToggleOldPassword, isOldPasswordVisible);
        });
        imgToggleNewPassword.setOnClickListener(v -> {
            isNewPasswordVisible = togglePasswordVisibility(newPassword, imgToggleNewPassword, isNewPasswordVisible);
        });
        imgToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = togglePasswordVisibility(confirmNewPassword, imgToggleConfirmPassword, isConfirmPasswordVisible);
        });
    }

    private boolean togglePasswordVisibility(EditText passwordField, ImageView toggleIcon, boolean isVisible) {
        if (isVisible) {
            // Set to password hidden state
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.baseline_remove_red_eye_24); // Hide icon
        } else {
            // Set to password visible state
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.eye_open); // Show icon
        }
        passwordField.setSelection(passwordField.getText().length()); // Move cursor to the end
        return !isVisible; // Return the new visibility state
    }

    private class ChangePasswordTask extends AsyncTask<Void, Void, String> {
        private final String oldPwd;
        private final String newPwd;

        ChangePasswordTask(String oldPwd, String newPwd) {
            this.oldPwd = oldPwd;
            this.newPwd = newPwd;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                URL url = new URL(baseUrl.BASE_URL + "/api/accounts/change-password");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("oldPassword", oldPwd);
                jsonObject.put("newPassword", newPwd);

                urlConnection.getOutputStream().write(jsonObject.toString().getBytes());

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "success";
                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    if (errorResponse.toString().contains("Old password is incorrect")) {
                        return "Mật khẩu cũ không chính xác.";
                    }
                    return "Cập nhật mật khẩu thất bại. Kiểm tra lại thông tin.";
                } else {
                    return "Lỗi không xác định. Mã lỗi: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("ChangePasswordActivity", "Lỗi cập nhật mật khẩu", e);
                return "Lỗi cập nhật mật khẩu. Thử lại sau.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if ("success".equals(result)) {
                Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ChangePasswordActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
