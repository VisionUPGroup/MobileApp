package com.example.glass_project.config.repositories;

import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.services.LensServices;

public class LensRepositories {
    public static LensServices getLensServices() {
        return RetrofitInstance.getRetrofitInstance().create(LensServices.class);
    }
}
