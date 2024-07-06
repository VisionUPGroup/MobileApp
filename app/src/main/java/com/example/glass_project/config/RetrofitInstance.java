package com.example.glass_project.config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://visionup.azurewebsites.net/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

    public static OrderService getOrderService() {
        return getRetrofitInstance().create(OrderService.class);
    }

    public static PaymentService getPaymentService() {
        return getRetrofitInstance().create(PaymentService.class);
    }

    public static CartService getCartService() {
        return getRetrofitInstance().create(CartService.class);
    }
}
