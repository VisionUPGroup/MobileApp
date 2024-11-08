package com.example.glass_project.product.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.ProfileAdapter;
import com.example.glass_project.data.model.profile.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final int UPDATE_PROFILE_REQUEST_CODE = 2;
    private ListView listView;
    private ProfileAdapter adapter;
    private List<Profile> profileList = new ArrayList<>();
    private int pageIndex = 1;
    private boolean isLoading = false;
    private boolean hasMoreData = true; // Dùng để kiểm tra xem còn dữ liệu để tải tiếp hay không
    private static final int CREATE_PROFILE_REQUEST_CODE = 1;

    private Button createProfileButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo ListView và Adapter
        listView = view.findViewById(R.id.listView);
        adapter = new ProfileAdapter(getContext(), profileList);
        listView.setAdapter(adapter);

        // Nút để tạo hồ sơ mới
        createProfileButton = view.findViewById(R.id.createProfileButton);
        createProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateProfileActivity.class);
            startActivityForResult(intent, CREATE_PROFILE_REQUEST_CODE);
        });

        // Tải hồ sơ ban đầu
        loadProfiles();

        // Thêm listener để lắng nghe sự kiện cuộn của ListView
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Không cần xử lý
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && hasMoreData && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    pageIndex++;
                    loadProfiles();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CREATE_PROFILE_REQUEST_CODE || requestCode == UPDATE_PROFILE_REQUEST_CODE) {
                // Nếu hồ sơ được tạo hoặc cập nhật thành công, tải lại danh sách từ đầu
                pageIndex = 1;
                profileList.clear();
                hasMoreData = true;
                loadProfiles();
            }
        }
    }

    // Phương thức tải hồ sơ từ API
    private void loadProfiles() {
        if (isLoading) return;
        isLoading = true;

        new FetchProfilesTask().execute();
    }

    // AsyncTask để tải dữ liệu hồ sơ từ API
    private class FetchProfilesTask extends AsyncTask<Void, Void, List<Profile>> {
        @Override
        protected List<Profile> doInBackground(Void... voids) {
            List<Profile> profiles = new ArrayList<>();
            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String accountId = sharedPreferences.getString("id", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    Log.e("ProfileFragment", "Account ID or Access Token not found");
                    return null;
                }

                String BaseUrl = baseUrl.BASE_URL;
                URL url = new URL(BaseUrl + "/api/profiles?AccountID=" + accountId + "&PageIndex=" + pageIndex + "&PageSize=10&Descending=true");

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

                    profiles = parseProfilesData(response.toString());
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
            isLoading = false;
            if (profiles != null && !profiles.isEmpty()) {
                profileList.addAll(profiles);
                adapter.notifyDataSetChanged();
            } else {
                // Đánh dấu rằng không còn dữ liệu để tải thêm
                hasMoreData = false;
                Toast.makeText(getContext(), "No more profiles to load", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức phân tích dữ liệu JSON từ API thành danh sách Profile
    private List<Profile> parseProfilesData(String jsonResponse) throws JSONException {
        List<Profile> profileList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject profileObject = dataArray.getJSONObject(i);

            int id = profileObject.getInt("id");
            int accountID = profileObject.getInt("accountID");
            String fullName = profileObject.getString("fullName");
            String phoneNumber = profileObject.getString("phoneNumber");
            String address = profileObject.getString("address");
            String urlImage = profileObject.optString("urlImage", "default_image_url");
            String birthday = profileObject.getString("birthday");
            boolean status = profileObject.getBoolean("status");

            Profile profile = new Profile(id, accountID, fullName, phoneNumber, address, urlImage, birthday, status);
            profileList.add(profile);
        }

        return profileList;
    }
}
