package com.example.glass_project.config;

import com.example.glass_project.DTO.CartDTO.CartSummaryResponse;
import com.example.glass_project.DTO.OrderDTO.OrderWithOrderDetailRequest;
import com.example.glass_project.model.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartService {
    String CART = "api/Cart";

    @GET(CART + "/{accountId}")
    Call<CartSummaryResponse> getbyaccountid(@Path("accountId") Object accountId);
}
