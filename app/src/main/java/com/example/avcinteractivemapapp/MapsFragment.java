package com.example.avcinteractivemapapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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
    final float MAX_ZOOM = 14.0f;
    final float INITIAL_ZOOM = 17.5f;

    // Marker Lists
    ArrayList<Marker> userMarker = new ArrayList<>();
    public static ArrayList<Marker> parkingLotMarkers = new ArrayList<>();
    public static ArrayList<Marker> classroomLocations = new ArrayList<>();
    public static ArrayList<Marker> foodLocations = new ArrayList<>();
    public static ArrayList<Marker> athleticLocations = new ArrayList<>();
    public static ArrayList<Marker> resourceLocations = new ArrayList<>();

    // HashMap used to lookup a location's xml file
    public static HashMap<Marker, String> locations = new HashMap<>();

    // Locations API Related (GPS Feature)
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;

    // Icons for markers
    BitmapDescriptor markerIcon;
    ImageButton centerMapButton;
    View view;

    // Handles map manipulation once the map is ready
    // Replaces onMapReady()
    private final OnMapReadyCallback callback = googleMap -> {
        setMapStyle(googleMap);
        setMapBounds(googleMap);
        centerMapCamera(googleMap);

        mMap = googleMap;

        markerIcon = BitmapFromVector(getActivity(), R.drawable.marker_icon);
        centerMapButton = view.findViewById(R.id.center_map);

        parseJson(googleMap);

        // googleMap.setOnMarkerClickListener();

        // Handles marker title clicks
        googleMap.setOnInfoWindowClickListener(marker -> {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView;

            // Depending on which marker is clicked, a popup view of the corresponding location is opened.
            // A HashMap is used to check the name of the marker clicked.

            String popup = locations.get(marker);
            if (popup == null) return;

            // resId stores the id of the corresponding xml file
            int resId = getResources().getIdentifier(popup, "layout", getContext().getPackageName());

            popupView = inflater.inflate(resId, null);
            popupViewCreator(popupView, view);
        });

        // Handles map clicks
        googleMap.setOnMapClickListener(latLng -> {
            if (userMarker.size() > 0) {
                // Removes existing marker from the map
                userMarker.get(0).remove();

                // Removes existing Marker object from ArrayList
                userMarker.remove(0);
            }

            // Creates a new Marker object and places it at the location
            Marker newUserMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("User Marker"));

            // Add user Marker to the ArrayList
            userMarker.add(newUserMarker);

            // Calculate the distance to the nearest lot
            Pair<Integer, Double> nearestLot = calculateNearestLot(newUserMarker);

            // Temporary code (just being used to display that the parking calculator actually works)
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View userMarkerView = inflater.inflate(R.layout.user_marker_popup, null);
            TextView nearestLotView = userMarkerView.findViewById(R.id.userMarkerPopup).findViewById(R.id.lotView);
            TextView distanceView = userMarkerView.findViewById(R.id.userMarkerPopup).findViewById(R.id.distanceView);
            nearestLotView.setText(parkingLotMarkers.get(nearestLot.first).getTitle());
            distanceView.setText(String.format("%.2f %s", nearestLot.second, distanceView.getText()));
            popupViewCreator(userMarkerView, view);
        });

        // Handle map camera movement
        googleMap.setOnCameraMoveListener(() -> {
            CameraPosition position = googleMap.getCameraPosition();

            // Ensures that the user doesn't go over the max zoom amount
            if (position.zoom > MAX_ZOOM) googleMap.setMinZoomPreference(MAX_ZOOM);
        });

        // Handles center map button clicks
        centerMapButton.setOnClickListener(view -> centerMapCamera(googleMap));

        // GPS Related
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());
        getCurrentLocation();

        // The uiSettings object removes default Google Maps hover buttons
        UiSettings uiSettings = googleMap.getUiSettings();
        // Removing the "Directions" and "Open in Maps" buttons
        uiSettings.setMapToolbarEnabled(false);

    };

    public static void findNearestMarkersToUser() {
        // 1) Determine user's current location
        // 2) Pass that info. to the nearest location calculator
        // 3) The nearest location calculator checks user's location to all other locations, determines
        //    which are closest based on a predetermined radius around a user
    }

    // Locations API required logic for GPS. Tutorial used: https://youtu.be/cnlSyYeRqrs
    private void getCurrentLocation() {

        // Checks if the permission is not granted, if it's not then evaluates to true
        if(ActivityCompat.checkSelfPermission(
                this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null) {

                    currentLocation = location;
                   /* SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.google_map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsFragment);*/

                }

            }
        });

        // intervalMillis sets how quickly the user's location is updated in milliseconds
        // IMPORTANT: The the lower the interval the faster the user's phone battery drains, but the faster the location is updated.
        LocationRequest.Builder mLocationRequest = new LocationRequest.Builder(60000);
        mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Toast.makeText(getApplicationContext()," location result is  " + locationResult, Toast.LENGTH_LONG).show();

                if (locationResult == null) {
                    //Toast.makeText(getApplicationContext(),"current location is null ", Toast.LENGTH_LONG).show();

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //Toast.makeText(getApplicationContext(),"current location is " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        //TODO: UI updates.
                    }
                }
            }
        };

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

    private void centerMapCamera(@NonNull GoogleMap googleMap) {
        LatLng avcCoords = new LatLng(34.6773, -118.1866);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(avcCoords)
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
                String xmlFile = location.getString("xml_file");
                String locationType = location.getString("type");

                Marker tmpMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title)
                        .icon(markerIcon));

                // All markers are stored in the locations hashmap. This is required for displaying popups.
                locations.put(tmpMarker, xmlFile);

                // Location types are sorted into their respective ArrayLists
                if (locationType.equals("parking")) {
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
