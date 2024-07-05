package com.example.glass_project.config.repositories;

import com.example.glass_project.config.RetrofitInstance;
import com.example.glass_project.config.services.AuthServices;

public class AuthRepositories {
    public static AuthServices getAuthServices() {
        return RetrofitInstance.getRetrofitInstance().create(AuthServices.class);
    }
}
