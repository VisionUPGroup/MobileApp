package com.example.glass_project.product.ui.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.product.ui.other.ProductsActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFragment extends Fragment {

    private EditText username, password, confirmPassword, email, phoneNumber;
    private ImageView imgTogglePassword, imgToggleConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private static final String TAG = "RegisterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Ánh xạ các view
        username = view.findViewById(R.id.editUsername);
        password = view.findViewById(R.id.editPassword);
        confirmPassword = view.findViewById(R.id.editConfirmPassword);
        email = view.findViewById(R.id.editEmailAddress);
        phoneNumber = view.findViewById(R.id.editPhoneNumber);
        Button registerButton = view.findViewById(R.id.btn_sign_up);
        imgTogglePassword = view.findViewById(R.id.imgTogglePassword);
        imgToggleConfirmPassword = view.findViewById(R.id.imgToggleConfirmPassword);

        // Toggle mật khẩu hiển thị/ẩn
        imgTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        imgToggleConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        // Xử lý sự kiện khi bấm nút đăng ký
        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser(
                        username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        phoneNumber.getText().toString()
                );
            }
        });

        return view;
    }

    // Phương thức hiển thị/ẩn mật khẩu
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgTogglePassword.setImageResource(R.drawable.baseline_remove_red_eye_24); // Closed eye icon
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            imgTogglePassword.setImageResource(R.drawable.eye_open); // Open eye icon
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.length());
    }

    // Phương thức hiển thị/ẩn xác nhận mật khẩu
    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgToggleConfirmPassword.setImageResource(R.drawable.baseline_remove_red_eye_24); // Closed eye icon
        } else {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            imgToggleConfirmPassword.setImageResource(R.drawable.eye_open); // Open eye icon
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPassword.setSelection(confirmPassword.length());
    }

    // Phương thức kiểm tra đầu vào
    private boolean validateInputs() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String phoneInput = phoneNumber.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            username.setError("Tên đăng nhập là bắt buộc");
            return false;
        }

        if (emailInput.isEmpty()) {
            email.setError("Email là bắt buộc");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Vui lòng nhập địa chỉ email hợp lệ");
            return false;
        }

        if (phoneInput.isEmpty()) {
            phoneNumber.setError("Số điện thoại là bắt buộc");
            return false;
        } else if (!Patterns.PHONE.matcher(phoneInput).matches() || phoneInput.length() != 10) {
            phoneNumber.setError("Vui lòng nhập số điện thoại hợp lệ gồm 10 chữ số");
            return false;
        }

        if (passwordInput.isEmpty()) {
            password.setError("Mật khẩu là bắt buộc");
            return false;
        } else if (passwordInput.length() < 6) {
            password.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!passwordInput.equals(confirmPasswordInput)) {
            confirmPassword.setError("Mật khẩu không khớp");
            return false;
        }

        return true;
    }

    // Phương thức gọi API để đăng ký
    private void registerUser(String username, String password, String email, String phoneNumber) {
        new RegisterUserTask().execute(username, password, email, phoneNumber);
    }

    // AsyncTask xử lý API
    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            String phoneNumber = params[3];

            try {
                String BaseUrl = Config.getBaseUrl();
                String baseUrl = BaseUrl + "/api/accounts/register";
                URL url = new URL(baseUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Tạo body request
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                jsonBody.put("email", email);
                jsonBody.put("phoneNumber", phoneNumber);

                // Gửi dữ liệu
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {

                    return "Đăng kí thành công";

                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorMessage = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorMessage.append(line);
                    }
                    String message ="";
                    if (errorMessage.toString().contains("[\"Username or Email or PhoneNumber is already exist.\"]")) {
                        message=" Tên đăng nhập hoặc số điện thoại đã tồn tại";
                    }
                    return  message.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage(), e);
                return "Error during registration";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
            if (result.equals("Đăng kí thành công")) {
                navigateToMainActivity();
            }
        }
    }

    // Chuyển đến màn hình chính sau khi đăng ký thành công
    private void navigateToMainActivity() {
        Intent intent = new Intent(requireContext(), ProductsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
