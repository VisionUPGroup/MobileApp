package com.example.glass_project.config.services;

import com.example.glass_project.data.model.notification.DeviceTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/notifications/device-tokens")
    Call<Void> saveDeviceToken(@Body DeviceTokenRequest request);

    @DELETE("api/notifications/device-tokens/{accountId}/{deviceToken}")
    Call<Void> deleteDeviceToken(@Path("accountId") String accountId, @Path("deviceToken") String deviceToken);

    @GET("api/notifications/device-tokens/{accountId}")
    Call<Object> getDeviceTokens(@Path("accountId") String accountId);
}
