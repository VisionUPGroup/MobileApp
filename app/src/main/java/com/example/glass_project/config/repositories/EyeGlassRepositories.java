package com.example.glass_project.config.repositories;

import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.services.EyeGlassServices;

public class EyeGlassRepositories {
    public static EyeGlassServices getEyeGlassServices() {
        return RetrofitInstance.getRetrofitInstance().create(EyeGlassServices.class);
    }
}
