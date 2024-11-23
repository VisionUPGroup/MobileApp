package com.example.glass_project.product.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText editFullName, editPhoneNumber, editAddress, editBirthday;
    private Button btnCreateProfile, btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Khởi tạo các thành phần giao diện
        editFullName = findViewById(R.id.editFullName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);
        editBirthday = findViewById(R.id.editBirthday);
        btnCreateProfile = findViewById(R.id.btnCreateProfile);
        btnCancel = findViewById(R.id.btnCancel);

        // Xử lý sự kiện khi nhấn vào editBirthday để hiển thị DatePickerDialog
        editBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateProfileActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        editBirthday.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Xử lý sự kiện nút "Create"
        btnCreateProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String phoneNumber = editPhoneNumber.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String urlImage = "testinmobile.com";
            String birthday = editBirthday.getText().toString().trim();

            if (validateInputs(fullName, phoneNumber, address, birthday)) {
                // Gọi API tạo profile
                new CreateProfileTask().execute(fullName, phoneNumber, address, urlImage, birthday);
            }
        });

        // Xử lý sự kiện nút "Cancel"
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Hàm kiểm tra tính hợp lệ của dữ liệu
    private boolean validateInputs(String fullName, String phoneNumber, String address, String birthday) {
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra số điện thoại (dùng regex để kiểm tra định dạng)
        Pattern phonePattern = Pattern.compile("^[0-9]{10,11}$");
        if (phoneNumber.isEmpty() || !phonePattern.matcher(phoneNumber).matches()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ (10-11 chữ số)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra ngày sinh (không được để trống và phải hợp lệ)
        if (birthday.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra định dạng ngày (yyyy-MM-dd)
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(birthday); // Ném lỗi nếu ngày không hợp lệ
        } catch (Exception e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class CreateProfileTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String accountId = sharedPreferences.getString("id", "");

                if (accessToken.isEmpty() || accountId.isEmpty()) {
                    Log.e("CreateProfileActivity", "Access token or account ID not found");
                    return false;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/accounts/profiles");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject profileData = new JSONObject();
                profileData.put("accountID", Integer.parseInt(accountId));
                profileData.put("fullName", params[0]);
                profileData.put("phoneNumber", params[1]);
                profileData.put("address", params[2]);
                profileData.put("urlImage", params[3].isEmpty() ? "default_image_url" : params[3]);
                String formattedBirthday = formatDate(params[4]);
                profileData.put("birthday", formattedBirthday);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = profileData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                Log.d("CreateProfileActivity", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("CreateProfileActivity", "Response: " + response.toString());
                    return false;
                }

            } catch (Exception e) {
                Log.e("CreateProfileActivity", "Exception: " + e.getMessage(), e);
                return false;
            }
        }

        private String formatDate(String date) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = inputFormat.parse(date);

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                return outputFormat.format(parsedDate);
            } catch (Exception e) {
                Log.e("CreateProfileActivity", "Error formatting date: " + e.getMessage());
                return date;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CreateProfileActivity.this, "Profile created successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(CreateProfileActivity.this, "Failed to create profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
