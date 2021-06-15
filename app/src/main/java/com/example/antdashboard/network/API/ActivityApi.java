package com.example.antdashboard.network.API;

import com.example.antdashboard.models.Activity;
import com.example.antdashboard.models.ActivityResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ActivityApi {
    @GET("activity/")
    Call<ActivityResponse> getActivity(@Header("Authorization") String token);

    @POST("activity/")
    Call<Activity> addActivity(@Header("Authorization") String token,
                               @Body Activity activity);

}
