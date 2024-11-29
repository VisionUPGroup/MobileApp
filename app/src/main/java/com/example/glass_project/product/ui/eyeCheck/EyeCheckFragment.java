package com.example.glass_project.product.ui.eyeCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
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

public class EyeCheckFragment extends Fragment {
    private FragmentEyeCheckBinding binding;
    private List<Exam> examList = new ArrayList<>();
    private List<Profile> profileList = new ArrayList<>();
    private String profileID = ""; // Giá trị profileID được chọn


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEyeCheckBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Spinner cho danh sách hồ sơ
        Spinner profileSpinner = binding.spinnerProfileID;

        // GridView cho danh sách bài kiểm tra
        GridView examGridView = binding.gridViewExamList;

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
