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

import java.util.ArrayList;
import java.util.HashMap;

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

        //ArrayList used for the logic of removing previously placed user marker
        ArrayList<Marker> userMarker = new ArrayList<>();

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


                //Markers for campus locations. TODO: Add markers to all significant locations
                //UH
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6787345742857, -118.18635845710243)).title("Uhazy Hall").icon(markerIcon)), "uh_popup");
                //YH
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67899187744454, -118.18548358738202)).title("Yoshida Hall").icon(markerIcon)), "yh_popup");
                //SUBWAY
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67918530937743, -118.18672504028271)).title("Subway Sandwich").icon(markerIcon)), "subway_popup");
                //SOAR
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67877310935158, -118.18800679457378)).title("SOAR (Students on the Academic Rise) High School").icon(markerIcon)), "soar_popup");
                //Gym
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67804292141216, -118.18734085519353)).title("Gymnasium").icon(markerIcon)), "gym_popup");
                //Stadium
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67634068573699, -118.19008554556989)).title("Marauder Stadium").icon(markerIcon)), "stadium_popup");
                //PAT
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6754613377245, -118.18723230937766)).title("Performing Arts Theatre").icon(markerIcon)), "pat_popup");
                //Art Gal
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67633186256671, -118.18678106433192)).title("Art Gallery").icon(markerIcon)), "gallery_popup");
                //Administration
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6755201268822, -118.18459238195355)).title("Administration Building").icon(markerIcon)), "admin_popup");
                //Admissions and Records
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67638480157416, -118.18531121391118)).title("Admissions and Records Office").icon(markerIcon)), "admissions_popup");
                //MH
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67687342944362, -118.18512883579885)).title("Mesquite Hall").icon(markerIcon)), "mh_popup");
                //LC
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67725682725537, -118.18531269317117)).title("Learning Center").icon(markerIcon)), "lc_popup");
                //Bookstore
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.676153144936336, -118.1860004428573)).title("Marauder Bookstore").icon(markerIcon)), "bookstore_popup");
                //library
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67705751700522, -118.18623111282814)).title("Library").icon(markerIcon)), "lib_popup");
                //LH
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67709130468547, -118.18756250227011)).title("Lecture Hall").icon(markerIcon)), "lh_popup");
                //ME
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67775573632852, -118.18589961736947)).title("Math and Engineering").icon(markerIcon)), "me_popup");
                //OSD
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67672471757968, -118.18439631632184)).title("Office for Students with Disabilities").icon(markerIcon)), "osd_popup");
                //FA
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67648676160919, -118.18738628333938)).title("Fine Arts").icon(markerIcon)), "fa_popup");
                //FAMO
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67626532974614, -118.18770778514867)).title("Fine Arts Music and Offices").icon(markerIcon)), "famo_popup");
                //TEAL
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67973186506707, -118.18654794631942)).title("Technical Education Technology").icon(markerIcon)), "tet_popup");
                //TEAFT
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.679898891625314, -118.1870825677824)).title("Technical Education: Agriculture Lab").icon(markerIcon)), "teal_popup");
                //GH
                locations.put(googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67988988181543, -118.18754492181769)).title("Greenhouse").icon(markerIcon)), "gh_popup");
                //Lot A1
                Marker lotA1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.681530052571766, -118.1873813729704)).title("Lot A1").icon(markerIcon));
                //Lot A2
                Marker lotA2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6815160851563, -118.18654051741896)).title("Lot A2").icon(markerIcon));
                //Lot A3
                Marker lotA3 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.68145251714801, -118.18554110652022)).title("Lot A3").icon(markerIcon));
                //Lot A4
                Marker lotA4 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.68137532735808, -118.18434291777308)).title("Lot A4").icon(markerIcon));
                //Lot B1
                Marker lotB1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.680290592554464, -118.18435262827403)).title("Lot B1").icon(markerIcon));
                //Lot B2
                Marker lotB2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67893436565732, -118.1843313226645)).title("Lot B2").icon(markerIcon));
                //Lot C1
                Marker lotC1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67795565478103, -118.18434219529921)).title("Lot C1").icon(markerIcon));
                //Lot C2
                Marker lotC2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67715814518791, -118.18433596797071)).title("Lot C2").icon(markerIcon));
                //Lot C3
                Marker lotC3 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67636064783748, -118.18422669590109)).title("Lot C3").icon(markerIcon));
                //Lot D1
                Marker lotD1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6753859837446, -118.18541367472605)).title("Lot D1").icon(markerIcon));
                //Lot D2
                Marker lotD2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67549627456488, -118.18617005769254)).title("Lot D2").icon(markerIcon));
                //Lot E1
                Marker lotE1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67581345503335, -118.18892817158141)).title("Lot E1").icon(markerIcon));
                //Lot E2
                Marker lotE2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6769166812582, -118.18915190113239)).title("Lot E2").icon(markerIcon));
                //Lot E3
                Marker lotE3 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67675828726229, -118.18841680858253)).title("Lot E3").icon(markerIcon));
                //Lot E4
                Marker lotE4 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67751746225447, -118.18911823651975)).title("Lot E4").icon(markerIcon));
                //Lot E5
                Marker lotE5 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67919774054158, -118.18916950001093)).title("Lot E5").icon(markerIcon));
                //Lot F1
                Marker lotF1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67613026710341, -118.19203306356845)).title("Lot F1").icon(markerIcon));
                //Lot F2
                Marker lotF2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67793654636213, -118.19232252356142)).title("Lot F2").icon(markerIcon));

                locations.put(lotA1, "lot_a1_popup");
                locations.put(lotA2, "lot_a2_popup");
                locations.put(lotA3, "lot_a3_popup");
                locations.put(lotA4, "lot_a4_popup");
                locations.put(lotB1, "lot_b1_popup");
                locations.put(lotB2, "lot_b2_popup");
                locations.put(lotC1, "lot_c1_popup");
                locations.put(lotC2, "lot_c2_popup");
                locations.put(lotC3, "lot_c3_popup");
                locations.put(lotD1, "lot_d1_popup");
                locations.put(lotD2, "lot_d2_popup");
                locations.put(lotE1, "lot_e1_popup");
                locations.put(lotE2, "lot_e2_popup");
                locations.put(lotE3, "lot_e3_popup");
                locations.put(lotE4, "lot_e4_popup");
                locations.put(lotE5, "lot_e5_popup");
                locations.put(lotF1, "lot_f1_popup");
                locations.put(lotF2, "lot_f2_popup");

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

                        // Adds all parking lot markers to an ArrayList
                        // NOTE: The logic for the code below is as follows: index 0 = lotA1, index 1 = lotA2, ..., index 17 = lotF2
                        // TODO: Maybe consider adding all locations to an ArrayList to clean up code?
                        ArrayList<Marker> parkingLotMarkers = new ArrayList<>();
                        parkingLotMarkers.add(lotA1);
                        parkingLotMarkers.add(lotA2);
                        parkingLotMarkers.add(lotA3);
                        parkingLotMarkers.add(lotA4);
                        parkingLotMarkers.add(lotB1);
                        parkingLotMarkers.add(lotB2);
                        parkingLotMarkers.add(lotC1);
                        parkingLotMarkers.add(lotC2);
                        parkingLotMarkers.add(lotC3);
                        parkingLotMarkers.add(lotD1);
                        parkingLotMarkers.add(lotD2);
                        parkingLotMarkers.add(lotE1);
                        parkingLotMarkers.add(lotE2);
                        parkingLotMarkers.add(lotE3);
                        parkingLotMarkers.add(lotE4);
                        parkingLotMarkers.add(lotE5);
                        parkingLotMarkers.add(lotF1);
                        parkingLotMarkers.add(lotF2);

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
        //If results has length 2 or greater, the initial bearing is stored in results[1].
        //If results has length 3 or greater, the final bearing is stored in results[2].
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
        //tyler was here
        //tyler is trying his best
    }
}