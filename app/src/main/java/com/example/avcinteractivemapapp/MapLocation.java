package com.example.avcinteractivemapapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

public class MapLocation {
    private final String locationTitle;
    private final String locationDescription;
    private final LatLng locationCoords;
    private final JSONArray locationImages;

    public MapLocation(String locationTitle, String locationDescription, LatLng locationCoords, JSONArray locationImages) {
        this.locationTitle = locationTitle;
        this.locationDescription = locationDescription;
        this.locationCoords = locationCoords;
        this.locationImages = locationImages;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getDescription() {
        return locationDescription;
    }

    public LatLng getLocationCoords() {
        return locationCoords;
    }

    public String getImages() {
        return locationImages.toString();
    }
}
