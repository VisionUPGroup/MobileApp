package com.example.glass_project.config.repositories;

import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.services.CartServices;

public class CartRepositories {
    public static CartServices cartServices() {
        return RetrofitInstance.getRetrofitInstance().create(CartServices.class);
    }
}
