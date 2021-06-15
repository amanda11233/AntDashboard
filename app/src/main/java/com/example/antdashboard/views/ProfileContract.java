package com.example.antdashboard.views;

import com.example.antdashboard.models.ActivityResponse;
import com.example.antdashboard.models.User;

public interface ProfileContract {
    interface View{
        void onSuccess(User user);
        void getActivity(ActivityResponse activity);
        void onFailed(String message);
    }


    //User Authentication Presenter
    interface Presenter{
        void onSuccess(User user);
        void getActivity(ActivityResponse activity);
        void onFailed(String message);
    }
}
