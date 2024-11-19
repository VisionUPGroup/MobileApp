package com.example.glass_project.product.ui.eyeCheck;

import com.example.glass_project.config.CustomVisionService;
import com.example.glass_project.data.model.camera.PredictionResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomVisionClient {
    private static final String BASE_URL = "https://southeastasia.api.cognitive.microsoft.com/";
    private static final String PREDICTION_KEY = "082723e13e5948bdba90c53b60f11e1f";
    // Prediction Key từ Custom Vision

    private final CustomVisionService service;

    public CustomVisionClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CustomVisionService.class);
    }

    public void predictImage(File imageFile, Callback<PredictionResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        service.predictImage(PREDICTION_KEY, requestBody).enqueue(callback);
    }
}
