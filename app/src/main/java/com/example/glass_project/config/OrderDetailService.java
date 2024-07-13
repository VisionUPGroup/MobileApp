package com.example.glass_project.config;

import com.example.glass_project.DTO.OrderDTO.OrderWithOrderDetailRequest;
import com.example.glass_project.model.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderDetailService {
    String ORDER = "api/Order";

    @GET(ORDER)
    Call<Order[]> getAll();

    @GET(ORDER + "/{id}")
    Call<Order> getbyid(@Path("id") Object id);

    @POST(ORDER)
    Call<Order> create(@Body OrderWithOrderDetailRequest orderWithOrderDetailRequest);

    @PUT(ORDER)
    Call<Order> update(@Body Order nhanvien);

    @DELETE(ORDER + "/{id}")
    Call<Order> delete(@Path("id") Object id);
}
