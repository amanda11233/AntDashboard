package com.example.antdashboard.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static final int FINE_LOCATION_REQUEST_CODE = 23;
    public static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int BACKGROUND_LOCATION_REQUEST_CODE = 33;



    public static final int PERMISSIONS_REQUEST_CAMERA = 0;
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int PERMISSIONS_REQUEST_STORAGE = 2;

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO;
    public static final String BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;




    public static boolean hasBackgroundLocationPermission(Activity activity){
        return ContextCompat.checkSelfPermission(activity, BACKGROUND_LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** Check to see we have the necessary permissions for this app. */
    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasLocationPermission(Activity activity){
        return ContextCompat.checkSelfPermission(activity, FINE_LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean hasStoragePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, EXTERNAL_STORAGE_PERMISSION)
                == PackageManager.PERMISSION_GRANTED ;
    }

    public static boolean hasAudioPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, AUDIO_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(
                activity, new String[] {FINE_LOCATION_PERMISSION}, FINE_LOCATION_REQUEST_CODE
        );
    }

    public static void requestBackgroundPermission(Activity activity){
        ActivityCompat.requestPermissions(
                activity, new String[] {BACKGROUND_LOCATION_PERMISSION}, BACKGROUND_LOCATION_REQUEST_CODE);
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {EXTERNAL_STORAGE_PERMISSION}, PERMISSIONS_REQUEST_STORAGE);
    }

    public static void requestAudioPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {AUDIO_PERMISSION}, PERMISSIONS_REQUEST_RECORD_AUDIO);
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {CAMERA_PERMISSION}, PERMISSIONS_REQUEST_CAMERA);
    }

    /** Check to see if we need to show the rationale for this permission. */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String PERMISSION) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION);
    }

    /** Launch Application Setting to grant permission. */
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

}
