package com.example.antdashboard.presenters;

import com.example.antdashboard.data.interactors.ActivityInteractor;
import com.example.antdashboard.models.Activity;
import com.example.antdashboard.views.ActivityContract;

public class ActivityPresenter implements ActivityContract.Presenter {

    private ActivityContract.View view;
    private ActivityInteractor interactor;

    public ActivityPresenter(ActivityContract.View view) {
        this.view = view;
        interactor = new ActivityInteractor(this);
    }

    public void addActivity(String token, Activity activity){
        interactor.addActivity(token, activity);
    }

    @Override
    public void onSuccess(String message) {
        view.onSuccess(message);
    }

    @Override
    public void onFailed(String message) {
        view.onFailed(message);
    }
}
