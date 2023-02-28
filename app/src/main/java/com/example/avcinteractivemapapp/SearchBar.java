package com.example.avcinteractivemapapp;

import android.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class SearchBar implements SearchView.OnQueryTextListener {
    private HashMap<Marker, MapLocation> locations;
    private GoogleMap mMap;
    private MapsFragment fragment;

    public SearchBar(HashMap<Marker, MapLocation> locations, GoogleMap mMap) {
        this.locations = locations;
        this.mMap = mMap;
        this.fragment = new MapsFragment();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // checking if the entered location is null or not.
        if (query == null || query.equals("")) return false;

        // Remove capital letters from query and remove whitespace
        query = query.toLowerCase().replaceAll("\\s+", "");

        // Iterate through each entry in the HashMap
        for (Map.Entry<Marker, MapLocation> entry : locations.entrySet()) {
            String locationTitle = entry.getValue().getLocationTitle().toLowerCase().replaceAll("\\s+", "");

            // If matches, move camera and show marker title
            if (locationTitle.contains(query)) {
                fragment.moveMapCamera(mMap, entry.getValue().getLocationCoords());
                entry.getKey().showInfoWindow();
            }
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
