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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.navigation.NavigationView;

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

    // Booleans for determining the visibility of markers
    private boolean showParkingLots = false;
    private boolean showClassrooms = false;
    private boolean showStudentResources = false;
    private boolean showFood = false;
    private boolean showAthletics = false;

    // Create instance of MapsFragment
    private MapsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Telling app to use custom toolbar as actionbar replacement
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        //A new fragment object is created to reference the MapsFragment.java class
        fragment = new MapsFragment();

        //Loads the Google Maps fragment to display the map
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();

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
        MenuItem locationsNearUserButton = nav.getMenu().findItem(R.id.locations_near_me);
        // Sets initial state of swtich as unchecked
        locationsNearUserButton.setActionView((RelativeLayout) getLayoutInflater().inflate(R.layout.switch_item, null));
        // Change state of switch when clicked and enable/disable circle filter
        locationsNearUserButton.setOnMenuItemClickListener(item -> {
            RelativeLayout actionLayout;
            if (MapsFragment.enableCircleFilter()) {
                actionLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.switch_item_enabled, null);
                locationsNearUserButton.setActionView(actionLayout);
            }
            else{
                actionLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.switch_item, null);
                locationsNearUserButton.setActionView(actionLayout);
            }
            return true;
        });

        MenuItem nearestParkingButton = nav.getMenu().findItem(R.id.nearest_parking);
        nearestParkingButton.setOnMenuItemClickListener(item -> {
            // Calculate the nearest lot and move camera
            if (!fragment.enableParkingCalculator()) {
                buildAlertMessageNoGps();
                return false;
            }

            // Close the drawer
            drawer.closeDrawer(GravityCompat.START);

            return true;
        });

        // Closes toolbar when map button is clicked
        MenuItem mapButton = nav.getMenu().findItem(R.id.nav_map);
        mapButton.setOnMenuItemClickListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

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
        filterMarkers();
    }
    // Filter the markers based on the boolean values of each marker type's visibility
    private void filterMarkers() {
        // If all the filters are unchecked, we want to show all the markers
        if (isFiltersDisabled()) {
            showAllMarkers();
            return;
        }

        for (Marker marker : fragment.parkingLotMarkers) marker.setVisible(showParkingLots);
        for (Marker marker : fragment.classroomLocations) marker.setVisible(showClassrooms);
        for (Marker marker : fragment.resourceLocations) marker.setVisible(showStudentResources);
        for (Marker marker : fragment.foodLocations) marker.setVisible(showFood);
        for (Marker marker : fragment.athleticLocations) marker.setVisible(showAthletics);
    }

    // Helper method that toggles all markers to be visible
    private void showAllMarkers() {
        for (Marker marker : fragment.locations.keySet()) marker.setVisible(true);
    }

    // Helper method that checks if all the filters are set to false (unchecked)
    private boolean isFiltersDisabled() {
        if (showParkingLots) return false;
        if (showClassrooms) return false;
        if (showStudentResources) return false;
        if (showFood) return false;
        return !showAthletics;
    }

    // Whenever an item is selected in the Map Legend
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Set the checkbox checked state
        item.setChecked(!item.isChecked());

        // Set the boolean value for the filter clicked on
        switch (item.getItemId()) {
            case (R.id.lots):
                showParkingLots = item.isChecked();
                break;
            case (R.id.classrooms):
                showClassrooms = item.isChecked();
                break;
            case (R.id.studentResources):
                showStudentResources = item.isChecked();
                break;
            case (R.id.food):
                showFood = item.isChecked();
                break;
            case (R.id.athletics):
                showAthletics = item.isChecked();
                break;
        }

        filterMarkers();
        return super.onOptionsItemSelected(item);
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