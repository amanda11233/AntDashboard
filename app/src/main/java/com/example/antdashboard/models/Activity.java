package com.example.antdashboard.models;

public class Activity {

    private User user;
    private String vehicle;
    private String date;
    private String start_time;
    private String end_time;
    private double avg_speed;
    private int kilometer;

    private String totalAvgSpeed;
    private String totalDistance;

    private boolean success;
    private String message;
    private String token;
    private Activity activity;
    private String _id;


    public Activity(User user, String vehicle, String date, String start_time, String end_time, double avg_speed, int kilometer, String totalAvgSpeed, String totalDistance) {
        this.user = user;
        this.vehicle = vehicle;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.avg_speed = avg_speed;
        this.kilometer = kilometer;
        this.totalAvgSpeed = totalAvgSpeed;
        this.totalDistance = totalDistance;
    }
    public Activity(){}

    public Activity(String token, String start_time){
        this.token = token;
        this.start_time = start_time;
    }
    public Activity(String _id, String totalAvgSpeed, String totalDistance, String value){
        this._id = _id;
        this.totalAvgSpeed = totalAvgSpeed;
        this.totalDistance = totalDistance;
    }

    public Activity(String end_time, double avg_speed, int kilometer, String date) {
        this.date = date;
        this.vehicle = vehicle;
        this.start_time = start_time;
        this.end_time = end_time;
        this.avg_speed = avg_speed;
        this.kilometer = kilometer;
    }
    public Activity(String vehicle, String start_time, String date) {
        this.date = date;
        this.vehicle = vehicle;
        this.start_time = start_time;
        this.end_time = end_time;
        this.avg_speed = avg_speed;
        this.kilometer = kilometer;
    }
    public Activity(boolean success, String message) {
        this.success = success;
        this.message = message;

    }


    public User getUser() {
        return user;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getDate() {
        return date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public int getKilometer() {
        return kilometer;
    }

    public String getTotalAvgSpeed() {
        return totalAvgSpeed;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
