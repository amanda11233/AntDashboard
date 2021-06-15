package com.example.antdashboard.views;

public interface ActivityContract {

    interface View{
        void onSuccess(String message);
        void onFailed(String message);
    }


    //User Authentication Presenter
    interface Presenter{
        void onSuccess(String message);
        void onFailed(String message);
    }
}
