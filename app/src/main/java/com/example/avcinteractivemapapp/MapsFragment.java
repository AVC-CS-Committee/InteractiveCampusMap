package com.example.avcinteractivemapapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
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

/**
 * DESCRIPTION:
 This class (fragment) is for managing the Google Maps API. All the features that need to be
 added to the map go here. Whenever MainActivity executes, it automatically calls on
 getSupportFragmentManager() which loads the code in this fragment.
 When this class is instantiated, onCreateView() (which is in this class) is called.
 Inside this method, onMapReady() is called. This is where most of the logic for the map goes and
 where code for implementing a new feature related to the map should be written.
 */
public class MapsFragment extends Fragment implements LocationListener {
    // Map related variables
    final float MAX_ZOOM = 14.0f;
    final float INITIAL_ZOOM = 17.5f;
    final LatLng AVC_COORDS = new LatLng(34.6773, -118.1866);

    // Marker Lists
    public ArrayList<Marker> parkingLotMarkers = new ArrayList<>();
    public ArrayList<Marker> classroomLocations = new ArrayList<>();
    public ArrayList<Marker> foodLocations = new ArrayList<>();
    public ArrayList<Marker> athleticLocations = new ArrayList<>();
    public ArrayList<Marker> resourceLocations = new ArrayList<>();

    // HashMap used to lookup a location's MapLocation object
    public HashMap<Marker, MapLocation> locations = new HashMap<>();

    // Locations API Related (GPS Feature)
    LocationManager locationManager;
    double currentLat;
    double currentLong;
    private final int REQUEST_CODE = 101;
    private GoogleMap mMap;

    // GPS Related
    private Circle previousCircle;
    private LocationRequest mLocationRequest;
    public static boolean enableCircleFilter = false;

    // Icons for markers
    BitmapDescriptor parkingMarkerIcon, classroomMarkerIcon, foodMarkerIcon, athleticsMarkerIcon, resourceMarkerIcon;
    // Temporary
    BitmapDescriptor markerIcon;
    ImageButton centerMapButton;
    View view;

    // Handles map manipulation once the map is ready
    // Replaces onMapReady()
    private final OnMapReadyCallback callback = googleMap -> {
        setMapStyle(googleMap);
        setMapBounds(googleMap);
        moveMapCamera(googleMap, AVC_COORDS);

        mMap = googleMap;

        markerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker);
        parkingMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_parking_marker);
        centerMapButton = view.findViewById(R.id.center_map);

        parseJson(googleMap);

        // googleMap.setOnMarkerClickListener();

        // Handles marker title clicks
        googleMap.setOnInfoWindowClickListener(marker -> {
            // Activity Popup
            MapLocation locationInfo = locations.get(marker);
            if (locationInfo == null) return;

            Intent intent = new Intent(getActivity(), TemplateMarkerDescriptions.class);

            // Bundles allow for passing data between activities
            Bundle bundle = new Bundle();
            bundle.putString("title", marker.getTitle());
            bundle.putString("description", locationInfo.getDescription());
            bundle.putString("images", locationInfo.getImages());
            intent.putExtras(bundle);

            startActivity(intent);
        });

        // Handles map clicks (was used for the old version of the nearest lot calculator)
        googleMap.setOnMapClickListener(latLng -> {});

        // Handle map camera movement
        googleMap.setOnCameraMoveListener(() -> {
            CameraPosition position = googleMap.getCameraPosition();

            // Ensures that the user doesn't go over the max zoom amount
            if (position.zoom > MAX_ZOOM) googleMap.setMinZoomPreference(MAX_ZOOM);
        });

        // Handles center map button clicks
        centerMapButton.setOnClickListener(view -> moveMapCamera(googleMap, AVC_COORDS));

        // GPS Related
        //fusedLocationProviderClient = getFusedLocationProviderClient(this.requireActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getCurrentLocation();

        // The uiSettings object removes default Google Maps hover buttons
        UiSettings uiSettings = googleMap.getUiSettings();
        // Removing the "Directions" and "Open in Maps" buttons
        uiSettings.setMapToolbarEnabled(false);

    };
    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public static boolean enableCircleFilter() {
        // 1) Determine user's current location
        // 2) Pass that info. to the nearest location calculator
        // 3) The nearest location calculator checks user's location to all other locations, determines
        //    which are closest based on a predetermined radius around a user
        enableCircleFilter = !enableCircleFilter;
        Log.d("TEST", "Current Value: " + enableCircleFilter);
        return enableCircleFilter;
    }

    public boolean enableParkingCalculator() {
        // Update the current user's location
        getCurrentLocation();

        // Check if the current location exists. If it doesn't, return false
        if (currentLong == 0.0 && currentLat == 0.0) return false;

        // Convert current user's location into a marker
        Marker userLocation = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat, currentLong)));

        // Calculate the nearest lot to the user then grab the lot marker
        Pair<Integer, Double> nearestLot = calculateNearestLot(userLocation);
        Marker nearestLotMarker = parkingLotMarkers.get(nearestLot.first);

        // Remove the temporary marker
        userLocation.remove();

        // Get the lot's coordinates
        LatLng nearestLotCoords = nearestLotMarker.getPosition();

        // Move the map camera to the coords
        moveMapCamera(mMap, nearestLotCoords);

        // Show the marker title
        nearestLotMarker.showInfoWindow();
        return true;
    }

    // Locations API required logic for GPS. Tutorial used for getting current location: https://javapapers.com/android/get-current-location-in-android/
    private void getCurrentLocation() {

        // Checks if the permission is not granted, if it's not then evaluates to true
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        // Requests location updates. Second parameter determines how quickly the user's location is updated
        // The quicker the location is updated the more quickly the battery drains
        // Currently using 2500 which is the highest it can be without causing any bugs
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, this);
        // Makes user's current location visible
        mMap.setMyLocationEnabled(true);


        // Adds the locations circle filter feature (https://guides.codepath.com/android/Retrieving-Location-with-LocationServices-API)
        // TODO: Fix the circle filter
       /* getFusedLocationProviderClient(this.requireActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {

                //Log.d("TEST", "Value: " + enableCircleFilter);
                if (enableCircleFilter) {

                    mMap.setMyLocationEnabled(true);

                    Location location = locationResult.getLastLocation();

                    // Removes circles from previous locations
                    if (previousCircle != null) {
                        previousCircle.remove();
                    }
                    // Customizes circle appearance
                    CircleOptions circleOptions = new CircleOptions()
                            .center(new LatLng(location.getLatitude(), location.getLongitude()))
                            .radius(100)  // radius in meters
                            .fillColor(getResources().getColor(R.color.light_blue))
                            .strokeColor(Color.TRANSPARENT)
                            .strokeWidth(2);

                    // Stores current circle for removal upon next location update
                    previousCircle = mMap.addCircle(circleOptions);

                    // Filter markers that are within the circle
                    for (Marker marker : locations.keySet()) {
                        if (SphericalUtil.computeDistanceBetween(marker.getPosition(), previousCircle.getCenter()) <= previousCircle.getRadius()) {
                            marker.setVisible(true);
                        } else {
                            marker.setVisible(false);
                        }
                    }

                }
                else {

                    // Remove circle
                    if (previousCircle != null) {
                        previousCircle.remove();
                    }

                    // If the marker is already visible, keep it visible, if not, ensure it's not
                    for (Marker marker : MapsFragment.locations.keySet()) {
                        if(!marker.isVisible()){
                            marker.setVisible(false);
                        }
                    }

                    // Remove location display
                    mMap.setMyLocationEnabled(false);

                }

            }
            }, Looper.myLooper());*/


    }

    // Permission Request for GPS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    // Once the view is created, we can instantiate the map
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        this.view = view;

        if (mapFragment == null) return;

        mapFragment.getMapAsync(callback);
    }

    private void setMapStyle(@NonNull GoogleMap googleMap) {
         // Adds custom JSON file which uses AVC colors for Google Maps
         try {
             googleMap.setMapStyle(
                     MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.custom_avc_map));
         } catch(Resources.NotFoundException e){
             Log.e("JSON", "Can't find style. Error: ", e);
         }
    }

    private void setMapBounds(@NonNull GoogleMap googleMap) {
        LatLng southwestBound = new LatLng(34.674910, -118.192287); // 34.674910, -118.192287
        LatLng northeastBound = new LatLng(34.682133, -118.183807); //  34.682133, -118.183807

        //Set boundary for the map
        final LatLngBounds avcBounds = new LatLngBounds(southwestBound, northeastBound);
        googleMap.setLatLngBoundsForCameraTarget(avcBounds);
    }

    private void moveMapCamera(GoogleMap googleMap, LatLng coords) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coords)
                .zoom(INITIAL_ZOOM)
                .bearing(0)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void parseJson(GoogleMap googleMap) {
        // Open json file
        InputStream jsonData = getResources().openRawResource(R.raw.locations);
        Scanner scnr = new Scanner(jsonData);
        StringBuilder builder = new StringBuilder();

        // Build json string
        while (scnr.hasNextLine()) {
            builder.append(scnr.nextLine());
        }

        // Parse json into objects
        try {
            JSONArray root = new JSONArray(builder.toString());

            for (int i = 0; i < root.length(); i++) {
                JSONObject location = root.getJSONObject(i);
                String title = location.getString("title");
                double latitude = location.getDouble("latitude");
                double longitude = location.getDouble("longitude");
                String locationType = location.getString("type");

                Marker tmpMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title)
                        .icon(markerIcon)); // Initialize all markers with default blue icon

                // Create MapLocation object
                String description = location.getString("description");
                JSONArray locationImages = location.getJSONArray("images");

                // All markers are stored in the locations hashmap. This is required for displaying popups.
                locations.put(tmpMarker, new MapLocation(description, locationImages));

                // Location types are sorted into their respective ArrayLists
                if (locationType.equals("parking")) {
                    tmpMarker.setIcon(parkingMarkerIcon);
                    parkingLotMarkers.add(tmpMarker);
                }
                if(locationType.equals("classroom")){
                    classroomLocations.add(tmpMarker);
                }
                if(locationType.equals("resource")){
                    resourceLocations.add(tmpMarker);
                }
                if(locationType.equals("food")){
                    foodLocations.add(tmpMarker);
                }
                if(locationType.equals("athletic")){
                    athleticLocations.add(tmpMarker);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Calculate which lot is the nearest to the user's marker
    private Pair<Integer, Double> calculateNearestLot(Marker newUserMarker) {
        // The logic used for the code is as follows:
        // index[0] = lotA1, index[1] = lotA2, ... , index[17] = lotF2
        final int NUMBER_OF_PARKING_LOTS = 18;
        double[] lotDistancesToMarker = new double[NUMBER_OF_PARKING_LOTS];
        int nearestLotIndex = 0;
        double smallestDistance = Double.MAX_VALUE;

        // Calculates and stores the distance of each marker into the lotDistancesToMarker array
        // while calculating the smallest value in the array
        for (int i = 0; i < lotDistancesToMarker.length; i++) {
            lotDistancesToMarker[i] = calculateMarkerDistance(newUserMarker, parkingLotMarkers.get(i));

            if (lotDistancesToMarker[i] < smallestDistance) {
                smallestDistance = lotDistancesToMarker[i];
                nearestLotIndex = i;
            }
        }

        return new Pair<>(nearestLotIndex, smallestDistance);
    }

    /**
     * Calculates distance between two given markers; uses Locations.distanceBetween()
     *
     * @param marker1 - user's marker
     * @param marker2 - parking lot marker
     * @return The float value in meters of the distance between the two provided markers
     */
    private float calculateMarkerDistance(Marker marker1, Marker marker2){
        // The computed distance is stored in results[0]. (https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2)
        // If results has length 2 or greater, the initial bearing is stored in results[1].
        // If results has length 3 or greater, the final bearing is stored in results[2].
        float[] results = new float[1];

        Location.distanceBetween(marker1.getPosition().latitude, marker1.getPosition().longitude,
                marker2.getPosition().latitude, marker2.getPosition().longitude, results);
        return results[0];
    }

    // Gets the width of the screen of current device
    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // Gets the height of the screen of current device
    private static int getScreenHeight() {
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
