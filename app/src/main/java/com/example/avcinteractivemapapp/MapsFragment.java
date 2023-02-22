package com.example.avcinteractivemapapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    final LatLng SOUTHWEST_BOUND = new LatLng(34.674910, -118.192287);
    final LatLng NORTHEAST_BOUND = new LatLng(34.682133, -118.183807);
    final LatLngBounds AVC_BOUNDS = new LatLngBounds(SOUTHWEST_BOUND, NORTHEAST_BOUND);


    MainActivity mainActivity;


    // Marker Lists
    private ArrayList<Marker> userLocationMarkers = new ArrayList<>();
    private ArrayList<Marker> parkingLotMarkers = new ArrayList<>();
    private ArrayList<Marker> classroomLocations = new ArrayList<>();
    private ArrayList<Marker> foodLocations = new ArrayList<>();
    private ArrayList<Marker> athleticLocations = new ArrayList<>();
    private ArrayList<Marker> resourceLocations = new ArrayList<>();

    // Booleans for determining the visibility of markers
    public boolean showParkingLots = false;
    public boolean showClassrooms = false;
    public boolean showStudentResources = false;
    public boolean showFood = false;
    public boolean showAthletics = false;

    // HashMap used to lookup a location's MapLocation object
    public HashMap<Marker, MapLocation> locations = new HashMap<>();

    // Locations API Related (GPS Feature)
    LocationManager locationManager;
    double currentLat;
    double currentLong;
    private final int REQUEST_CODE = 101;
    public GoogleMap mMap;

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
    SearchView searchView;

    // Handles map manipulation once the map is ready
    // Replaces onMapReady()
    private final OnMapReadyCallback callback = googleMap -> {
        setMapStyle(googleMap);
        setMapBounds(googleMap);
        moveMapCamera(googleMap, AVC_COORDS);

        mainActivity = (MainActivity) getActivity();

        mMap = googleMap;

        markerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker);
        parkingMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_parking);
        classroomMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_classroom);
        foodMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_food);
        resourceMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_resources);
        athleticsMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_athletics);
        centerMapButton = view.findViewById(R.id.center_map);
        searchView = view.findViewById(R.id.searchView);

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
/*
        // Set the search view to be visible
        searchView.setVisibility(View.VISIBLE);
        searchView.setQueryHint("Search Locations");
        searchView.clearFocus();
        // adding on query listener for our search view.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();


                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {

                    // TESTING
                    if(location.equals("Uhazy")){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(classroomLocations.get(1).getPosition(), 20));
                        classroomLocations.get(1).showInfoWindow();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
*/
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

    // Filter the markers based on the boolean values of each marker type's visibility
    public void filterMarkers() {
        // If all the filters are unchecked, we want to show all the markers
        if (isFiltersDisabled()) {
            showAllMarkers();
            return;
        }

        for (Marker marker : parkingLotMarkers) marker.setVisible(showParkingLots);
        for (Marker marker : classroomLocations) marker.setVisible(showClassrooms);
        for (Marker marker : resourceLocations) marker.setVisible(showStudentResources);
        for (Marker marker : foodLocations) marker.setVisible(showFood);
        for (Marker marker : athleticLocations) marker.setVisible(showAthletics);
        for (Marker marker : userLocationMarkers) marker.setVisible(true);
    }

    // Helper method that toggles all markers to be visible
    public void showAllMarkers() {
        for (Marker marker : locations.keySet()) marker.setVisible(true);
    }

    // Toggles all markers to be invisible
    public void disableAllMarkers() {
        for (Marker marker : locations.keySet()) marker.setVisible(false);
    }

    // Show specific markers
    public void showSpecificMarkers(ArrayList<Marker> markers) {
        for(Marker marker : markers) {
            marker.setVisible(true);
        }
    }

    // Helper method that checks if all the filters are set to false (unchecked)
    private boolean isFiltersDisabled() {
        if (showParkingLots) return false;
        if (showClassrooms) return false;
        if (showStudentResources) return false;
        if (showFood) return false;
        return !showAthletics;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        // Set the current lat and long
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

        // For circle filter. If active and user is in bounds, create the circle
        // Circle is created here in order to remove the previously created circle more quickly.
        // Without this, the circle will not update properly
        if(enableCircleFilter && AVC_BOUNDS.contains(new LatLng(location.getLatitude(), location.getLongitude()))){
            createCircle(location);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public boolean enableCircleFilter() {

        enableCircleFilter = !enableCircleFilter;
        getCurrentLocation();
        return enableCircleFilter;
    }



    public void disableCircleFilter(){
        enableCircleFilter = false;
        getCurrentLocation();
        mainActivity.toggleOffCircleFilterSwitch();

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

        // Check if the marker is visible, if it isn't, make it visible
        if (!nearestLotMarker.isVisible() && userLocationMarkers.size() == 0) {
            userLocationMarkers.add(nearestLotMarker);
            nearestLotMarker.setVisible(true);
        }
        else if(userLocationMarkers.size() >= 1 ){
            if(showParkingLots) {
                userLocationMarkers.clear();
                userLocationMarkers.add(nearestLotMarker);
            }
            else{
                userLocationMarkers.get(0).setVisible(false);
                userLocationMarkers.clear();
                userLocationMarkers.add(nearestLotMarker);
                nearestLotMarker.setVisible(true);
            }
        }

        // Show the marker title
        nearestLotMarker.showInfoWindow();
        return true;
    }

    // Creates circle around user and contains logic for circle filter
    private void createCircle(Location location){

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
                // WARNING: Might also have to account for different location types being in the circle at one time. Might cause issues
                //          with visibility of markers. (Works fine so far.)
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
                if(parkingLotMarkers.contains(marker)){
                    showParkingLots = false;
                }
                if(classroomLocations.contains(marker)){
                    showClassrooms = false;
                }
                if(foodLocations.contains(marker)){
                    showFood = false;
                }
                if(athleticLocations.contains(marker)){
                    showAthletics = false;
                }
                if(resourceLocations.contains(marker)){
                    showStudentResources = false;
                }
            }
        }

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
        if (enableCircleFilter) {


            // Get the last known location from the network provider
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            LatLng currentUserCoords = new LatLng(location.getLatitude(), location.getLongitude());

            // Checks if user is within map bounds. If false, feature is not enabled
            if(AVC_BOUNDS.contains(currentUserCoords)) {

                // Toggle switch UI on
                mainActivity.toggleOnCircleFilterSwitch();
                // Disables markers outside of the circle
                mainActivity.disableNonCircleFilterMarkers();

                // Not required to call createCircle() again here, however, it helps with displaying
                // the circle more quickly
                createCircle(location);

                // Center on user
                moveMapCamera(mMap, new LatLng(location.getLatitude(), location.getLongitude()));

                mMap.setMyLocationEnabled(true);

            }
            else{

                Toast.makeText(this.getActivity(), "Unavailable. You are not at Antelope Valley College!", Toast.LENGTH_SHORT).show();

            }

        }
        else {

            // Remove circle
            if (previousCircle != null) {
                previousCircle.remove();
            }

            // If the marker is already visible, keep it visible, if not, ensure it's not
            for (Marker marker : locations.keySet()) {
                if(!marker.isVisible()){
                    marker.setVisible(false);
                }
            }


        }



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

        //Set boundary for the map
        googleMap.setLatLngBoundsForCameraTarget(AVC_BOUNDS);
    }

    public void moveMapCamera(GoogleMap googleMap, LatLng coords) {
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
                // Get all JSON fields
                JSONObject location = root.getJSONObject(i);
                String title = location.getString("title");
                LatLng coords = new LatLng(location.getDouble("latitude"),
                        location.getDouble("longitude"));
                String locationType = location.getString("type");
                String description = location.getString("description");
                JSONArray locationImages = location.getJSONArray("images");

                Marker tmpMarker = googleMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .title(title)
                        .icon(markerIcon)); // Initialize all markers with default blue icon

                // All markers are stored in the locations hashmap with a MapLocation object.
                locations.put(tmpMarker, new MapLocation(title, description, coords, locationImages));

                // Location types are sorted into their respective ArrayLists
                if (locationType.equals("parking")) {
                    tmpMarker.setIcon(parkingMarkerIcon);
                    parkingLotMarkers.add(tmpMarker);
                }
                if(locationType.equals("classroom")){
                    tmpMarker.setIcon(classroomMarkerIcon);
                    classroomLocations.add(tmpMarker);
                }
                if(locationType.equals("resource")){
                    tmpMarker.setIcon(resourceMarkerIcon);
                    resourceLocations.add(tmpMarker);
                }
                if(locationType.equals("food")){
                    tmpMarker.setIcon(foodMarkerIcon);
                    foodLocations.add(tmpMarker);
                }
                if(locationType.equals("athletic")){
                    tmpMarker.setIcon(athleticsMarkerIcon);
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
