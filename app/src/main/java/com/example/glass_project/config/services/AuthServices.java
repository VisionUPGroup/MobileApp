package com.example.glass_project.config.services;

import com.example.glass_project.data.model.Login;
import com.example.glass_project.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthServices {
    @POST("api/Account/Login")
    Call<LoginResponse> login(@Body Login login);
}
