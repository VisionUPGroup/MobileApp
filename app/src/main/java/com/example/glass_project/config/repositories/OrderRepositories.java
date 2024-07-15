package com.example.glass_project.config.repositories;

import com.example.glass_project.config.OrderService;
import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.services.LensServices;

public class OrderRepositories {
    public static OrderService getOrderServices() {
        return RetrofitInstance.getRetrofitInstance().create(OrderService.class);
    }
}
