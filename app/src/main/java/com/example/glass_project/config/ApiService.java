package com.example.glass_project.config;

import com.example.glass_project.data.model.ResponseData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/EyeGlass")
    Call<ResponseData> getGlasses();
}
