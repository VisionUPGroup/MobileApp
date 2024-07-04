package com.example.glass_project.product.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glass_project.config.ApiService;
import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.data.adapter.GlassAdapter;
import com.example.glass_project.data.model.EyeGlass;
import com.example.glass_project.data.model.ResponseData;
import com.example.glass_project.databinding.FragmentHomeBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Call<ResponseData> call = apiService.getGlasses();

        // This is the retrofit2 call to get the list of glasses
        call.enqueue(new retrofit2.Callback<ResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseData> call, @NonNull retrofit2.Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EyeGlass> eyeGlasses = response.body().getData();

                    // Set adapter for recycler view
                    GlassAdapter glassAdapter = new GlassAdapter(getContext(), eyeGlasses);
                    recyclerView.setAdapter(glassAdapter);

                } else {
                    Log.e("API Error", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseData> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage(), t);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}