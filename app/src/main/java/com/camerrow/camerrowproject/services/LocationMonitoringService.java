package com.camerrow.camerrowproject.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    private String user_id;



    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();

    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";


    @Override
    public void onCreate() {

        super.onCreate();


//        mAuth = FirebaseAuth.getInstance();

//        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
//        mDatabaseUsers.keepSynced(true);

//            if (mAuth.getCurrentUser()!= null) {
//                user_id = mAuth.getCurrentUser().getUid();

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

//            mLocationRequest.setInterval(1000);
//            mLocationRequest.setFastestInterval(1000);
//
//            int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
//
//            mLocationRequest.setPriority(priority);
//
//
//
//
//
//            mLocationClient.connect();
 //       }


    }

//    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("started","service");
//        //moved to onCreate because  GoogleApiClient is throwing “GoogleApiClient is not connected yet”
////        mLocationClient = new GoogleApiClient.Builder(this)
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this)
////                .addApi(LocationServices.API)
////                .build();

        mLocationRequest.setInterval(5*1000);
        mLocationRequest.setFastestInterval(5*1000);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");



        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

//            mDatabaseUsers.child(user_id).child("location").child("longitude").setValue(location.getLongitude());
//            mDatabaseUsers.child(user_id).child("location").child("latitude").setValue(location.getLatitude());

        }


    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }
}
