package com.example.avcinteractivemapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*TODO: (FOR THOSE WORKING ON THE GOOGLE MAPS API)
        Tutorial being followed: https://youtu.be/lBW58tPLn-A?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi
        -Restrict API Key; get done before deployment
        -Polish Google Services, GPS, and location permissions
        -Add custom markers to each location (buildings, parking lots, stadium, etc.)
        -Set boundaries (DONE)
        -On marker click open a popup menu with info about that location
 */

/*TODO: Implement the functionality of the maps legend. Create an event listener that listens to current checks in
        the drawer menu so that it will hide or remove all markers associated with the checklists description.
*/

/*
    DONE: Nearest Parking calculator
            -the user taps anywhere on avc and the nearest parking lot is shown (don't need the markers on every building)
            -we'd only need the parking lot markers
            -calculates the distance between the place the user tapped on the map and all the parking lot markers
            -the marker that is the closest is displayed (and a path is revealed to it?)
 */

/*
    TODO: Code to eventually refactor
        -Adding markers for each location
        -Adding popups on each marker click?
        -Parking Calculator

 */

/**
 * DESCRIPTION:
    This class (fragment) is for managing the Google Maps API. All the features that need to be
    added to the map go here. Whenever MainActivity executes, it automatically calls on
    getSupportFragmentManager() which loads the code in this fragment.
    When this class is instantiated, onCreateView() (which is in this class) is called.
    Inside this method, onMapReady() is called. This is where most of the logic for the map goes and
    where code for implementing a new feature related to the map should be written.
 */
public class MapsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Get the center map button
        ImageButton centerMapsButton = view.findViewById(R.id.center_map);


        // ArrayList used for the logic of removing previously placed user marker
        ArrayList<Marker> userMarker = new ArrayList<>();

        // ArrayList used for parking markers
        ArrayList<Marker> parkingLotMarkers = new ArrayList<>();

        // HashMap used to lookup a location's xml file
        HashMap<Marker, String> locations = new HashMap<>();

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                BitmapDescriptor markerIcon = BitmapFromVector(getActivity(), R.drawable.marker_icon);

                //Set boundary for the map
                final LatLngBounds avcBounds = new LatLngBounds(new LatLng(34.674910, -118.192287), new LatLng(34.682133, -118.183807));
                googleMap.setLatLngBoundsForCameraTarget(avcBounds);

                //Focus AVC and place default marker (uses custom marker)
                LatLng avc = new LatLng(34.6773, -118.1866);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avc, 17.5f));
                locations.put(googleMap.addMarker(new MarkerOptions().position(avc).title("Antelope Valley College").icon(markerIcon)), "avc_popup");

                // Read from locations.json
                InputStream jsonData = getResources().openRawResource(R.raw.locations);
                Scanner scnr = new Scanner(jsonData);
                StringBuilder builder = new StringBuilder();

                // Build locations.json string
                while (scnr.hasNextLine()) {
                    builder.append(scnr.nextLine());
                }
                scnr.close();

                // Parse json string and add locations to HashMap
                try {
                    JSONArray root = new JSONArray(builder.toString());

                    for (int i = 0; i < root.length(); i++) {
                        JSONObject location = root.getJSONObject(i);
                        String title = location.getString("title");
                        double latitude = location.getDouble("latitude");
                        double longitude = location.getDouble("longitude");
                        String xmlFile = location.getString("xml_file");
                        String locationType = location.getString("type");

                        Marker tmpMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).icon(markerIcon));
                        locations.put(tmpMarker, xmlFile);

                        // If the marker is a parking marker, add it to the parkingLotMarkers ArrayList
                        if (locationType.equals("parking")) {
                            parkingLotMarkers.add(tmpMarker);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @SuppressLint("InflateParams")
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View popupView;

                        // Depending on which marker is clicked, a popup view of the corresponding location is opened.
                        // A HashMap is used to check the name of the marker clicked.

                        String popup = locations.get(marker);
                        if (popup == null) return false;

                        // resId stores the id of the corresponding xml file
                        int resId = getResources().getIdentifier(popup, "layout", getContext().getPackageName());

                        popupView = inflater.inflate(resId, null);
                        popupViewCreator(popupView, view);

                        return false;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        //Checks if the userMarker ArrayList has an existing marker
                        if(userMarker.size() > 0){

                            //Removes the existing marker from the map
                            userMarker.get(0).remove();

                            //Removes the existing Marker object from the userMarker ArrayList
                            userMarker.remove(userMarker.get(0));

                        }
                        // Creates a new Marker object and places it at the selected latitude and longitude
                        Marker newUserMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("User Marker"));

                        // Adds the new Marker object to the userMarker ArrayList
                        userMarker.add(newUserMarker);

                        // NOTE: The logic for the code below is as follows: index 0 = lotA1, index 1 = lotA2, ..., index 17 = lotF2

                        // Total number of parking lots
                        final int NUMBER_OF_PARKING_LOTS = 18;

                        //  An array to store the distances to each lot marker from the user's marker.
                        //  (Index 0 = LotA1's distance to the marker, Index 1 = LotA2's distance, Index 2 = LotA3's distance, ..., Index 17 = Lot F2's distance.
                        float[] lotDistancesToMarker = new float[NUMBER_OF_PARKING_LOTS];

                        // Calculates and stores the distance of each marker into the lotDistancesToMarker array
                        for(int i = 0; i < lotDistancesToMarker.length; ++i){
                            // Store the distance in lotDistancesToMarker
                            lotDistancesToMarker[i] = calculateMarkerDistance(newUserMarker, parkingLotMarkers.get(i));
                        }

                        // Finds the smallest value in the lotDistanceToMarker array (this represents the nearest lot marker)
                        // The nearest parking lot's index is stored in nearestLotIndex
                        float smallestDistance = lotDistancesToMarker[0];
                        int nearestLotIndex = 0;
                        for(int i = 0; i < lotDistancesToMarker.length; ++i){
                            if(lotDistancesToMarker[i] < smallestDistance) {
                                smallestDistance = lotDistancesToMarker[i];
                                nearestLotIndex = i;
                            }
                        }

                        // DEBUG PURPOSES
                        // Log.d("RESULTS", "Lot " + parkingLotMarkers.get(nearestLotIndex).getTitle() + " is closest by " + lotDistancesToMarker[nearestLotIndex] + " meters.");

                        //TEMPORARY CODE? (just being used to display that the parking calculator actually works)
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View userMarkerView = inflater.inflate(R.layout.user_marker_popup, null);
                        TextView nearestLotView = userMarkerView.findViewById(R.id.userMarkerPopup).findViewById(R.id.lotView);
                        TextView distanceView = userMarkerView.findViewById(R.id.userMarkerPopup).findViewById(R.id.distanceView);
                        nearestLotView.setText(parkingLotMarkers.get(nearestLotIndex).getTitle());
                        distanceView.setText(Float.toString(lotDistancesToMarker[nearestLotIndex]) + distanceView.getText());
                        popupViewCreator(userMarkerView, view);

                    }
                });

                // Handle center maps button click
                centerMapsButton.setOnClickListener(v -> {
                    LatLng avc1 = new LatLng(34.6773, -118.1866);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(avc1, 17.5f));
                });

                // Adds custom JSON file which uses AVC colors for Google Maps
                try {
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle( getActivity(), R.raw.custom_avc_map)
                    );
                } catch(Resources.NotFoundException e){
                    Log.e("JSON", "Can't find style. Error: ", e);
                }

                // When map is loaded
               /* googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //When something on the map is clicked
                    }
                });*/
            }
        });
        // Return view
        return view;
    }

    /**
     * Calculates distance between two given markers; uses Locations.distanceBetween()
     *
     * @param marker1
     * @param marker2
     * @return The float value in meters of the distance between the two provided markers
     */
    private float calculateMarkerDistance(Marker marker1, Marker marker2){
        // The computed distance is stored in results[0]. (https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2)
        // If results has length 2 or greater, the initial bearing is stored in results[1].
        // If results has length 3 or greater, the final bearing is stored in results[2].
        float[] results = new float[1];
        Location.distanceBetween(marker1.getPosition().latitude, marker1.getPosition().longitude, marker2.getPosition().latitude, marker2.getPosition().longitude, results);
        return results[0];
    }

    // Gets the width of the screen of current device
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // Gets the height of the screen of current device
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    //General code needed to create a new popup. Code used: https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android
    private void popupViewCreator(View popupView, View view){
        // create the popup window
        // Set the width and height slightly smaller than device display screen.
        int width = (getScreenWidth() - 150);
        int height = (getScreenHeight() - 450);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    //Logic for adding a custom marker (https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/#:~:text=For%20adding%20a%20custom%20marker,this%20marker%20to%20our%20Map.)
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}