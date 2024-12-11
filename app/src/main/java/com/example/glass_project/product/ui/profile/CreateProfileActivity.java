package com.example.glass_project.product.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.glass_project.R;
import com.example.glass_project.config.baseUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
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

    private static final int REQUEST_IMAGE_PICK = 101;

    private EditText editFullName, editPhoneNumber, editAddress, editBirthday;
    private ImageView imgSelected;
    private Button btnCreateProfile, btnCancel, btnSelectImage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Khởi tạo các thành phần giao diện
        editFullName = findViewById(R.id.editFullName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);
        editBirthday = findViewById(R.id.editBirthday);
        imgSelected = findViewById(R.id.imgSelected);
        btnCreateProfile = findViewById(R.id.btnCreateProfile);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectImage = findViewById(R.id.btnSelectImage);

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

        // Xử lý sự kiện nút "Chọn ảnh"
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Xử lý sự kiện nút "Tạo hồ sơ"
        btnCreateProfile.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String phoneNumber = editPhoneNumber.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String birthday = editBirthday.getText().toString().trim();

            if (validateInputs(fullName, phoneNumber, address, birthday)) {
                disableAllInputs();
                new CreateProfileTask().execute(fullName, phoneNumber, address, birthday);
            }
        });

        // Xử lý sự kiện nút "Hủy"
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Xử lý kết quả trả về từ trình chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgSelected.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn
            }
        }
    }

    // Vô hiệu hóa tất cả input và nút
    private void disableAllInputs() {
        editFullName.setEnabled(false);
        editPhoneNumber.setEnabled(false);
        editAddress.setEnabled(false);
        editBirthday.setEnabled(false);
        btnCreateProfile.setEnabled(false);
        btnCancel.setEnabled(false);
        btnSelectImage.setEnabled(false);
    }

    // Kích hoạt lại tất cả input và nút
    private void enableAllInputs() {
        editFullName.setEnabled(true);
        editPhoneNumber.setEnabled(true);
        editAddress.setEnabled(true);
        editBirthday.setEnabled(true);
        btnCreateProfile.setEnabled(true);
        btnCancel.setEnabled(true);
        btnSelectImage.setEnabled(true);
    }

    // Hàm kiểm tra tính hợp lệ của dữ liệu
    private boolean validateInputs(String fullName, String phoneNumber, String address, String birthday) {
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên.", Toast.LENGTH_SHORT).show();
            return false;
        }

        Pattern phonePattern = Pattern.compile("^[0-9]{10,11}$");
        if (phoneNumber.isEmpty() || !phonePattern.matcher(phoneNumber).matches()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ (10-11 chữ số).", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (birthday.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(birthday);
        } catch (Exception e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class CreateProfileTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String accountId = sharedPreferences.getString("id", "");

                if (accessToken.isEmpty() || accountId.isEmpty()) {
                    Log.e("CreateProfileActivity", "Access token or account ID not found");
                    return null;
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
                profileData.put("birthday", formatDate(params[3]));

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = profileData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String response = in.readLine();
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getInt("id"); // Lấy ID từ response
                }

            } catch (Exception e) {
                Log.e("CreateProfileActivity", "Exception: " + e.getMessage(), e);
            }
            return null;
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
        protected void onPostExecute(Integer profileId) {
            if (profileId != null) {
                if (selectedImageUri != null) {
                    new UploadImageTask(profileId).execute(selectedImageUri);
                } else {
                    Toast.makeText(CreateProfileActivity.this, "Tạo hồ sơ thành công.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(CreateProfileActivity.this, "Tạo hồ sơ thất bại.", Toast.LENGTH_SHORT).show();
                enableAllInputs();
            }
        }
    }

    private class UploadImageTask extends AsyncTask<Uri, Void, Boolean> {
        private int profileId;

        public UploadImageTask(int profileId) {
            this.profileId = profileId;
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {
            try {
                Uri imageUri = uris[0];
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                String uploadUrl = baseUrl.BASE_URL + "/api/accounts/profiles/upload_image";
                HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary");
                connection.setDoOutput(true);

                String boundary = "----WebKitFormBoundary";

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    // Thêm phần dữ liệu Id
                    outputStream.writeBytes("--" + boundary + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"Id\"\r\n\r\n");
                    outputStream.writeBytes(profileId + "\r\n");

                    // Thêm phần dữ liệu hình ảnh
                    outputStream.writeBytes("--" + boundary + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"Image\"; filename=\"image.jpg\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                    // Ghi dữ liệu file hình ảnh
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.writeBytes("\r\n");

                    // Kết thúc multipart
                    outputStream.writeBytes("--" + boundary + "--\r\n");
                    outputStream.flush();
                }

                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                Log.e("UploadImageTask", "Error uploading image", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CreateProfileActivity.this, "Tạo hồ sơ thành công.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Trả về kết quả thành công
                finish();
            } else {
                Toast.makeText(CreateProfileActivity.this, "Tải ảnh thất bại.", Toast.LENGTH_SHORT).show();
                enableAllInputs();
            }
        }
    }
}
