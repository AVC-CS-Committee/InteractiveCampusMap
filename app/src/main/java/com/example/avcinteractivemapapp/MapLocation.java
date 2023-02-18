package com.example.avcinteractivemapapp;

import org.json.JSONArray;

public class MapLocation {
    private String locationDescription;
    private JSONArray locationImages;

    public MapLocation(String locationDescription, JSONArray locationImages) {
        this.locationDescription = locationDescription;
        this.locationImages = locationImages;
    }

    public String getDescription() {
        return locationDescription;
    }

    public String getImages() {
        return locationImages.toString();
    }
}
