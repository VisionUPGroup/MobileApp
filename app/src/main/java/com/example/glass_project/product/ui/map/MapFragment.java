package com.example.glass_project.product.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.Config;
import com.example.glass_project.data.adapter.KioskAdapter;
import com.example.glass_project.data.model.kiosk.Kiosk;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private KioskAdapter adapter;
    private List<Kiosk> kioskList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Load the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Fetch data from API
        fetchNearbyKiosks();

        // Nút để gọi API lấy ki-ốt gần đây
        Button btnNearbyKiosks = view.findViewById(R.id.btn_nearby_kiosks);
        btnNearbyKiosks.setOnClickListener(v -> fetchNearbyKiosks());

        // Nút để gọi API lấy tất cả ki-ốt
        Button btnAllKiosks = view.findViewById(R.id.btn_all_kiosks);
        btnAllKiosks.setOnClickListener(v -> fetchAllKiosks());

        return view;
    }
    private void fetchAllKiosks() {
        executorService.execute(() -> {
            try {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = Config.getBaseUrl();

                URL url = new URL(BaseUrl + "/api/kiosks");
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

                    List<Kiosk> kiosks = parseKiosksData(response.toString());
                    requireActivity().runOnUiThread(() -> updateUIWithKiosks(kiosks));
                } else {
                    Log.e("API_ERROR", "Failed to fetch all kiosks. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("API_ERROR", "Exception: " + e.getMessage(), e);
            }
        });
    }
    private void updateUIWithKiosks(List<Kiosk> kiosks) {
        if (kiosks != null) {
            kioskList = kiosks;
            adapter = new KioskAdapter(kioskList, this::onKioskSelected, requireContext());
            recyclerView.setAdapter(adapter);

            mMap.clear(); // Xóa tất cả marker cũ
            for (Kiosk kiosk : kioskList) {
                LatLng location = getLocationFromAddress(kiosk.getAddress());
                if (location != null) {
                    addMarker(location, kiosk.getName());
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            addCurrentLocationMarker();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void addCurrentLocationMarker() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền nếu chưa được cấp
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Nếu quyền đã được cấp, lấy vị trí hiện tại
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                // Lấy tọa độ vị trí hiện tại
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng currentLocation = new LatLng(latitude, longitude);

                // Thêm marker cho vị trí hiện tại
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Your Current Location")
                        .snippet("This is your current position"));

                // Di chuyển camera đến vị trí hiện tại
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));
            } else {
                Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu người dùng cấp quyền, thêm marker vị trí hiện tại
                addCurrentLocationMarker();
            } else {
                // Nếu người dùng từ chối quyền, thông báo
                Toast.makeText(requireContext(), "Quyền vị trí bị từ chối. Không thể hiển thị vị trí hiện tại.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchNearbyKiosks() {
        if (!isAdded()) {
            Log.e("Fragment Error", "Fragment not attached to Activity");
            return;
        }

        // Check location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Attempt to get the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                fetchNearbyKiosksFromApi(latitude, longitude);
            } else {
                // Show a prompt to enable location
                showLocationEnableDialog();
            }
        });
    }

    // Show a dialog to prompt the user to enable location services
    private void showLocationEnableDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Bật định vị")
                .setMessage("Ứng dụng cần quyền truy cập vị trí để hiển thị các kiosk gần bạn. Vui lòng bật định vị.")
                .setPositiveButton("Cài đặt", (dialog, which) -> {
                    // Redirect user to location settings
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // Prevent dismissal by tapping outside
                .show();
    }


    private void checkAndRequestLocationPermission() {
        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Nếu quyền đã được cấp, kiểm tra xem GPS có bật không
            checkLocationSettings();
        }
    }

    private void checkLocationSettings() {
        // Tạo đối tượng LocationRequest để kiểm tra yêu cầu bật GPS
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), locationSettingsResponse -> {
            // GPS đã bật, có thể lấy vị trí
            fetchNearbyKiosks();
        });

        task.addOnFailureListener(getActivity(), e -> {
            if (e instanceof ResolvableApiException) {
                // Nếu chưa bật GPS, yêu cầu người dùng bật GPS
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(), 101); // Mã yêu cầu yêu cầu bật GPS
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.e("GPS", "Error requesting location settings resolution.", sendEx);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            // Kiểm tra xem người dùng có bật GPS không
            if (resultCode == getActivity().RESULT_OK) {
                // GPS đã được bật, tiếp tục gọi API lấy ki-ốt gần
                fetchNearbyKiosks();
            } else {
                Toast.makeText(getContext(), "Vui lòng bật GPS để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchNearbyKiosksFromApi(double latitude, double longitude) {
        executorService.execute(() -> {
            try {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = Config.getBaseUrl();

                URL url = new URL(BaseUrl + "/api/kiosks/get-nearby-kiosks?latitude=" + latitude + "&longitude=" + longitude + "&maxDistanceKm=5");
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

                    List<Kiosk> kiosks = parseKiosksData(response.toString());
                    requireActivity().runOnUiThread(() -> updateUIWithKiosks(kiosks));
                } else {
                    Log.e("API_ERROR", "Failed to fetch nearby kiosks. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("API_ERROR", "Exception: " + e.getMessage(), e);
            }
        });
    }

//    private void fetchAllKiosks() {
//        // Asynchronously fetch all kiosks from the API
//        new FetchAllKiosksTask().execute();
//    }

    private class FetchAllKiosksTask extends AsyncTask<Void, Void, List<Kiosk>> {

        @Override
        protected List<Kiosk> doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = Config.getBaseUrl();

                URL url = new URL(BaseUrl + "/api/kiosks");
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

                    return parseKiosksData(response.toString());
                } else {
                    Log.e("API_ERROR", "Failed to fetch all kiosks. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("API_ERROR", "Exception: " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Kiosk> kiosks) {
            if (kiosks != null) {
                kioskList = kiosks;
                adapter = new KioskAdapter(kioskList, MapFragment.this::onKioskSelected, getContext());
                recyclerView.setAdapter(adapter);

                // Xóa tất cả các marker trước đó
                mMap.clear();

                // Thêm marker cho tất cả các ki-ốt
                for (Kiosk kiosk : kioskList) {
                    LatLng location = getLocationFromAddress(kiosk.getAddress());
                    if (location != null) {
                        addMarker(location, kiosk.getName());
                    }
                }
            }
        }
    }

    private class FetchNearbyKiosksTask extends AsyncTask<Void, Void, List<Kiosk>> {

        private final double latitude;
        private final double longitude;

        public FetchNearbyKiosksTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected List<Kiosk> doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = Config.getBaseUrl();

                URL url = new URL(BaseUrl + "/api/kiosks/get-nearby-kiosks?latitude=" + latitude + "&longitude=" + longitude + "&maxDistanceKm=5");
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

                    return parseKiosksData(response.toString());
                } else {
                    Log.e("API_ERROR", "Failed to fetch nearby kiosks. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e("API_ERROR", "Exception: " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Kiosk> kiosks) {
            if (kiosks != null) {
                kioskList = kiosks;
                adapter = new KioskAdapter(kioskList, MapFragment.this::onKioskSelected, getContext());
                recyclerView.setAdapter(adapter);

                // Xóa tất cả các marker trước đó
                mMap.clear();

                // Thêm marker cho các ki-ốt gần đây
                for (Kiosk kiosk : kioskList) {
                    LatLng location = getLocationFromAddress(kiosk.getAddress());
                    if (location != null) {
                        addMarker(location, kiosk.getName());
                    }
                }
            }
        }
    }

    private List<Kiosk> parseKiosksData(String jsonResponse) throws JSONException {
        List<Kiosk> kioskList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");
            String phoneNumber = jsonObject.getString("phoneNumber");
            String email = jsonObject.getString("email");
            int accountID = jsonObject.getInt("accountID");
            String openingHours = jsonObject.getString("openingHours");
            String createdAt = jsonObject.getString("createdAt");
            String updatedAt = jsonObject.getString("updatedAt");
            boolean status = jsonObject.getBoolean("status");

            if (status) {
                Kiosk kiosk = new Kiosk(id, name, address, phoneNumber, email, accountID, openingHours, createdAt, updatedAt, status);
                kioskList.add(kiosk);
            }
        }

        return kioskList;
    }

    private LatLng getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            Log.e("Geocoder Error", "Error getting location from address: " + e.getMessage());
        }
        return null;
    }

    private void addMarker(LatLng location, String title) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        }
    }

    private void onKioskSelected(Kiosk kiosk) {
        LatLng location = getLocationFromAddress(kiosk.getAddress());
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        }

        // Xử lý sự kiện bấm vào nút gọi điện
        ImageButton btnCall = getView().findViewById(R.id.btn_call);
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + kiosk.getPhoneNumber()));
            startActivity(intent);
        });

        // Xử lý sự kiện bấm vào nút gửi email
        ImageButton btnEmail = getView().findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + kiosk.getEmail()));
            startActivity(intent);
        });
    }
}
