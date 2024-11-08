package com.example.glass_project.product.ui.VisualAcuityRecord;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.VisualAcuityRecordAdapter;
import com.example.glass_project.data.model.ExamResult;
import com.example.glass_project.data.model.VisualAcuity.VisualAcuityRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VisualAcuityRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VisualAcuityRecordAdapter adapter;
    private int profileID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_acuity_record);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy ProfileID từ intent
        profileID = getIntent().getIntExtra("profileID", -1);

        if (profileID != -1) {
            new FetchVisualAcuityRecordsTask().execute();
        } else {
            Toast.makeText(this, "Invalid Profile ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class FetchVisualAcuityRecordsTask extends AsyncTask<Void, Void, List<VisualAcuityRecord>> {
        @Override
        protected List<VisualAcuityRecord> doInBackground(Void... voids) {
            List<VisualAcuityRecord> records = new ArrayList<>();
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return null;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/visual-acuity-records?ProfileID=" + profileID);

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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject recordObject = dataArray.getJSONObject(i);
                        VisualAcuityRecord record = new VisualAcuityRecord(
                                recordObject.getInt("id"),
                                recordObject.getInt("profileID"),
                                recordObject.getString("startTime"),
                                recordObject.getBoolean("status")
                        );
                        records.add(record);
                    }
                }
            } catch (Exception e) {
                Log.e("VisualAcuityRecord", "Exception: " + e.getMessage(), e);
            }
            return records;
        }

        @Override
        protected void onPostExecute(List<VisualAcuityRecord> records) {
            if (records != null && !records.isEmpty()) {
                adapter = new VisualAcuityRecordAdapter(records, VisualAcuityRecordActivity.this);
                recyclerView.setAdapter(adapter);

                for (VisualAcuityRecord record : records) {
                    new FetchExamResultsTask(record.getId()).execute();
                }
            } else {
                Toast.makeText(VisualAcuityRecordActivity.this, "No records found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchExamResultsTask extends AsyncTask<Void, Void, List<ExamResult>> {
        private int recordId;

        public FetchExamResultsTask(int recordId) {
            this.recordId = recordId;
        }

        @Override
        protected List<ExamResult> doInBackground(Void... voids) {
            List<ExamResult> results = new ArrayList<>();
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return null;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/visual-acuity/" + recordId + "/exam-results");

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

                    JSONArray dataArray = new JSONArray(response.toString());
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject resultObject = dataArray.getJSONObject(i);
                        ExamResult result = new ExamResult(
                                resultObject.getInt("id"),
                                resultObject.getString("examDate"),
                                resultObject.getDouble("diopter"),
                                resultObject.getBoolean("status"),
                                resultObject.getString("eyeSide")
                        );
                        results.add(result);
                    }
                }
            } catch (Exception e) {
                Log.e("ExamResultActivity", "Exception: " + e.getMessage(), e);
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<ExamResult> results) {
            if (results != null) {
                adapter.setExamResults(recordId, results);
            }
        }
    }
}