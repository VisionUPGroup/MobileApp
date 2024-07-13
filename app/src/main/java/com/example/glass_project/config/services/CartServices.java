package com.example.glass_project.config.services;

import com.example.glass_project.data.model.request.CartRequest;
import com.example.glass_project.data.model.response.CartResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CartServices {
    @POST("api/Cart")
    Call<CartResponse> addToCart(@Body CartRequest cartRequest);


}
