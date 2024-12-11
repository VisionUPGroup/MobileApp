package com.example.glass_project.product.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.glass_project.R;
import com.example.glass_project.config.baseUrl;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment implements UpdateProfileDialogFragment.OnProfileUpdatedListener {

    public static final int UPDATE_PROFILE_REQUEST_CODE = 2;
    private ListView listView;
    private ProfileAdapter adapter;
    private List<Profile> profileList = new ArrayList<>();
    private int pageIndex = 1;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private static final int CREATE_PROFILE_REQUEST_CODE = 1;

    private Button createProfileButton, searchButton;
    private EditText searchEditText;
    private View noDataView;
    private RelativeLayout progressBar;

    // Biến lưu giá trị tìm kiếm hiện tại
    private String currentSearchQuery = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các thành phần giao diện
        listView = view.findViewById(R.id.listView);
        createProfileButton = view.findViewById(R.id.createProfileButton);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        noDataView = view.findViewById(R.id.noDataView);
        progressBar = view.findViewById(R.id.progressBar); // Khởi tạo ProgressBar

        // Thiết lập Adapter
        adapter = new ProfileAdapter(getContext(), this, profileList);
        listView.setAdapter(adapter);

        // Nút tạo hồ sơ mới
        createProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateProfileActivity.class);
            startActivityForResult(intent, CREATE_PROFILE_REQUEST_CODE);
        });

        // Nút tìm kiếm
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                pageIndex = 1; // Reset pageIndex khi tìm kiếm mới
                currentSearchQuery = query; // Lưu lại giá trị tìm kiếm hiện tại
                profileList.clear(); // Xóa dữ liệu cũ
                adapter.notifyDataSetChanged(); // Cập nhật adapter
                loadProfiles(currentSearchQuery); // Tải dữ liệu mới
            } else {
                Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });

        // Tải dữ liệu ban đầu
        loadProfiles(null);

        // Infinite scrolling
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && hasMoreData && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    pageIndex++;
                    loadProfiles(currentSearchQuery); // Tải thêm dựa trên giá trị tìm kiếm hiện tại
                }
            }
        });

        return view;
    }

    // Hiển thị hoặc ẩn ProgressBar
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        listView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PROFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Reset danh sách và tải lại dữ liệu sau khi tạo hồ sơ thành công
            pageIndex = 1;
            profileList.clear();
            adapter.notifyDataSetChanged();
            loadProfiles(currentSearchQuery); // Tải lại danh sách hồ sơ
        }
    }

    // Hàm tải dữ liệu hồ sơ
    private void loadProfiles(String fullname) {
        if (isLoading) return;
        isLoading = true;
        showLoading(true);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String accountId = sharedPreferences.getString("id", "");

                if (accountId.isEmpty() || accessToken.isEmpty()) {
                    Log.e("ProfileFragment", "Không tìm thấy ID tài khoản hoặc Access Token");
                    return;
                }

                String BaseUrl = baseUrl.BASE_URL;
                StringBuilder urlBuilder = new StringBuilder(BaseUrl + "/api/profiles?");
                urlBuilder.append("AccountID=").append(accountId);
                urlBuilder.append("&PageIndex=").append(pageIndex);
                urlBuilder.append("&PageSize=10&Descending=true");

                // Thêm tham số tìm kiếm nếu có
                if (fullname != null && !fullname.isEmpty()) {
                    urlBuilder.append("&Fullname=").append(fullname);
                }

                URL url = new URL(urlBuilder.toString());

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

                    List<Profile> newProfiles = parseProfilesData(response.toString());
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (!newProfiles.isEmpty()) {
                            if (pageIndex == 1) {
                                profileList.clear(); // Xóa danh sách cũ khi tải trang đầu tiên
                            }

                            // Filter profiles with status false
                            List<Profile> filteredProfiles = new ArrayList<>();
                            for (Profile profile : newProfiles) {
                                if (profile.isStatus()) { // Assuming getStatus() returns boolean
                                    filteredProfiles.add(profile);
                                }
                            }

                            profileList.addAll(filteredProfiles);
                            adapter.notifyDataSetChanged();

                            // Hiển thị ListView và ẩn noDataView
                            listView.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                        } else {
                            if (pageIndex == 1) {
                                profileList.clear();
                                adapter.notifyDataSetChanged();

                                // Ẩn ListView và hiển thị noDataView
                                listView.setVisibility(View.GONE);
                                noDataView.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
                            } else {
                                hasMoreData = false;
                            }
                        }
                        isLoading = false;
                    });
                } else {
                    Log.e("ProfileFragment", "Lấy danh sách hồ sơ thất bại. Mã phản hồi: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Lỗi: " + e.getMessage(), e);
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lỗi khi tải hồ sơ", Toast.LENGTH_SHORT).show());
                isLoading = false;
            }
        });
    }


    // Hàm parse dữ liệu JSON
    private List<Profile> parseProfilesData(String jsonResponse) throws JSONException {
        List<Profile> profileList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject profileObject = dataArray.getJSONObject(i);

            boolean status = profileObject.getBoolean("status");
            if (!status) {
                continue;
            }

            int id = profileObject.getInt("id");
            int accountID = profileObject.getInt("accountID");
            String fullName = profileObject.getString("fullName");
            String phoneNumber = profileObject.getString("phoneNumber");
            String address = profileObject.getString("address");
            String urlImage = profileObject.optString("urlImage", "default_image_url");
            String birthday = profileObject.getString("birthday");

            Profile profile = new Profile(id, accountID, fullName, phoneNumber, address, urlImage, birthday, status);
            profileList.add(profile);
        }

        return profileList;
    }

    @Override
    public void onProfileUpdated() {
        pageIndex = 1;
        profileList.clear();
        hasMoreData = true;
        loadProfiles(currentSearchQuery);
    }
}
