package com.example.antdashboard.repo;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.example.antdashboard.DashboardActivity;
import com.example.antdashboard.R;
import com.example.antdashboard.helpers.GeofenceHelper;
import com.example.antdashboard.helpers.PermissionHelper;
import com.example.antdashboard.helpers.RoutingHelper;
import com.example.antdashboard.helpers.SnackbarHelper;
import com.example.antdashboard.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class GoogleMapsBuilder extends ContextWrapper implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {


    private static final String TAG = "GoogleMapsBuilder";
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private GoogleMap mMap;
    private SnackbarHelper snackbarHelper;

    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng end = null;

    //polyline object
    private List<Polyline> polylines = null;
    private RoutingHelper routingHelper;
    private DashboardActivity dashboardActivity;


    public GoogleMapsBuilder(Context base, DashboardActivity activity, SnackbarHelper snackbarHelper) {
        super(base);
        this.dashboardActivity = activity;
        this.snackbarHelper = snackbarHelper;
        routingHelper = new RoutingHelper(this);
        geofencingClient = LocationServices.getGeofencingClient(activity);
        geofenceHelper = new GeofenceHelper(this);

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        snackbarHelper.showMessageWithDismiss(dashboardActivity, e.toString());

    }

    @Override
    public void onRoutingStart() {
        snackbarHelper.showMessage(dashboardActivity, "Searching for Nearest Possible Route...");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        snackbarHelper.hide(dashboardActivity);
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.softwarica_blue));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        snackbarHelper.showMessageWithDismiss(dashboardActivity, "Not Connected to the internet!!!");
        findRoutes(start,end);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();

        if (Build.VERSION.SDK_INT >= 29) {
            if(!PermissionHelper.hasBackgroundLocationPermission(dashboardActivity)){
                if(PermissionHelper.shouldShowRequestPermissionRationale(dashboardActivity, PermissionHelper.BACKGROUND_LOCATION_PERMISSION))
                {
                    PermissionHelper.requestBackgroundPermission(dashboardActivity);
                }else{
                    PermissionHelper.requestBackgroundPermission(dashboardActivity);
                }
            }else{
                addMarker(new LatLng(27.70459728110769, 85.32920976370872));
                addCircle(new LatLng(27.70459728110769, 85.32920976370872), Constants.GEOFENCE_RADIUS);
                addMarker(new LatLng(27.723205098076303, 85.32146573412854));
                addCircle(new LatLng(27.723205098076303, 85.32146573412854), Constants.GEOFENCE_RADIUS);
                addGeofence(new LatLng(27.70459728110769, 85.32920976370872), Constants.GEOFENCE_RADIUS);
            }
        } else {
            addMarker(new LatLng(27.70459728110769, 85.32920976370872));
            addCircle(new LatLng(27.70459728110769, 85.32920976370872), Constants.GEOFENCE_RADIUS);
            addMarker(new LatLng(27.723205098076303, 85.32146573412854));
            addCircle(new LatLng(27.723205098076303, 85.32146573412854), Constants.GEOFENCE_RADIUS);
            addGeofence(new LatLng(27.70459728110769, 85.32920976370872), Constants.GEOFENCE_RADIUS);
        }
    }


    public void mapInit(){
        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) dashboardActivity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                myLocation=location;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                mMap.animateCamera(cameraUpdate);
            }
        });

        //get destination location when user click on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                end=latLng;

                mMap.clear();

                start=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                //start route finding
                findRoutes(start,end);
            }
        });

    }

    // function to find Routes.
    public void findRoutes(LatLng startPoint, LatLng endPoint)
    {
        if(startPoint==null || endPoint==null) {
            makeText(this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
            routingHelper.getRouting(this, startPoint, endPoint).execute();
        }
    }



    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }


    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(Constants.GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeText(dashboardActivity, "Geofence Added...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }


    private void enableUserLocation() {
        if(!PermissionHelper.hasLocationPermission(dashboardActivity)){
            if(PermissionHelper.shouldShowRequestPermissionRationale(dashboardActivity, PermissionHelper.FINE_LOCATION_PERMISSION)){
                PermissionHelper.requestLocationPermission(dashboardActivity);
            }else{
                PermissionHelper.requestLocationPermission(dashboardActivity);
            }
        }else{
            mMap.setMyLocationEnabled(true);
        }

    }

}
