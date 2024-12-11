package com.example.glass_project.config.services;

import com.example.glass_project.data.model.camera.PredictionResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CustomVisionService {
    @POST("customvision/v3.0/Prediction/bb7f3604-b5ac-4497-9017-fa55c4db42ba/detect/iterations/Iteration1/image")
    Call<PredictionResponse> predictImage(
            @Header("Prediction-Key") String predictionKey,
            @Body RequestBody image
    );
}
