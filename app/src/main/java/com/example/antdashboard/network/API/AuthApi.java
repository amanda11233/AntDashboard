package com.example.antdashboard.network.API;

import com.example.antdashboard.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<User> login(@Body User user);

    @POST("auth/register")
    Call<User> register(@Body User user);

    @GET("auth/profile")
    Call<User> profile(@Header("authorization") String token);


}
