package com.example.glass_project.product.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.model.profile.Profile;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileDetailActivity extends AppCompatActivity {

    private EditText etFullName, etPhoneNumber, etAddress, etBirthday;
    private TextView tvFullName, tvPhoneNumber, tvAddress, tvBirthday;
    private ImageView imgProfile;
    private Button btnUpdateProfile,btnListRefractionRecord,btnListVisualAcuityRecord;
    private boolean isEditing = false;

    private Profile profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // Initialize views
        tvFullName = findViewById(R.id.tvFullName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvBirthday = findViewById(R.id.tvBirthday);
        imgProfile = findViewById(R.id.imgProfile);

        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        etBirthday = findViewById(R.id.etBirthday);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnListRefractionRecord = findViewById(R.id.btnListRefractionRecord);
        btnListVisualAcuityRecord = findViewById(R.id.btnListVisualAcuityRecord);
        // Get profile data from intent
        profile = (Profile) getIntent().getSerializableExtra("profile");
        if (profile == null) {
            Toast.makeText(this, "Profile data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        btnListVisualAcuityRecord.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileDetailActivity.this, com.example.glass_project.product.ui.visualAcuityRecord.VisualAcuityRecordActivity.class);
            intent.putExtra("profileID", profile.getId());
            startActivity(intent);
        });
        displayProfileData();

        // Button to enable editing or save changes
        btnUpdateProfile.setOnClickListener(v -> {
            if (isEditing) {
                saveProfileChanges();
                btnUpdateProfile.setText("Update Profile");
                isEditing = false;
            } else {
                enableEditing();
                btnUpdateProfile.setText("Save");
                isEditing = true;
            }
        });
    }

    private void displayProfileData() {
        tvFullName.setText(profile.getFullName());
        tvPhoneNumber.setText(profile.getPhoneNumber());
        tvAddress.setText(profile.getAddress());
        tvBirthday.setText(profile.getBirthday());

        if (profile.getUrlImage() != null && !profile.getUrlImage().isEmpty()) {
            Glide.with(this).load(profile.getUrlImage()).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.default_image);
        }
    }

    private void enableEditing() {
        tvFullName.setVisibility(View.GONE);
        tvPhoneNumber.setVisibility(View.GONE);
        tvAddress.setVisibility(View.GONE);
        tvBirthday.setVisibility(View.GONE);

        etFullName.setVisibility(View.VISIBLE);
        etPhoneNumber.setVisibility(View.VISIBLE);
        etAddress.setVisibility(View.VISIBLE);
        etBirthday.setVisibility(View.VISIBLE);

        etFullName.setText(profile.getFullName());
        etPhoneNumber.setText(profile.getPhoneNumber());
        etAddress.setText(profile.getAddress());
        etBirthday.setText(profile.getBirthday());
    }

    private void saveProfileChanges() {
        profile.setFullName(etFullName.getText().toString());
        profile.setPhoneNumber(etPhoneNumber.getText().toString());
        profile.setAddress(etAddress.getText().toString());
        profile.setBirthday(etBirthday.getText().toString());

        new UpdateProfileTask().execute(profile);
    }

    private class UpdateProfileTask extends AsyncTask<Profile, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Profile... profiles) {
            try {
                Profile updatedProfile = profiles[0];
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return false;
                }

                String BaseUrl = Config.getBaseUrl();
                URL url = new URL(BaseUrl + "/api/profiles");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id", updatedProfile.getId());
                jsonParam.put("accountID", updatedProfile.getAccountID());
                jsonParam.put("fullName", updatedProfile.getFullName());
                jsonParam.put("phoneNumber", updatedProfile.getPhoneNumber());
                jsonParam.put("address", updatedProfile.getAddress());
                jsonParam.put("urlImage", updatedProfile.getUrlImage());
                jsonParam.put("birthday", updatedProfile.getBirthday());
                jsonParam.put("status", updatedProfile.isStatus());

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ProfileDetailActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                // Trả kết quả về ProfileFragment để tải lại dữ liệu
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish(); // Đóng activity sau khi cập nhật thành công
            } else {
                Toast.makeText(ProfileDetailActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
