package com.example.antdashboard.data.interactors;

import android.util.Log;

import com.example.antdashboard.models.Activity;
import com.example.antdashboard.network.API.ActivityApi;
import com.example.antdashboard.network.RetrofitInit;
import com.example.antdashboard.views.ActivityContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityInteractor {

    private ActivityContract.Presenter presenter;

    private static final String TAG = "ActivityInteractor";

    public ActivityInteractor(ActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }


    public void addActivity(String token, Activity activity){
        ActivityApi api = RetrofitInit.getRetrofitInstance().create(ActivityApi.class);
        Call<Activity> activityResponse = api.addActivity(token, activity);

        activityResponse.enqueue(new Callback<Activity>() {
            @Override
            public void onResponse(Call<Activity> call, Response<Activity> response) {
                if(response.body().isSuccess()){

                }else{
                    presenter.onFailed("Failed to add activity");
                }
            }

            @Override
            public void onFailure(Call<Activity> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                presenter.onFailed("Connection Error!");
            }
        });


    }
}
