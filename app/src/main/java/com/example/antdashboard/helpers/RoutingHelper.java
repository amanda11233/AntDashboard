package com.example.antdashboard.helpers;

import android.content.Context;
import android.content.ContextWrapper;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;

public class RoutingHelper  extends ContextWrapper {
    private Routing routing;
    public RoutingHelper(Context base) {
        super(base);
    }

    public Routing getRouting(RoutingListener routingListener, LatLng startPoint, LatLng endPoint){
        routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(routingListener)
                .alternativeRoutes(true)
                .waypoints(startPoint, endPoint)
                .key("AIzaSyDmD5A98K2Q6JM92Wb3LkHLlJafBr98yfI")  //also define your api key here.
                .build();



        return routing;

    }
}
