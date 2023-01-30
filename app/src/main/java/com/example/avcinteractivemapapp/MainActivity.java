package com.example.avcinteractivemapapp;

import static android.content.ContentValues.TAG;

import static com.example.avcinteractivemapapp.Constants.ERROR_DIALOG_REQUEST;
import static com.example.avcinteractivemapapp.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.avcinteractivemapapp.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DESCRIPTION:
 *  This class is where the entire program starts, more specifically
 *  in the onCreate() function which sort of acts as the main method.
 */
//yo
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;

    //For determining whether or not user grants permission for location services
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Telling app to use custom toolbar as actionbar replacement
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        //Grabbing custom drawer layout from activity_main
        drawer = findViewById(R.id.drawer_layout);

        // Creating hamburger button and rotating animation when clicked
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Opens HelpActivity when help button is clicked
        MenuItem helpButton = nav.getMenu().findItem(R.id.nav_help);
        helpButton.setOnMenuItemClickListener(item -> {
            openHelpActivity();
            return true;
        });

        // Initiates method to determine the nearest markers based on the user's current location
       /* MenuItem locationsNearUserButton = nav.getMenu().findItem(R.id.nav_locations_near_me);
        locationsNearUserButton.setOnMenuItemClickListener(item -> {
            MapsFragment.findNearestMarkersToUser();
            return true;
        });*/

        // Closes toolbar when map button is clicked
        MenuItem mapButton = nav.getMenu().findItem(R.id.nav_map);
        mapButton.setOnMenuItemClickListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        //A new fragment object is created to reference the MapsFragment.java class
        Fragment fragment = new MapsFragment();

        //Loads the Google Maps fragment to display the map
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();
    }

    /*
        Called whenever this activity is about to go into the background or not be visible by the user.
        Good place to save unsaved data, perform cleanup operations, etc.
        Overriding this method allows us to create custom behaviors before the activity is no longer the priority.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Resets the state for all location markers
        HashMap<Marker, String> locations = MapsFragment.locations;
        showAllMarkers(locations);

    }

    // Logic for marker filtering
    void hideMarkerType(ArrayList<Marker> markerType) {
        for (Marker marker : markerType) marker.setVisible(false);
    }

    void showMarkerType(ArrayList<Marker> markerType) {
        for (Marker marker : markerType) marker.setVisible(true);
    }

    void showAllMarkers(Map<Marker, String> locations){
        for (Map.Entry<Marker, String> entry : locations.entrySet()) {
            entry.getKey().setVisible(true);
        }
    }

    // Whenever an item is selected in the Map Legend
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ArrayList<Marker> lotMarkers = MapsFragment.parkingLotMarkers;
        ArrayList<Marker> classMarkers = MapsFragment.classroomLocations;
        ArrayList<Marker> resourceMarkers = MapsFragment.resourceLocations;
        ArrayList<Marker> foodMarkers = MapsFragment.foodLocations;
        ArrayList<Marker> athleticMarkers = MapsFragment.athleticLocations;

        switch (item.getItemId()) {
            case R.id.lots:
                // Ensures the lot markers display. Accounts for the case where they're already disabled
                showMarkerType(lotMarkers);

                // Sets checked state of checkbox
                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    hideMarkerType(classMarkers);
                    hideMarkerType(resourceMarkers);
                    hideMarkerType(foodMarkers);
                    hideMarkerType(athleticMarkers);

                    // Close the legend view
                    drawer.closeDrawer(GravityCompat.START);

                }
                else {
                    showMarkerType(classMarkers);
                    showMarkerType(resourceMarkers);
                    showMarkerType(foodMarkers);
                    showMarkerType(athleticMarkers);
                }
                return true;
            case R.id.classrooms:
                showMarkerType(classMarkers);

                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    hideMarkerType(lotMarkers);
                    hideMarkerType(resourceMarkers);
                    hideMarkerType(foodMarkers);
                    hideMarkerType(athleticMarkers);

                    // Close the legend view
                    drawer.closeDrawer(GravityCompat.START);

                }
                else {
                    showMarkerType(lotMarkers);
                    showMarkerType(resourceMarkers);
                    showMarkerType(foodMarkers);
                    showMarkerType(athleticMarkers);
                }

                return true;
            case R.id.studentResources:
                showMarkerType(resourceMarkers);

                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    hideMarkerType(lotMarkers);
                    hideMarkerType(classMarkers);
                    hideMarkerType(foodMarkers);
                    hideMarkerType(athleticMarkers);

                    // Close the legend view
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    showMarkerType(lotMarkers);
                    showMarkerType(classMarkers);
                    showMarkerType(foodMarkers);
                    showMarkerType(athleticMarkers);
                }

                return true;
            case R.id.food:
                showMarkerType(foodMarkers);

                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    hideMarkerType(lotMarkers);
                    hideMarkerType(resourceMarkers);
                    hideMarkerType(classMarkers);
                    hideMarkerType(athleticMarkers);

                    // Close the legend view
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    showMarkerType(lotMarkers);
                    showMarkerType(resourceMarkers);
                    showMarkerType(classMarkers);
                    showMarkerType(athleticMarkers);
                }

                return true;
            case R.id.athletics:
                showMarkerType(athleticMarkers);

                item.setChecked(!item.isChecked());

                if(item.isChecked()) {
                    hideMarkerType(lotMarkers);
                    hideMarkerType(resourceMarkers);
                    hideMarkerType(foodMarkers);
                    hideMarkerType(classMarkers);

                    // Close the legend view
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    showMarkerType(lotMarkers);
                    showMarkerType(resourceMarkers);
                    showMarkerType(foodMarkers);
                    showMarkerType(classMarkers);
                }

                return true;
            default:
                return false;
        }
    }

    //Closes drawer instead of closing activity
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //START of code from tutorial (https://youtu.be/lBW58tPLn-A?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi)
    @Override
    protected void onResume(){
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                //getChatRooms() (From tutorial)
                //Can use map
            }
            else{
                getLocationPermission();
            }
        }
    }

    private void openHelpActivity(){
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    private boolean checkMapServices(){
        if (!isServicesOK()) return false;
        return isMapsEnabled();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(this, "Location services is disabled. Some features may not work properly.", Toast.LENGTH_LONG).show();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Step 2: Determines whether or not the current application the user is using has GPS enabled on their device
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        //if they don't have GPS enabled
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    //TODO: Bug; whenever user denies location permissions twice, it still lets them in/breaks app
    //INFO: This method is responsible for the location permission popup. Maybe have to just close
    //      the app if denied? Also not sure why popup comes up twice after clicking deny.
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getMap
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Step 1: Determines whether or not the device is able to use Google Services
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); //super was required??
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //getChatrooms();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
    //END of code from tutorial
}