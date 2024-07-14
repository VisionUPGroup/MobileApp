package com.example.glass_project.product.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.caverock.androidsvg.BuildConfig;
import com.example.glass_project.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView mapView;
    private Marker marker;
    private GeoPoint markerPoint = new GeoPoint(10.87573, 106.80291); // Coordinates of Ho Chi Minh City

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Configure OSMDroid User Agent
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Initialize MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Set the initial zoom level and center the map on Ho Chi Minh City
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(markerPoint);

        // Add a marker at the specified location
        addMarker(markerPoint, "Vision Up", "Địa Chỉ Nhà Văn Hóa Sinh Viên Dĩ An Bình Dương");

        return view;
    }

    private void addMarker(GeoPoint geoPoint, String title, String snippet) {
        // Remove existing marker if exists
        if (marker != null) {
            mapView.getOverlays().remove(marker);
        }

        marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        mapView.getOverlays().add(marker);
        mapView.invalidate(); // Refresh map
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Needed for OSMDroid
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Needed for OSMDroid
    }
}