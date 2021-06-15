package com.example.antdashboard.presenters;

import com.example.antdashboard.data.interactors.ProfileInteractor;
import com.example.antdashboard.models.ActivityResponse;
import com.example.antdashboard.models.User;
import com.example.antdashboard.views.ProfileContract;

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileInteractor interactor;
    private ProfileContract.View view;

    public ProfilePresenter(ProfileContract.View view) {
        this.interactor = interactor;
        this.view = view;

        interactor = new ProfileInteractor(this);
    }

    public void getProfile(String token){
        interactor.getProfile(token);
    }
    public void getUserActivity(String token){
        interactor.getActivity(token);
    }

    @Override
    public void onSuccess(User user) {
        view.onSuccess(user);
    }

    @Override
    public void getActivity(ActivityResponse activity) {
        view.getActivity(activity);
    }

    @Override
    public void onFailed(String message) {
        view.onFailed(message);
    }
}
