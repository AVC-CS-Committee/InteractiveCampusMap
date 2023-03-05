package com.example.avcinteractivemapapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

public class MapLocation {
    private String locationTitle;
    private String locationDescription;
    private LatLng locationCoords;
    private JSONArray locationImages;

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
