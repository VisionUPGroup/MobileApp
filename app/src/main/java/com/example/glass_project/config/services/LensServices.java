package com.example.glass_project.config.services;

import com.example.glass_project.data.model.Lens;
import com.example.glass_project.data.model.LensType;
import com.example.glass_project.data.model.ResponseLensList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LensServices {
    @GET("api/LensType")
    Call<List<LensType>> getLensType();

    @GET("/api/Lens")
    Call<ResponseLensList> getLens();

}
