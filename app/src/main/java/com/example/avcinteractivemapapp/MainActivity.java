package com.example.avcinteractivemapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * DESCRIPTION:
 *  This class is where the entire program starts, more specifically
 *  in the onCreate() function which sort of acts as the main method.
 */
//yo
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                               EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
    };
    // Unique code for EasyPermissions permissions
    public static final int RC_PERMISSIONS = 864;
    private DrawerLayout drawer;

    // Create instance of MapsFragment
    private MapsFragment fragment;

    MenuItem locationsNearUserButton;
    RelativeLayout actionLayout;
    public NavigationView nav;
    SearchView searchBar;
    ImageButton hamburgerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        //A new fragment object is created to reference the MapsFragment.java class
        fragment = new MapsFragment();

        //Loads the Google Maps fragment to display the map
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

        //Grabbing custom drawer layout from activity_main
        drawer = findViewById(R.id.drawer_layout);
        hamburgerButton = findViewById(R.id.hamburger_button);

        searchBar = findViewById(R.id.searchView);

        hamburgerButton.setOnClickListener(v -> {
            SearchBar.hideKeyboard(searchBar, this);

            drawer.openDrawer(GravityCompat.START);
        });

        // Opens HelpActivity when help button is clicked
        MenuItem helpButton = nav.getMenu().findItem(R.id.nav_help);
        helpButton.setOnMenuItemClickListener(item -> {
            openHelpActivity();
            return true;
        });

        // Initiates method to determine the nearest markers based on the user's current location
        locationsNearUserButton = nav.getMenu().findItem(R.id.locations_near_me);
        // Sets initial state of switch as unchecked
        locationsNearUserButton.setActionView(View.inflate(this, R.layout.switch_item, null));
        // Change state of switch when clicked and enable/disable circle filter
        locationsNearUserButton.setOnMenuItemClickListener(item -> {
            circleFilterTask();
            return true;
        });

        MenuItem nearestParkingButton = nav.getMenu().findItem(R.id.nearest_parking);
        nearestParkingButton.setOnMenuItemClickListener(item -> {
            nearestParkingTask();
            return true;
        });

        // Closes toolbar when map button is clicked
        MenuItem mapButton = nav.getMenu().findItem(R.id.nav_map);
        mapButton.setOnMenuItemClickListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @AfterPermissionGranted(RC_PERMISSIONS)
    public void circleFilterTask() {
        if (hasLocationPermission()) {
            if (fragment.enableCircleFilter()) {
                // Handled by circleFilterHandler() in MapsFragment
            }
            else {
                // Set "Locations Near Me" switch to off
                toggleOffCircleFilterSwitch();
                // Disable all active filters
                fragment.disableAllFilters();
                // Show all markers
                fragment.showAllMarkers();
            }
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            locationPermissionsRationale();
        }
    }

    @AfterPermissionGranted(RC_PERMISSIONS)
    public void nearestParkingTask() {
        if (hasLocationPermission()) {
           // Toast.makeText(this, "nearestParkingTask", Toast.LENGTH_SHORT).show();
            fragment.enableParkingCalculator();
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            locationPermissionsRationale();
        }

    }

    private void locationPermissionsRationale(){
        EasyPermissions.requestPermissions(this, "This app requires location access in order to use some features.", RC_PERMISSIONS, REQUIRED_PERMISSIONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // Handle search query
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleOnCircleFilterSwitch(){
        actionLayout = (RelativeLayout) View.inflate(this, R.layout.switch_item_enabled, null);
        locationsNearUserButton.setActionView(actionLayout);
    }

    public void toggleOffCircleFilterSwitch(){
        actionLayout = (RelativeLayout) View.inflate(this, R.layout.switch_item, null);
        locationsNearUserButton.setActionView(actionLayout);
    }

    public boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /*
        Called whenever this activity is about to go into the background or not be visible by the user.
        Good place to save unsaved data, perform cleanup operations, etc.
        Overriding this method allows us to create custom behaviors before the activity is no longer the priority.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.i("onPause", "");

        // Resets the state for all location markers
        fragment.filterMarkers();
    }

    public void disableNonCircleFilterMarkers() {
        // Disable filter checks (UI)
        Menu filtersMenu = nav.getMenu().findItem(R.id.Filters).getSubMenu();
        for (int i = 0; i < filtersMenu.size(); i++) {
            filtersMenu.getItem(i).setChecked(false);
        }
    }

    // Whenever an item is selected in the Map Legend
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Set the checkbox checked state
        item.setChecked(!item.isChecked());

        // If a filter is activated, disable the circle filter
        fragment.disableCircleFilter();

        // Set the boolean value for the filter clicked on
        switch (item.getItemId()) {
            case (R.id.lots):
                fragment.showParkingLots = item.isChecked();
                break;
            case (R.id.classrooms):
                fragment.showClassrooms = item.isChecked();
                break;
            case (R.id.studentResources):
                fragment.showStudentResources = item.isChecked();
                break;
            case (R.id.food):
                fragment.showFood = item.isChecked();
                break;
            case (R.id.athletics):
                fragment.showAthletics = item.isChecked();
                break;
        }

        fragment.filterMarkers();
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

        SearchBar.hideKeyboard(searchBar, this);
        Log.i("onResume", "resume");
    }

    private void openHelpActivity(){
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        else {
            locationPermissionsRationale();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.i("onActivityResult", hasLocationPermission() ? "Location granted" : "Location not granted");
        }
    }
}
