package com.example.glass_project.config.services;

import com.example.glass_project.DTO.CartDTO.CartSummaryResponse;
import com.example.glass_project.data.model.request.CartRequest;
import com.example.glass_project.data.model.response.CartResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartServices {
    String CART = "api/Cart";

    @POST(CART)
    Call<CartResponse> addToCart(@Body CartRequest cartRequest);

    @GET(CART + "/{accountId}")
    Call<CartSummaryResponse> getbyaccountid(@Path("accountId") Object accountId);
}
