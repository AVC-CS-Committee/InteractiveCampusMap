package com.example.avcinteractivemapapp;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class SearchBar implements SearchView.OnQueryTextListener {
    private final HashMap<Marker, MapLocation> locations;
    private final GoogleMap mMap;
    private final MapsFragment fragment;

    public SearchBar(HashMap<Marker, MapLocation> locations, GoogleMap mMap, MapsFragment fragment) {
        this.locations = locations;
        this.mMap = mMap;
        this.fragment = fragment;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // checking if the entered location is null or not.
        if (query == null || query.equals("")) return false;

        // Remove capital letters from query and remove whitespace
        query = query.toLowerCase().replaceAll("\\s+", "");

        Log.i("searchbar", String.valueOf(fragment.isCircleFilterOn));

        if (fragment.isCircleFilterOn) {
            fragment.disableCircleFilter();
            fragment.filterMarkers();
        }

        if (!fragment.isFiltersDisabled()) {
            fragment.disableAllFilters();
            fragment.filterMarkers();
        }

        // Iterate through each entry in the HashMap
        for (Map.Entry<Marker, MapLocation> entry : locations.entrySet()) {
            String locationTitle = entry.getValue().getLocationTitle().toLowerCase().replaceAll("\\s+", "");

            // If matches, move camera and show marker title
            if (locationTitle.contains(query)) {
                fragment.moveMapCamera(mMap, entry.getValue().getLocationCoords(), fragment.MARKER_ZOOM);
                entry.getKey().showInfoWindow();
            }
        }

        return false;
    }

    public static void hideKeyboard(SearchView searchView, Context activity){
        // Get a reference to the InputMethodManager system service
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Hide the keyboard for the SearchView
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.clearFocus();
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
