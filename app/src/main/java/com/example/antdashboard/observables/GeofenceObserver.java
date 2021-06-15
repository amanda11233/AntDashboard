package com.example.antdashboard.observables;

import java.util.Observable;

public class GeofenceObserver extends Observable {
    private static GeofenceObserver instance = new GeofenceObserver();

    public static GeofenceObserver getInstance() {
        return instance;
    }

    private GeofenceObserver() {
    }

    public void updateValue(Object data, int value) {
        synchronized (this) {
            setChanged();
            notifyObservers(value);
        }
    }
}
