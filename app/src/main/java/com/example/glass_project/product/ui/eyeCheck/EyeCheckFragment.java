package com.example.glass_project.product.ui.eyeCheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.ExamGridAdapter;
import com.example.glass_project.data.model.eyeCheck.Exam;
import com.example.glass_project.data.model.profile.Profile;
import com.example.glass_project.databinding.FragmentEyeCheckBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EyeCheckFragment extends Fragment implements SensorEventListener {
    private FragmentEyeCheckBinding binding;
    private List<Exam> examList = new ArrayList<>();
    private List<Profile> profileList = new ArrayList<>();
    private String profileID = ""; // Giá trị profileID được chọn
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private boolean isTracking = false;
    private float totalDistance = 0f;
    private float initialX = 0f, initialY = 0f, initialZ = 0f; // Tọa độ ban đầu
    private float currentX = 0f, currentY = 0f, currentZ = 0f; // Tọa độ hiện tại
    private float roll = 0f, pitch = 0f, yaw = 0f;
    private Button btnAct;
    private TextView textViewDistance;
    private float initialDistance = 0f;
    private long lastTimestamp = 0;

    // Bộ lọc trung bình động
    private final int FILTER_WINDOW_SIZE = 10; // Kích thước cửa sổ bộ lọc
    private final LinkedList<Float> accelerationXBuffer = new LinkedList<>();
    private final LinkedList<Float> accelerationYBuffer = new LinkedList<>();
    private final LinkedList<Float> accelerationZBuffer = new LinkedList<>();

    private float velocityX = 0f, velocityY = 0f, velocityZ = 0f; // Vận tốc theo từng trục

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
                stopTracking();
            } else {
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
            if (!selectedExam.isStatus()) {
                Toast.makeText(getContext(), "Bài kiểm tra này hiện không khả dụng.", Toast.LENGTH_SHORT).show();
                return;
            }
            Profile selectedProfile = (Profile) profileSpinner.getSelectedItem();
            if (selectedProfile != null) {
                profileID = String.valueOf(selectedProfile.getId());
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profileID", profileID);
                editor.putInt("selectedExamType", selectedExam.getId());
                editor.apply();
                clearExamData();
                Intent intent = new Intent(getActivity(), EyeSelectionActivity.class);
                intent.putExtra("examName", selectedExam.getType());
                startActivity(intent);
            } else {
                showError("Vui lòng chọn hồ sơ trước.");
            }
        });

        return rootView;
    }
    private static final float THRESHOLD = 0.1f;

    private float[] applyThreshold(float[] values) {
        for (int i = 0; i < values.length; i++) {
            if (Math.abs(values[i]) < THRESHOLD) {
                values[i] = 0;
            }
        }
        return values;
    }
    private static final float ALPHA = 0.8f; // Hệ số lọc trọng lực
    private float[] gravity = new float[3];

    private float[] removeGravity(float[] values) {
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * values[0];
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * values[1];
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * values[2];

        float[] linearAcceleration = new float[3];
        linearAcceleration[0] = values[0] - gravity[0];
        linearAcceleration[1] = values[1] - gravity[1];
        linearAcceleration[2] = values[2] - gravity[2];

        return linearAcceleration;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTimestamp = System.currentTimeMillis();
            if (lastTimestamp == 0) {
                lastTimestamp = currentTimestamp;
                return;
            }

            float deltaTime = (currentTimestamp - lastTimestamp) / 1000.0f;
            lastTimestamp = currentTimestamp;

            float[] rawAcceleration = event.values;
            float[] linearAcceleration = removeGravity(rawAcceleration);
            linearAcceleration = applyThreshold(linearAcceleration);

            addToBuffer(accelerationXBuffer, linearAcceleration[0]);
            addToBuffer(accelerationYBuffer, linearAcceleration[1]);
            addToBuffer(accelerationZBuffer, linearAcceleration[2]);

            float filteredAx = getAverage(accelerationXBuffer);
            float filteredAy = getAverage(accelerationYBuffer);
            float filteredAz = getAverage(accelerationZBuffer);

            if (isTracking) {
                float[] worldAcc = applyRotationToAcceleration(filteredAx, filteredAy, filteredAz, roll, pitch, yaw);

                velocityX += worldAcc[0] * deltaTime;
                velocityY += worldAcc[1] * deltaTime;
                velocityZ += worldAcc[2] * deltaTime;

                applyVelocityDecay(); // Giảm trôi dạt

                currentX += velocityX * deltaTime;
                currentY += velocityY * deltaTime;
                currentZ += velocityZ * deltaTime;

                totalDistance = calculateEuclideanDistance(initialX, initialY, initialZ, currentX, currentY, currentZ);
                textViewDistance.setText(String.format("Khoảng cách: %.5f m", totalDistance));
            }
        }
    }
    private void applyVelocityDecay() {
        float decayFactor = 0.99f; // Hệ số giảm
        velocityX *= decayFactor;
        velocityY *= decayFactor;
        velocityZ *= decayFactor;
    }


    // Thêm giá trị mới vào buffer, xóa giá trị cũ nếu vượt quá kích thước
    private void addToBuffer(LinkedList<Float> buffer, float value) {
        if (buffer.size() >= FILTER_WINDOW_SIZE) {
            buffer.poll(); // Xóa giá trị cũ nhất
        }
        buffer.add(value);
    }

    // Tính trung bình của các giá trị trong buffer
    private float getAverage(LinkedList<Float> buffer) {
        float sum = 0f;
        for (float value : buffer) {
            sum += value;
        }
        return buffer.size() > 0 ? sum / buffer.size() : 0f;
    }

    private float[] applyRotationToAcceleration(float ax, float ay, float az, float roll, float pitch, float yaw) {
        // Tạo ma trận quay từ Roll, Pitch, Yaw
        float[][] rotationMatrix = new float[3][3];
        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);

        rotationMatrix[0][0] = cosPitch * cosYaw;
        rotationMatrix[0][1] = cosPitch * sinYaw;
        rotationMatrix[0][2] = -sinPitch;

        rotationMatrix[1][0] = sinRoll * sinPitch * cosYaw - cosRoll * sinYaw;
        rotationMatrix[1][1] = sinRoll * sinPitch * sinYaw + cosRoll * cosYaw;
        rotationMatrix[1][2] = sinRoll * cosPitch;

        rotationMatrix[2][0] = cosRoll * sinPitch * cosYaw + sinRoll * sinYaw;
        rotationMatrix[2][1] = cosRoll * sinPitch * sinYaw - sinRoll * cosYaw;
        rotationMatrix[2][2] = cosRoll * cosPitch;

        float[] worldAcc = new float[3];
        worldAcc[0] = rotationMatrix[0][0] * ax + rotationMatrix[0][1] * ay + rotationMatrix[0][2] * az;
        worldAcc[1] = rotationMatrix[1][0] * ax + rotationMatrix[1][1] * ay + rotationMatrix[1][2] * az;
        worldAcc[2] = rotationMatrix[2][0] * ax + rotationMatrix[2][1] * ay + rotationMatrix[2][2] * az;

        return worldAcc;
    }

    private float calculateEuclideanDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void startTracking() {
        isTracking = true;
        totalDistance = 0f;
        velocityX = 0f;
        velocityY = 0f;
        velocityZ = 0f;
        lastTimestamp = 0;

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);

        btnAct.setText("Dừng");
    }

    private void stopTracking() {
        isTracking = false;
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
        examList.add(new Exam(3, "Loạn thị", R.drawable.eyetestc, false));
        examList.add(new Exam(4, "Mù màu", R.drawable.eyeteste, false));

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
        editor.remove("examItemsJson");
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

                String BaseUrl = Config.getBaseUrl();
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
                        Profile profile = new Profile(
                                profileObject.getInt("id"),
                                profileObject.getInt("accountID"),
                                profileObject.getString("fullName"),
                                profileObject.getString("phoneNumber"),
                                profileObject.getString("address"),
                                profileObject.optString("urlImage", ""),
                                profileObject.getString("birthday"),
                                profileObject.getBoolean("status")
                        );
                        // Only add profiles with status true
                        if (profile.isStatus()) {
                            profiles.add(profile);
                        }
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
                new AlertDialog.Builder(getContext())
                        .setTitle("Bạn chưa có hồ sơ nào")
                        .setMessage("Vui lòng tạo hồ sơ trước khi thực hiện kiểm tra.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
                                bottomNavigationView.setSelectedItemId(R.id.navigation_profile); // Optional: adds the transaction to the back stack
                            }
                        })
                        .show();
            }
        }
    }
}
