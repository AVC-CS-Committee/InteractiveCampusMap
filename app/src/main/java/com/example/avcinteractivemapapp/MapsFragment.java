package com.example.avcinteractivemapapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

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

/*TODO: (FOR THOSE WORKING ON THE GOOGLE MAPS API)
        Tutorial being followed: https://youtu.be/lBW58tPLn-A?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi
        -Restrict API Key; get done before deployment
        -Polish Google Services, GPS, and location permissions
        -Add custom markers to each location (buildings, parking lots, stadium, etc.)
        -Set boundaries (DONE)
        -On marker click open a popup menu with info about that location
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

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //Set boundary for the map
                final LatLngBounds avcBounds = new LatLngBounds(new LatLng(34.674910, -118.192287), new LatLng(34.682133, -118.183807));
                googleMap.setLatLngBoundsForCameraTarget(avcBounds);

                //Focus AVC and place default marker (uses custom marker)
                LatLng avc = new LatLng(34.6773, -118.1866);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avc, 17.5f));
                googleMap.addMarker(new MarkerOptions().position(avc).title("Antelope Valley College").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));

                //Markers for campus locations. TODO: Add markers to all significant locations
                //UH
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6787345742857, -118.18635845710243)).title("Uhazy Hall").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //YH
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67899187744454, -118.18548358738202)).title("Yoshida Hall").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //SUBWAY
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67918530937743, -118.18672504028271)).title("Subway Sandwich").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //SOAR
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67877310935158, -118.18800679457378)).title("SOAR (Students on the Academic Rise) High School").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Gym
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67804292141216, -118.18734085519353)).title("Gymnasium").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Stadium
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67634068573699, -118.19008554556989)).title("Marauder Stadium").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //PAT
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.675599536158934, -118.18722094657464)).title("Performing Arts Theatre").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Art Gal
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67633186256671, -118.18678106433192)).title("Art Gallery").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Administration
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.6755201268822, -118.18459238195355)).title("Administration Building").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Admissions and Records
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67638480157416, -118.18531121391118)).title("Admissions and Records Office").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //MH
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67687342944362, -118.18512883579885)).title("Mesquite Hall").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //LC
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67725682725537, -118.18531269317117)).title("Learning Center").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //Bookstore
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.676153144936336, -118.1860004428573)).title("Marauder Bookstore").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //library
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67705751700522, -118.18623111282814)).title("Library").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //LH
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67709130468547, -118.18756250227011)).title("Lecture Hall").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //ME
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67775573632852, -118.18589961736947)).title("Math and Engineering").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //OSD
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67672471757968, -118.18439631632184)).title("Office for Students with Disabilities").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //FA
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67648676160919, -118.18738628333938)).title("Fine Arts").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //FAMO
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67626532974614, -118.18770778514867)).title("Fine Arts Music and Offices").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //TEAL
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.679898891625314, -118.1870825677824)).title("Technical Education: Agriculture Lab").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                //GH
                googleMap.addMarker(new MarkerOptions().position(new LatLng(34.67988988181543, -118.18754492181769)).title("Greenhouse").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
            /*    //
                googleMap.addMarker(new MarkerOptions().position(new LatLng()).title("").icon(BitmapFromVector(getActivity(), R.drawable.marker_icon)));
                 */
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @SuppressLint("InflateParams")
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        String clickedMarker = marker.getTitle();

                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View popupView;

                        /*
                        //NOTE: The code used in the "Uhazy Hall" elseif statement is the same code used for all popups
                        //TODO: Add an elseif condition for each marker and create a popup for that location
                        if(clickedMarker.equals("Antelope Valley College")){
                            //Center of campus marker (should this have a popup??)
                          }
                        else if(clickedMarker.equals("Uhazy Hall")){
                            //Instantiates the corresponding location's xml file
                            popupView = inflater.inflate(R.layout.uh_popup, null);

                            //Creates the popup for that location
                            popupViewCreator(popupView, view);

                        }*/
                        //Depending on which marker is clicked, a popup view of the corresponding location is opened.
                        // A switch statement to check the name of the marker clicked. Add new case with marker name for future additions.
                        // Create a new popup layout in re\layout folder for each new location.
                        if (clickedMarker != null) {
                            switch(clickedMarker){
                                case "Antelope Valley College":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.avc_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Yoshida Hall":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.yh_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Tech Ed: Welding/Fire Technology":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.tewft_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Uhazy Hall":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.uh_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Subway Sandwich":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.subway_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "SOAR (Students on the Academic Rise) High School":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.soar_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Gymnasium":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.gym_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Marauder Stadium":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.stadium_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Performing Arts Theatre":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.pat_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Art Gallery":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.gallery_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Administration Building":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.admin_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Admissions and Records Office":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.admissions_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Mesquite Hall":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.mh_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Learning Center":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.lc_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Marauder Bookstore":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.bookstore_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Library":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.lib_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Lecture Hall":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.lh_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Math and Engineering":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.me_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Office for Students with Disabilities":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.osd_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Fine Arts":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.fa_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Fine Arts Music and Offices":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.famo_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Technical Education: Agriculture Lab":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.teal_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                                case "Greenhouse":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout.gh_popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                            /*    case "":
                                    //Instantiates the corresponding location's xml file
                                    popupView = inflater.inflate(R.layout._popup, null);

                                    //Creates the popup for that location
                                    popupViewCreator(popupView, view);
                                    break;
                            */
                                default:
                                    break;
                            }
                        }

                        return false;
                    }
                });


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