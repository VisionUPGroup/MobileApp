package com.example.glass_project.product.ui.eyeCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.ExamGridAdapter;
import com.example.glass_project.data.model.eyeCheck.Exam;
import com.example.glass_project.data.model.profile.Profile;
import com.example.glass_project.databinding.FragmentEyeCheckBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EyeCheckFragment extends Fragment implements SensorEventListener {
    private FragmentEyeCheckBinding binding;
    private List<Exam> examList = new ArrayList<>();
    private List<Profile> profileList = new ArrayList<>();
    private String profileID = ""; // Giá trị profileID được chọn
    private SensorManager sensorManager;
    private Sensor accelerometer,gyroscope;
    private boolean isTracking = false;
    private float totalDistance = 0f;
    private float initialX = 0f, initialY = 0f, initialZ = 0f; // Tọa độ ban đầu
    private float currentX = 0f, currentY = 0f, currentZ = 0f; // Tọa độ hiện tại
    private float velocityX = 0f, velocityY = 0f, velocityZ = 0f; // Vận tốc
    private float distanceX = 0f, distanceY = 0f, distanceZ = 0f; // Khoảng cách
    private float timeStep = 0.1f;
    private float roll = 0f, pitch = 0f, yaw = 0f;
    private Handler handler = new Handler();
    private Button btnAct;
    private TextView textViewDistance;
    private float initialDistance = 0f;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEyeCheckBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Spinner cho danh sách hồ sơ
        Spinner profileSpinner = binding.spinnerProfileID;

        // GridView cho danh sách bài kiểm tra
        GridView examGridView = binding.gridViewExamList;
        btnAct = binding.btnAct;
        textViewDistance = binding.textViewDistance;
        btnAct.setOnClickListener(v -> {
            if (isTracking) {
                // Dừng theo dõi
                stopTracking();
            } else {
                // Bắt đầu theo dõi
                startTracking();
            }
        });
        // Tải danh sách hồ sơ và bài kiểm tra
        loadProfiles();
        loadExams();
        clearExamData();
        // Xử lý sự kiện chọn bài kiểm tra từ GridView
        examGridView.setOnItemClickListener((parent, view, position, id) -> {
            Exam selectedExam = examList.get(position);

            // Lấy thông tin hồ sơ được chọn
            Profile selectedProfile = (Profile) profileSpinner.getSelectedItem();
            if (selectedProfile != null) {
                profileID = String.valueOf(selectedProfile.getId());

                // Lưu thông tin vào SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profileID", profileID);
                editor.putInt("selectedExamType", selectedExam.getId());
                editor.apply();

                // Chuyển đến EyeSelectionActivity
                Intent intent = new Intent(getActivity(), EyeSelectionActivity.class);
                startActivity(intent);
            } else {
                showError("Vui lòng chọn hồ sơ trước.");
            }
        });

        return rootView;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Lấy gia tốc hiện tại từ cảm biến (trong không gian thiết bị)
            float ax = event.values[0];  // Gia tốc theo trục X
            float ay = event.values[1];  // Gia tốc theo trục Y
            float az = event.values[2];  // Gia tốc theo trục Z
            Log.d("EyeCheckFragment", "Initial Acceleration - X: " + ax + ", Y: " + ay + ", Z: " + az);

            if (isTracking) {
                // Chuyển gia tốc từ không gian thiết bị sang không gian thế giới
                float[] worldAcc = applyRotationToAcceleration(ax, ay, az, roll, pitch, yaw);

                // Cập nhật gia tốc hiện tại
                currentX = worldAcc[0];
                currentY = worldAcc[1];
                currentZ = worldAcc[2];

                // Tính khoảng cách từ gia tốc đã chuyển đổi
                float distance = calculateEuclideanDistance(initialX, initialY, initialZ, currentX, currentY, currentZ);

                // Nếu là lần theo dõi đầu tiên, lưu khoảng cách ban đầu
                if (initialDistance == 0f) {
                    initialDistance = distance;
                }

                // Tính tổng khoảng cách đã di chuyển, trừ đi giá trị ban đầu
                totalDistance = distance - initialDistance;

                // Cập nhật giao diện người dùng
                textViewDistance.setText("Khoảng cách: " + String.format("%.5f", totalDistance) + " m");
            }
        }
    }

    private float[] applyRotationToAcceleration(float ax, float ay, float az, float roll, float pitch, float yaw) {
        // Tạo ma trận quay từ Roll, Pitch, Yaw
        float[][] rotationMatrix = new float[3][3];

        // Tạo ma trận quay (3x3) từ góc Roll, Pitch, Yaw (đơn vị radian)
        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);

        // Ma trận quay từ các góc Euler (Roll, Pitch, Yaw)
        rotationMatrix[0][0] = cosPitch * cosYaw;
        rotationMatrix[0][1] = cosPitch * sinYaw;
        rotationMatrix[0][2] = -sinPitch;

        rotationMatrix[1][0] = sinRoll * sinPitch * cosYaw - cosRoll * sinYaw;
        rotationMatrix[1][1] = sinRoll * sinPitch * sinYaw + cosRoll * cosYaw;
        rotationMatrix[1][2] = sinRoll * cosPitch;

        rotationMatrix[2][0] = cosRoll * sinPitch * cosYaw + sinRoll * sinYaw;
        rotationMatrix[2][1] = cosRoll * sinPitch * sinYaw - sinRoll * cosYaw;
        rotationMatrix[2][2] = cosRoll * cosPitch;

        // Áp dụng ma trận quay để chuyển gia tốc sang không gian thế giới
        float[] worldAcc = new float[3];
        worldAcc[0] = rotationMatrix[0][0] * ax + rotationMatrix[0][1] * ay + rotationMatrix[0][2] * az;
        worldAcc[1] = rotationMatrix[1][0] * ax + rotationMatrix[1][1] * ay + rotationMatrix[1][2] * az;
        worldAcc[2] = rotationMatrix[2][0] * ax + rotationMatrix[2][1] * ay + rotationMatrix[2][2] * az;

        return worldAcc;
    }

    // Tính khoảng cách Euclidean giữa 2 điểm trong không gian 3D
    private float calculateEuclideanDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void startTracking() {
        isTracking = true;
        totalDistance = 0f;

        // Đăng ký lắng nghe cảm biến
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);

        // Lưu giá trị gia tốc ban đầu
        initialX = 0f;
        initialY = 0f;
        initialZ = 0f;
        Log.d("EyeCheckFragment", "Initial Acceleration - X: " + initialX + ", Y: " + initialY + ", Z: " + initialZ);
        btnAct.setText("Dừng");
    }



    private void stopTracking() {
        isTracking = false;
        // Dừng lắng nghe cảm biến
        sensorManager.unregisterListener(this);

        btnAct.setText("Bắt đầu");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadProfiles() {
        new FetchProfilesTask().execute();
    }

    private void loadExams() {
        // Thêm dữ liệu bài kiểm tra (hiển thị cố định)
        examList.add(new Exam(1, "Thị lực E", R.drawable.eyeteste, true));
        examList.add(new Exam(2, "Thị lực C", R.drawable.eyetestc, true));
        examList.add(new Exam(3, "Loạn thị", R.drawable.eyetestc, true));
        examList.add(new Exam(4, "Áp lực mắt", R.drawable.eyeteste, true));

        // Hiển thị dữ liệu trên GridView
        ExamGridAdapter adapter = new ExamGridAdapter(getContext(), examList);
        binding.gridViewExamList.setAdapter(adapter);
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
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    // AsyncTask để tải hồ sơ từ API
    private class FetchProfilesTask extends AsyncTask<Void, Void, List<Profile>> {
        @Override
        protected List<Profile> doInBackground(Void... voids) {
            List<Profile> profiles = new ArrayList<>();
            try {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String accountId = sharedPreferences.getString("id", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    Log.e("ProfileFragment", "Account ID or Access Token not found");
                    return null;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/profiles?AccountID=" + accountId + "&PageIndex=1&PageSize=10&Descending=true");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONArray jsonArray = new JSONObject(response.toString()).getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject profileObject = jsonArray.getJSONObject(i);
                        profiles.add(new Profile(
                                profileObject.getInt("id"),
                                profileObject.getInt("accountID"),
                                profileObject.getString("fullName"),
                                profileObject.getString("phoneNumber"),
                                profileObject.getString("address"),
                                profileObject.optString("urlImage", ""),
                                profileObject.getString("birthday"),
                                profileObject.getBoolean("status")
                        ));
                    }
                } else {
                    Log.e("ProfileFragment", "Failed to fetch profiles. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("ProfileFragment", "Exception: " + e.getMessage(), e);
            }
            return profiles;
        }

        @Override
        protected void onPostExecute(List<Profile> profiles) {
            if (profiles != null && !profiles.isEmpty()) {
                profileList = profiles;
                ArrayAdapter<Profile> profileAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, profileList);
                profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerProfileID.setAdapter(profileAdapter);
            } else {
                showError("Không có hồ sơ nào khả dụng.");
            }
        }
    }
}
