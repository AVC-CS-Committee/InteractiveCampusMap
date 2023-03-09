package com.example.avcinteractivemapapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import pub.devrel.easypermissions.EasyPermissions;

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
    // 17f is the max zoom before render issues occur
    final float MAX_ZOOM = 17f;
    // Initial zoom must be larger (more zoomed in) than max to prevent the max zoom from breaking
    final float INITIAL_ZOOM = 17.001f;
    final LatLng AVC_COORDS = new LatLng(34.678652329599096, -118.18616290156892);
    final LatLng SOUTHWEST_BOUND = new LatLng(34.674910, -118.192287);
    final LatLng NORTHEAST_BOUND = new LatLng(34.682133, -118.183807);
    final LatLngBounds AVC_BOUNDS = new LatLngBounds(SOUTHWEST_BOUND, NORTHEAST_BOUND);


    MainActivity mainActivity;


    // Marker Lists
    private final ArrayList<Marker> userLocationMarkers = new ArrayList<>();
    private final ArrayList<Marker> parkingLotMarkers = new ArrayList<>();
    private final ArrayList<Marker> classroomLocations = new ArrayList<>();
    private final ArrayList<Marker> foodLocations = new ArrayList<>();
    private final ArrayList<Marker> athleticLocations = new ArrayList<>();
    private final ArrayList<Marker> resourceLocations = new ArrayList<>();

    // Booleans for determining the visibility of markers
    public boolean showParkingLots = false;
    public boolean showClassrooms = false;
    public boolean showStudentResources = false;
    public boolean showFood = false;
    public boolean showAthletics = false;

    // HashMap used to lookup a location's MapLocation object
    public HashMap<Marker, MapLocation> locations = new HashMap<>();

    // Locations API Related (GPS Feature)
    private LocationManager locationManager;
    private Location userLocation;
    private double currentLat;
    private double currentLong;
    public GoogleMap mMap;

    // GPS Related
    private Circle previousCircle;
    public static boolean enableCircleFilter = false;

    // Icons for markers
    BitmapDescriptor parkingMarkerIcon, classroomMarkerIcon, foodMarkerIcon, athleticsMarkerIcon, resourceMarkerIcon;
    // Temporary
    BitmapDescriptor markerIcon;
    ImageButton centerMapButton;
    ImageButton centerUserButton;
    View view;
    SearchView searchView;

    // Handles map manipulation once the map is ready
    // Replaces onMapReady()
    @SuppressLint("ClickableViewAccessibility")
    private final OnMapReadyCallback callback = googleMap -> {
        setMapStyle(googleMap);
        setMapBounds(googleMap);
        moveMapCamera(googleMap, AVC_COORDS);

        mainActivity = (MainActivity) requireActivity();

        mMap = googleMap;

        markerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker);
        parkingMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_parking);
        classroomMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_classroom);
        foodMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_food);
        resourceMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_resources);
        athleticsMarkerIcon = BitmapFromVector(getActivity(), R.drawable.icon_marker_athletics);
        centerMapButton = requireActivity().findViewById(R.id.center_map);
        centerUserButton = requireActivity().findViewById(R.id.centerUserButton);
       // searchView = view.findViewById(R.id.searchView);

        parseJson(googleMap);


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

        // TODO: Setup Custom Info Windows for Markers
        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getActivity());
        mMap.setInfoWindowAdapter(infoWindowAdapter);

        // Set the search view to be visible
        searchView.setVisibility(View.VISIBLE);
        searchView.setQueryHint("Search Locations");
        searchView.clearFocus();
        // adding on query listener for our search view.
        searchView.setOnQueryTextListener(new SearchBar(locations, mMap));

        // Handles map clicks (was used for the old version of the nearest lot calculator)
        googleMap.setOnMapClickListener(latLng -> {});

        // Handle map camera movement
        googleMap.setOnCameraMoveListener(() -> {
            CameraPosition position = googleMap.getCameraPosition();

            // Ensures that the user doesn't go over the max zoom amount
            if (position.zoom > MAX_ZOOM) googleMap.setMinZoomPreference(MAX_ZOOM);

            SearchBar.hideKeyboard(searchView, requireActivity());

        });

        // TODO: Hide keyboard on marker click
        mMap.setOnMarkerClickListener(marker -> {
            SearchBar.hideKeyboard(searchView, requireActivity());
            return false;
        });

        // Handles center map button clicks
        centerMapButton.setOnClickListener(view -> moveMapCamera(googleMap, AVC_COORDS));

        centerMapButton.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundResource(R.drawable.icon_center_map_pressed);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundResource(R.drawable.icon_center_map);
            }
            return false;

        });

        centerUserButton.setOnClickListener(v -> {
            if (mainActivity.hasLocationPermission() && getCurrentLocation() != null) {
                moveMapCamera(googleMap, new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            }
            else {
                // Not displaying
                EasyPermissions.requestPermissions(this, "Location services is disabled. Some features may not work properly", MainActivity.RC_PERMISSIONS, MainActivity.REQUIRED_PERMISSIONS);
            }
        });

        centerUserButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundResource(R.drawable.icon_center_user_pressed);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundResource(R.drawable.icon_center_user);
            }
            return false;
        });

        // GPS Related
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // The uiSettings object removes default Google Maps hover buttons
        UiSettings uiSettings = googleMap.getUiSettings();
        // Removing the "Directions" and "Open in Maps" buttons
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
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
        userLocation = location;
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

        // For circle filter. If active and user is in bounds, create the circle
        // Circle is created here in order to remove the previously created circle more quickly.
        // Without this, the circle will not update properly
        if (enableCircleFilter && AVC_BOUNDS.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
            createCircle(location);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Try-catch purpose: Should be catching an exception on app re-entry where MainActivity is not yet visible
        //                    Should only be catching this exception once for every re-entry
        // Overall purpose: Disable all GPS related features on resuming MapsFragment (i.e., app was minimized/navigated away from)
        try {
            // Disable all GPS related features
            disableCircleFilter();
        } catch (NullPointerException e) {
            // Expected Exception
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        mMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);

        // Stop displaying location
        mMap.setMyLocationEnabled(false);

        // Set location to null
        userLocation = null;

        // Turn off all GPS related features when location is disabled
        if(enableCircleFilter){
            disableCircleFilter();
        }

    }

    public boolean enableCircleFilter() {
        if (getCurrentLocation() != null) {
            enableCircleFilter = !enableCircleFilter;
            circleFilterHandler();

        }
        return enableCircleFilter;
    }

    public void disableCircleFilter(){
        enableCircleFilter = false;
        getCurrentLocation();
        circleFilterHandler();
        mainActivity.toggleOffCircleFilterSwitch();

    }

    // TODO: Disable nearest lot calculator when circle filter is active
    public void enableParkingCalculator() {
        // Update the current user's location
        getCurrentLocation();

        // Check if the current location exists. If it doesn't, return false
        if (userLocation == null) return;

        // Convert current user's location into a marker
        Marker userMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLat, currentLong)));

        // Calculate the nearest lot to the user then grab the lot marker
        Pair<Integer, Double> nearestLot = calculateNearestLot(userMarker);
        Marker nearestLotMarker = parkingLotMarkers.get(nearestLot.first);

        // Remove the temporary marker
        if (userMarker != null) userMarker.remove();

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
    }

    // Creates circle around user and contains logic for circle filter
    private void createCircle(Location location) {

        // Removes circles from previous locations
        if (previousCircle != null) {
            previousCircle.remove();
        }

        // Customizes circle appearance
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(100)  // radius in meters
                .fillColor(getResources().getColor(R.color.circle_filter_blue))
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

    private void circleFilterHandler() {
        // Handle turning off the circle filter
        if (!enableCircleFilter) {
            // Remove circle
            if (previousCircle != null) {
                previousCircle.remove();
            }

            // If the marker is already visible, keep it visible, if not, ensure it's not
            // This logic is needed to "save" marker states whenever this activity is resumed
            // (i.e., filtering a location, entering a location description, and returning to
            // the fragment activity will have the filtered marker states "saved")
            for (Marker marker : locations.keySet()) {
                if(!marker.isVisible()){
                    marker.setVisible(false);
                }
            }

            return;
        }

        // Get the last known location from the network provider
        LatLng currentUserCoords = new LatLng(currentLat, currentLong);

        // Checks if user is not within map bounds. If they aren't, show toast and return.
        if (!AVC_BOUNDS.contains(currentUserCoords)) {
            Toast.makeText(this.getActivity(), "Unavailable. You are not at Antelope Valley College!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Toggle switch UI on
        mainActivity.toggleOnCircleFilterSwitch();
        // Disables markers outside of the circle
        mainActivity.disableNonCircleFilterMarkers();

        // Not required to call createCircle() again here, however, it helps with displaying
        // the circle more quickly
        createCircle(userLocation);

        // Center on user
        moveMapCamera(mMap, new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
    }

    // TODO: figure out a way to not use SuppressLint
    @SuppressLint("MissingPermission")
    private Location getCurrentLocation() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Location Services");
                builder.setMessage("Enable location services to use certain features of this app.");
                builder.setPositiveButton("OK", (dialog, which) -> {});

                // Create and show the alert dialog
                builder.create().show();
                return null;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, this);

            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (userLocation == null) return null;

            currentLat = userLocation.getLatitude();
            currentLong = userLocation.getLongitude();

            mMap.setMyLocationEnabled(true);

            return userLocation;
        }
        return null;
    }

    // Permission Request for GPS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

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

        searchView = requireActivity().findViewById(R.id.searchView);
        mapFragment.getMapAsync(callback);
    }

    private void setMapStyle(@NonNull GoogleMap googleMap) {
         // Adds custom JSON file which uses AVC colors for Google Maps
         try {
             googleMap.setMapStyle(
                     MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.custom_avc_map));
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

                if (tmpMarker == null) return;

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
        if (vectorDrawable == null) return null;
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
