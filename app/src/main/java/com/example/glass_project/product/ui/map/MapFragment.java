package com.example.glass_project.product.ui.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.auth.baseUrl;
import com.example.glass_project.data.adapter.KioskAdapter;
import com.example.glass_project.data.model.kiosk.Kiosk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private KioskAdapter adapter;
    private List<Kiosk> kioskList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Fetch data from API
        new FetchKiosksTask().execute();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private class FetchKiosksTask extends AsyncTask<Void, Void, List<Kiosk>> {
        @Override
        protected List<Kiosk> doInBackground(Void... voids) {
            try {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");
                String BaseUrl = baseUrl.BASE_URL;

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
                    Log.e("API_ERROR", "Failed to fetch data. Response Code: " + responseCode);
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
