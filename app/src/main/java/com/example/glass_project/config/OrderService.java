package com.example.glass_project.config;

import com.example.glass_project.DTO.OrderDTO.OrderResponse;
import com.example.glass_project.DTO.OrderDTO.OrderWithOrderDetailRequest;
import com.example.glass_project.model.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderService {
    String ORDER = "api/Order";

    @GET(ORDER)
    Call<Order[]> getAll();

    @GET(ORDER + "/{id}")
    Call<Order> getbyid(@Path("id") Object id);

    @GET(ORDER + "/account/{account_id}")
    Call<Order[]> getbyaccountid(@Path("account_id") Object account_id);

    @POST(ORDER + "/Product")
    Call<OrderResponse> create(@Body OrderWithOrderDetailRequest orderWithOrderDetailRequest);

    @PUT(ORDER)
    Call<Order> update(@Body Order order);

    @DELETE(ORDER + "/{id}")
    Call<Order> delete(@Path("id") Object id);
}
