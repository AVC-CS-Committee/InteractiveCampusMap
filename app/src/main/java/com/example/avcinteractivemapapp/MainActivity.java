package com.example.avcinteractivemapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/*TODO: (FOR THOSE WORKING ON THE GOOGLE MAPS API)
        Tutorial being followed: https://youtu.be/lBW58tPLn-A?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi
        -Add Google Maps Dependency to Gradle file (DONE)
        -Get a working API Key
        -Restrict API Key; get done before deployment
        -Add API key to the manifest file
        -Add permissions ? (11:00 in tutorial)
        -Look into Google Services, GPS, and location permissions (https://youtu.be/1f4b2-Y_q2A?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi)
        -Display a Google Map using a MapView; customize (make it look like the final design)
        -...

 */


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}