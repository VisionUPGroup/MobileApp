package com.example.glass_project.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.R;
import com.example.glass_project.config.repositories.LensRepositories;
import com.example.glass_project.config.services.LensServices;
import com.example.glass_project.data.adapter.LensAdapter;
import com.example.glass_project.data.model.Lens;
import com.example.glass_project.data.model.LensType;
import com.example.glass_project.data.model.ResponseLensList;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLensActivity extends AppCompatActivity {

    private final List<LensType> lensTypeList = new ArrayList<>();
    private LensAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_len);

        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.rv_select_lens);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LensAdapter(lensTypeList, this);
        recyclerView.setAdapter(adapter);

        fetchLensData();
    }

    private void fetchLensData() {
        progressBar.setVisibility(View.VISIBLE);

        LensServices lensServices = LensRepositories.getLensServices();

        lensServices.getLensType().enqueue(new Callback<List<LensType>>() {
            @Override
            public void onResponse(@NonNull Call<List<LensType>> call, @NonNull Response<List<LensType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lensTypeList.clear();
                    lensTypeList.addAll(response.body());
                    fetchLensDetails();  // Fetch lens details after lens types are loaded
                } else {
                    Log.e("API Error", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LensType>> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    // Handle timeout error
                    Log.e("API Error", "Timeout occurred: " + t.getMessage());
                } else {
                    // Handle other errors
                    Log.e("API Error", t.getMessage(), t);
                }
            }
        });
    }

    private void fetchLensDetails() {
        LensServices lensServices = LensRepositories.getLensServices();

        lensServices.getLens().enqueue(new Callback<ResponseLensList>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResponseLensList> call, @NonNull Response<ResponseLensList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Lens> lensList = response.body().getData();

                    for (LensType lensType : lensTypeList) {
                        List<Lens> assignedLensList = new ArrayList<>();
                        for (Lens lens : lensList) {
                            if (lens.getLensTypeID() == lensType.getId()) {
                                assignedLensList.add(lens);
                            }
                        }
                        lensType.setLens(assignedLensList);
                    }

                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();  // Update adapter with new data
                } else {
                    Log.e("API Error", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseLensList> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
