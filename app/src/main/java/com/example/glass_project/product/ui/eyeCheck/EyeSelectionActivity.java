package com.example.glass_project.product.ui.eyeCheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.glass_project.MainActivity;
import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.model.eyeCheck.ExamItem;
import com.example.glass_project.databinding.ActivityEyeSelectionBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EyeSelectionActivity extends AppCompatActivity {

    private ActivityEyeSelectionBinding binding;
    private int selectedExamTypeId;
    private List<ExamItem> examItems = new ArrayList<>();
    private String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEyeSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", "");
        selectedExamTypeId = sharedPreferences.getInt("selectedExamType", -1);

        examItems = loadExamItemsFromPreferences();

        if (!examItems.isEmpty()) {
            binding.buttonExportResults.setEnabled(true);
        } else {
            if (selectedExamTypeId != -1 && !accessToken.isEmpty()) {
                new FetchExamItemsTask().execute(selectedExamTypeId, accessToken);
            } else {
                Toast.makeText(this, "No exam type selected or access token missing", Toast.LENGTH_SHORT).show();
            }
        }

        displayEyeTestResults("left", binding.textLeftEyeResults);
        displayEyeTestResults("right", binding.textRightEyeResults);

        binding.buttonLeftEye.setOnClickListener(v -> saveEyeSide("left"));
        binding.buttonRightEye.setOnClickListener(v -> saveEyeSide("right"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button buttonGuide = findViewById(R.id.buttonGuide);
        buttonGuide.setOnClickListener(v -> {
            Intent intent = new Intent(EyeSelectionActivity.this, GuideActivity.class);
            startActivity(intent);
        });
        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String examDataLeft = sharedPreferences.getString("ExamData_left", null);
        String examDataRight = sharedPreferences.getString("ExamData_right", null);
        // Gọi khi buttonExportResults được click
        if (examDataLeft != null || examDataRight != null) {
            binding.buttonExportResults.setEnabled(true);
            binding.buttonExportResults.setOnClickListener(v -> handleExportResults(examDataLeft, examDataRight));
        } else {
            binding.buttonExportResults.setEnabled(false);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Gọi phương thức để hiển thị kết quả kiểm tra mắt cho cả hai bên
        displayEyeTestResults("left", binding.textLeftEyeResults);
        displayEyeTestResults("right", binding.textRightEyeResults);

        // Kiểm tra dữ liệu và cập nhật trạng thái của buttonExportResults
        SharedPreferences sharedPreferences = getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
        String examDataLeft = sharedPreferences.getString("ExamData_left", null);
        String examDataRight = sharedPreferences.getString("ExamData_right", null);

        if (examDataLeft != null || examDataRight != null) {
            binding.buttonExportResults.setEnabled(true);
            binding.buttonExportResults.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.accent)
            );
            binding.buttonExportResults.setOnClickListener(v -> handleExportResults(examDataLeft, examDataRight));
        } else {
            binding.buttonExportResults.setEnabled(false);
            binding.buttonExportResults.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.gray)
            );

        }
    }

    private void handleExportResults(String examDataLeft, String examDataRight) {
        if (examDataLeft != null && examDataRight != null) {
            // Gửi cả hai bên mắt
            int profileID = getProfileIDFromPreferences();
            new FetchVisualAcuityRecordIDTask(profileID).execute();
        } else {
            // Chỉ có dữ liệu từ một bên mắt, hiển thị modal
            String missingSide = (examDataLeft == null) ? "trái" : "phải";
            showIncompleteDataDialog(missingSide);
        }
    }
    private void showIncompleteDataDialog(String missingSide) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dữ liệu chưa đầy đủ");
        builder.setMessage("Bạn chỉ có dữ liệu từ một bên mắt. Bạn nên thực hiện kiểm tra bên mắt " + missingSide + " để tăng độ chính xác.");

        builder.setPositiveButton("Tiếp tục", (dialog, which) -> {
            int profileID = getProfileIDFromPreferences();
            new FetchVisualAcuityRecordIDTask(profileID).execute(); // Tiếp tục gửi dữ liệu hiện có
        });

        builder.setNegativeButton("Quay lại", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private int getProfileIDFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String profileIDStr = sharedPreferences.getString("profileID", "-1");
        try {
            return Integer.parseInt(profileIDStr);
        } catch (NumberFormatException e) {
            Log.e("EyeSelectionActivity", "profileID không hợp lệ", e);
            return -1;
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
    private void displayEyeTestResults(String eyeSide, TextView resultTextView) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int numberOfTest = sharedPreferences.getInt("numberOfTest_" + eyeSide, -1);
        float myopia = sharedPreferences.getFloat("myopia_" + eyeSide, -1.0f);

        if (numberOfTest != -1 && myopia != -1.0f) {
            resultTextView.setText("Số lần kiểm tra: " + numberOfTest + "\nĐộ cận thị: " + String.format("%.4f", myopia));
        } else {
            resultTextView.setText("Chưa có dữ liệu");
        }
    }

    private void saveJsonResponseToPreferences(String jsonResponse) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("examItemsJson", jsonResponse);
        editor.apply();
    }

    private List<ExamItem> loadExamItemsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String jsonResponse = sharedPreferences.getString("examItemsJson", "");

        if (!jsonResponse.isEmpty()) {
            return parseJsonResponse(jsonResponse);
        } else {
            Log.d("Load ExamItems", "No cached exam items found.");
            return new ArrayList<>();
        }
    }

    private List<ExamItem> parseJsonResponse(String jsonResponse) {
        List<ExamItem> examItemsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                Log.d("JSON Response", "Data Array: " + dataArray.toString());

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    int id = item.optInt("id", -1);
                    int rotation = item.optInt("rotation", 0);
                    String type = item.optString("type", "");
                    int level = item.optInt("level", 1);
                    int standardDistance = item.optInt("standardDistance", 3);
                    String imageUrl = item.optString("url", "");
                    int content = item.optInt("content", 0);
                    double myopia = item.optDouble("myopia", 0.0);
                    String expectedAnswer = item.optString("expectedAnswer", "");
                    int examID = item.optInt("examID", -1);

                    ExamItem examItem = new ExamItem(id, rotation, type, level, standardDistance, imageUrl, content, myopia, expectedAnswer, examID);
                    examItemsList.add(examItem);
                }
            } else {
                Log.e("JSON Error", "No 'data' array found in JSON response");
            }
        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Error parsing JSON response", e);
        }
        return examItemsList;
    }

    private class FetchExamItemsTask extends AsyncTask<Object, Void, List<ExamItem>> {
        @Override
        protected List<ExamItem> doInBackground(Object... params) {
            int examId = (int) params[0];
            String accessToken = (String) params[1];
            List<ExamItem> examItemsList = new ArrayList<>();

            try {
                URL url = new URL(baseUrl.BASE_URL + "/api/exam-items/exam/" + examId + "/exam-items?ExamID=" + examId + "&PageIndex=1&PageSize=35");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    saveJsonResponseToPreferences(response.toString());
                    examItemsList = parseJsonResponse(response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return examItemsList;
        }

        @Override
        protected void onPostExecute(List<ExamItem> examItemsList) {
            if (!examItemsList.isEmpty()) {
                examItems = examItemsList;
                binding.buttonExportResults.setEnabled(true);
            }
        }
    }

    private void saveEyeSide(String eyeSide) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eyeSide", eyeSide);
        editor.apply();
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    private class FetchVisualAcuityRecordIDTask extends AsyncTask<Void, Void, Integer> {
        private final int profileID;

        public FetchVisualAcuityRecordIDTask(int profileID) {
            this.profileID = profileID;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                URL url = new URL(baseUrl.BASE_URL + "/api/visual-acuity-records");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("profileID", profileID);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getInt("id");
                }
            } catch (Exception e) {
                Log.e("FetchVisualAcuityRecordIDTask", "Exception occurred while sending exam result data: " + e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer visualAcuityRecordID) {
            if (visualAcuityRecordID != null) {
                sendCreateFromClientRequest(visualAcuityRecordID);
            } else {
                Toast.makeText(EyeSelectionActivity.this, "Failed to retrieve visualAcuityRecordID", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendCreateFromClientRequest(int visualAcuityRecordID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EyeExamData", Context.MODE_PRIVATE);
        String eyeSideLeftData = sharedPreferences.getString("ExamData_left", null);
        String eyeSideRightData = sharedPreferences.getString("ExamData_right", null);

        if (eyeSideLeftData != null) {
            postExamResultAsync(eyeSideLeftData, "left", visualAcuityRecordID);
        }
        if (eyeSideRightData != null) {
            postExamResultAsync(eyeSideRightData, "right", visualAcuityRecordID);
        }

        if (eyeSideLeftData != null || eyeSideRightData != null) {
            // Hiển thị AlertDialog để xác nhận
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn quay lại màn hình chính không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Chuyển về MainActivity nếu chọn "Có"
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Đóng Activity hiện tại
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        // Đóng dialog nếu chọn "Không"
                        dialog.dismiss();
                    })
                    .show();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void postExamResultAsync(final String examDataJson, final String eyeSide, final int visualAcuityRecordID) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    URL url = new URL(baseUrl.BASE_URL + "/api/exam-results/create-from-client");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    connection.setRequestProperty("Content-Type", "application/json");

                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    float diopter = sharedPreferences.getFloat("myopia_" + eyeSide, 0.0f);
                    String formattedDiopter = String.format("%.2f", diopter);
                    diopter = Float.parseFloat(formattedDiopter);
                    int examID = sharedPreferences.getInt("selectedExamType", -1);

                    JSONObject jsonRequest = new JSONObject(examDataJson);
                    filterDuplicateExamItems(jsonRequest);  // Assuming this method filters duplicates
                    jsonRequest.put("visualAcuityRecordID", visualAcuityRecordID);
                    jsonRequest.put("diopter", diopter);
                    jsonRequest.put("examID", examID);
                    jsonRequest.put("eyeSide", eyeSide);

                    Log.d("postExamResultAsync", "Sending request with data: " + jsonRequest.toString());

                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(jsonRequest.toString());
                    writer.flush();

                    int responseCode = connection.getResponseCode();
                    writer.close();

                    return responseCode;
                } catch (Exception e) {
                    Log.e("postExamResultAsync", "Error sending data", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer responseCode) {
                if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(EyeSelectionActivity.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                    clearExamData();
                    Log.d("postExamResultAsync", "Data sent successfully with response code: " + responseCode);
                } else {
                    Toast.makeText(EyeSelectionActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
                    Log.e("postExamResultAsync", "Failed to send data. Response code: " + responseCode);
                }
            }
        }.execute();
    }
    public void filterDuplicateExamItems(JSONObject jsonRequest) {
        try {
            // Lấy danh sách examResultItems từ jsonRequest
            JSONArray examResultItems = jsonRequest.getJSONArray("examResultItems");

            // HashSet để lưu các examItemID đã gặp
            HashSet<Integer> uniqueExamItemIds = new HashSet<>();
            JSONArray filteredExamResultItems = new JSONArray();

            // Lọc các examItemID trùng lặp, chỉ giữ lại mục đầu tiên
            for (int i = 0; i < examResultItems.length(); i++) {
                JSONObject item = examResultItems.getJSONObject(i);
                int examItemID = item.getInt("examItemID");

                // Nếu examItemID chưa tồn tại trong HashSet, thêm vào kết quả và HashSet
                if (!uniqueExamItemIds.contains(examItemID)) {
                    uniqueExamItemIds.add(examItemID);
                    filteredExamResultItems.put(item);
                }
            }

            // Thay thế mảng examResultItems bằng mảng đã lọc
            jsonRequest.put("examResultItems", filteredExamResultItems);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void clearExamData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.remove("ExamData_left").commit();
        //editor.remove("ExamData_right").commit();
        //editor.remove("numberOfTest_left");
        //editor.remove("myopia_left");
        //editor.remove("numberOfTest_right");
        //.remove("myopia_right");
        editor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearExamData();
        binding = null;
    }
}
