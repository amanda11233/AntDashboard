package com.example.antdashboard.views;

public interface AuthContract {
    //User Authentication View
    interface View{
        void onSuccess();
        void onFailed(String message);
    }


    //User Authentication Presenter
    interface Presenter{
        void onSuccess();
        void onFailed(String message);
    }
}
