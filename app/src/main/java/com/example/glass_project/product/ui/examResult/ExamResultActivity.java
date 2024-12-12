package com.example.glass_project.product.ui.examResult;

import android.content.Intent;
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
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.ExamResultAdapter;
import com.example.glass_project.data.model.VisualAcuity.ExamResult;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExamResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExamResultAdapter adapter;
    private int recordId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        recordId = intent.getIntExtra("recordId", -1);

        if (recordId != -1) {
            new FetchExamResultsTask().execute();
        } else {
            Toast.makeText(this, "Invalid Record ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class FetchExamResultsTask extends AsyncTask<Void, Void, List<ExamResult>> {
        @Override
        protected List<ExamResult> doInBackground(Void... voids) {
            List<ExamResult> results = new ArrayList<>();
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                if (accessToken.isEmpty()) {
                    return null;
                }

                String BaseUrl = Config.getBaseUrl();
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
            if (results != null && !results.isEmpty()) {
                adapter = new ExamResultAdapter(results);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(result -> {
                    // Handle item click, if needed.
                });
            } else {
                Toast.makeText(ExamResultActivity.this, "No records found or failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
