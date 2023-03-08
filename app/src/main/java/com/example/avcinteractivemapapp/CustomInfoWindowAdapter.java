package com.example.avcinteractivemapapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;

    public CustomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    // TODO: Setup dynamic description and image
    private void render(Marker marker, View view) {
        // Get references to views in the custom layout
        TextView titleTextView = view.findViewById(R.id.windowTitle);
        TextView descriptionTextView = view.findViewById(R.id.windowDescription);
        //ImageView imageView = view.findViewById(R.id.windowImage);

        // Set Title
        titleTextView.setText(marker.getTitle());

        // Set Description
//        String description = MapsFragment.getMarkerType(marker);
//        String capitalized = description.substring(0, 1).toUpperCase() + description.substring(1);
//        descriptionTextView.setText(capitalized);

        // TODO: Set image related to location
        // Set Image
        //imageView.setImageResource(R.drawable.image_yoshidahall);
    }
}
