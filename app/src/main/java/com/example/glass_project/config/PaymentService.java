package com.example.glass_project.config;

import com.example.glass_project.DTO.PaymentDTO.PaymentGetLinkRequest;
import com.example.glass_project.DTO.PaymentDTO.PaymentGetLinkResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PaymentService {
    String ORDER = "api/Payment";

    @Headers("Content-Type: application/json")
    @POST(ORDER + "/create-payment-url")
    Call<PaymentGetLinkResponse> createPaymentUrl(@Header("Return-Url") String returnUrl, @Body PaymentGetLinkRequest paymentGetLinkRequest);
}
