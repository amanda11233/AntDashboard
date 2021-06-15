package com.example.antdashboard.models;

public class ActivityResponse {

    private boolean success;
    private String message;
    private double totalAvgSpeed;
    private int totalDistance;
    private ActivityResponse activity;

    private String _id;

    public ActivityResponse(double totalAvgSpeed, int totalDistance) {
        this.totalAvgSpeed = totalAvgSpeed;
        this.totalDistance = totalDistance;
    }

    public ActivityResponse(String _id, double totalAvgSpeed, int totalDistance) {
        this.totalAvgSpeed = totalAvgSpeed;
        this.totalDistance = totalDistance;
        this._id = _id;
    }

    public ActivityResponse(boolean success, String message, ActivityResponse activity) {
        this.success = success;
        this.message = message;
        this.activity = activity;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double getTotalAvgSpeed() {
        return totalAvgSpeed;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public ActivityResponse getActivity() {
        return activity;
    }

    public String get_id() {
        return _id;
    }

    public ActivityResponse(){}
}
