package com.example.antdashboard.data.interactors;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.antdashboard.models.ActivityResponse;
import com.example.antdashboard.models.User;
import com.example.antdashboard.network.API.ActivityApi;
import com.example.antdashboard.network.API.AuthApi;
import com.example.antdashboard.network.RetrofitInit;
import com.example.antdashboard.views.ProfileContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileInteractor {
    private ProfileContract.Presenter presenter;
    private static final String TAG = "ProfileInteractor";

    public SharedPreferences sharedPreferences;

    public ProfileInteractor(ProfileContract.Presenter presenter) {
        this.presenter = presenter;
    }


    public void getProfile(String token){
        AuthApi api =  RetrofitInit.getRetrofitInstance().create(AuthApi.class);
        Call<User> profileresponse = api.profile(token);
        Log.d(TAG, "getProfile: " + token);
        profileresponse.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body().isSuccess()){
                        String fullname = response.body().getUser().getFullname();
                        String email = response.body().getUser().getEmail();
                        String emContact = response.body().getUser().getEmContact();
                        String contact = response.body().getUser().getContact();


                        presenter.onSuccess(new User(fullname, email, emContact, contact));
                    }else{
                        presenter.onFailed("User not logged in!");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                presenter.onFailed("Connection Error!");
            }
        });
    }


    public void getActivity(String token){
        ActivityApi api = RetrofitInit.getRetrofitInstance().create(ActivityApi.class);
        Call<ActivityResponse> activityResponse = api.getActivity(token);

        activityResponse.enqueue(new Callback<ActivityResponse>() {
            @Override
            public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                if(response.body().isSuccess()){
                    if(response.body().getActivity() != null){
                        double totalAvgSpeed = response.body().getActivity().getTotalAvgSpeed();
                        int totalDistance = response.body().getActivity().getTotalDistance();

                        presenter.getActivity(new ActivityResponse(totalAvgSpeed, totalDistance));
                    }else{
                        presenter.getActivity(new ActivityResponse(10, 0));

                    }
                }else{
                    presenter.getActivity(new ActivityResponse(10, 0));
                    presenter.onFailed("Failed to get activity!");
                }
            }

            @Override
            public void onFailure(Call<ActivityResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                presenter.onFailed("Connection Error!");
            }
        });
    }

}
