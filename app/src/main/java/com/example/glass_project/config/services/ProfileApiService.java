package com.example.glass_project.config.services;



import com.example.glass_project.data.model.profile.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProfileApiService {

    @GET("api/profiles")
    Call<List<Profile>> getProfiles(
            @Query("AccountID") int accountID,
            @Query("PageIndex") int pageIndex,
            @Query("PageSize") int pageSize,
            @Query("Descending") boolean descending
    );
}
