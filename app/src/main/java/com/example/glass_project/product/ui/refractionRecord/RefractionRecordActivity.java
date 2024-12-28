package com.example.glass_project.product.ui.refractionRecord;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.RefractionRecordAdapter;
import com.example.glass_project.data.model.Refraction.Kiosk;
import com.example.glass_project.data.model.Refraction.MeasurementResult;
import com.example.glass_project.data.model.Refraction.RefractionRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RefractionRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RefractionRecordAdapter adapter;
    private int profileID;
    private View noDataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refraction_record);

        initializeUI();

        profileID = getIntent().getIntExtra("profileID", -1);
        if (profileID != -1) {
            new FetchRefractionRecordsTask().execute();
        } else {
            Toast.makeText(this, "Invalid Profile ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noDataView = findViewById(R.id.noDataView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private class FetchRefractionRecordsTask extends AsyncTask<Void, Void, List<RefractionRecord>> {
        @Override
        protected List<RefractionRecord> doInBackground(Void... voids) {
            List<RefractionRecord> records = new ArrayList<>();
            try {
                String accessToken = getAccessToken();
                if (accessToken.isEmpty()) return null;

                String urlStr = Config.getBaseUrl() + "/api/refraction-records?ProfileID=" + profileID + "&PageIndex=1&PageSize=10&Descending=true";
                String response = makeGetRequest(urlStr, accessToken);
                if (response != null) {
                    records = parseRefractionRecords(response);
                }
            } catch (Exception e) {
                Log.e("FetchRecords", "Error fetching records", e);
            }
            return records;
        }

        @Override
        protected void onPostExecute(List<RefractionRecord> records) {
            if (records != null && !records.isEmpty()) {
                adapter = new RefractionRecordAdapter(records, RefractionRecordActivity.this);
                recyclerView.setAdapter(adapter);

                for (RefractionRecord record : records) {
                    new FetchMeasurementResultsTask(record.getId()).execute();
                }
            } else {
                recyclerView.setVisibility(View.GONE);
                noDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FetchMeasurementResultsTask extends AsyncTask<Void, Void, List<MeasurementResult>> {
        private final int recordId;

        public FetchMeasurementResultsTask(int recordId) {
            this.recordId = recordId;
        }

        @Override
        protected List<MeasurementResult> doInBackground(Void... voids) {
            List<MeasurementResult> results = new ArrayList<>();
            try {
                String accessToken = getAccessToken();
                if (accessToken.isEmpty()) return null;

                String urlStr = Config.getBaseUrl() + "/api/measurement-results?RecordID=" + recordId + "&PageIndex=1&PageSize=10&Descending=true";
                String response = makeGetRequest(urlStr, accessToken);
                if (response != null) {
                    results = parseMeasurementResults(response);
                }
            } catch (Exception e) {
                Log.e("FetchResults", "Error fetching measurement results", e);
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<MeasurementResult> results) {
            if (results != null && !results.isEmpty()) {
                adapter.setMeasurementResults(recordId, results);
            }
        }
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    private String makeGetRequest(String urlString, String accessToken) throws Exception {
        URL url = new URL(urlString);
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
            return response.toString();
        } else {
            Log.e("HTTP Request", "Error response code: " + responseCode);
            return null;
        }
    }

    private List<RefractionRecord> parseRefractionRecords(String response) throws Exception {
        List<RefractionRecord> records = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject recordObject = dataArray.getJSONObject(i);

            List<Kiosk> kiosks = new ArrayList<>();
            JSONArray kioskArray = recordObject.getJSONArray("kiosks");
            for (int j = 0; j < kioskArray.length(); j++) {
                JSONObject kioskObject = kioskArray.getJSONObject(j);
                kiosks.add(new Kiosk(
                        kioskObject.getInt("id"),
                        kioskObject.getString("name"),
                        kioskObject.getString("address"),
                        kioskObject.getString("phoneNumber"),
                        kioskObject.getString("email")
                ));
            }

            RefractionRecord record = new RefractionRecord(
                    recordObject.getInt("id"),
                    recordObject.getInt("profileID"),
                    formatDate(recordObject.getString("startTime")),
                    recordObject.getBoolean("status"),
                    kiosks,
                    recordObject.getBoolean("isJoin")
            );
            records.add(record);
        }
        return records;
    }

    private List<MeasurementResult> parseMeasurementResults(String response) throws Exception {
        List<MeasurementResult> results = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject resultObject = dataArray.getJSONObject(i);
            MeasurementResult result = new MeasurementResult(
                    resultObject.getInt("id"),
                    resultObject.getInt("recordID"),
                    resultObject.getInt("employeeID"),
                    resultObject.getInt("testType"),
                    resultObject.getDouble("spherical"),
                    resultObject.getDouble("cylindrical"),
                    resultObject.getDouble("axis"),
                    resultObject.getDouble("pupilDistance"),
                    resultObject.getInt("eyeSide"),
                    resultObject.getString("prescriptionDetails"),
                    resultObject.getString("lastCheckupDate"),
                    resultObject.getString("nextCheckupDate"),
                    resultObject.getString("notes")
            );
            results.add(result);
        }
        return results;
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
