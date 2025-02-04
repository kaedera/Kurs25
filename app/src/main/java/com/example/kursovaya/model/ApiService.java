package com.example.kursovaya.model;

import com.example.kursovaya.api.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/login")
    Call<LoginResponse> login(@Body User user);
}
