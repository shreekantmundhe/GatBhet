package com.gatbhet.activities;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.gatbhet.config.GenericWebServiceAsyncTask;
import com.gatbhet.config.NotificationAsyncTask;
import com.gatbhet.config.Util;
import com.gatbhet.config.WebServiceAsyncTask;
import com.gatbhet.model.Alert;
import com.gatbhet.model.TokenResponse;
import com.gatbhet.services.LoginWebService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ADMINIBM on 4/23/2016.
 */
public class BackgroundGPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, WebServiceAsyncTask.WebServiceResponseListener,GenericWebServiceAsyncTask.GenericWebServiceResponseListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    
    private static final int REQUEST_LOCATION = 14441;
    private static final int REQUEST_CHECK_SETTINGS = 1211;
    private static final String NAME = "Location";
    private GoogleApiClient googleApiClient;
    private String latitude,longitude;


    @Override
    public void onConnected(Bundle bundle) {
        Util.log("Location", "Connected to google services");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Util.log("Location", "Connection suspended to google services");
    }

    @Override
    public void onLocationChanged(Location location) {
        Util.log("Location", "Location changed");
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        WebServiceAsyncTask webServiceAsyncTask = new WebServiceAsyncTask();
        webServiceAsyncTask.setWebServiceResponseListener(this);
        webServiceAsyncTask.execute();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Util.log("Location", "Connection failed to google services");
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Util.log("Location", "Permission is not given");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Util.log("Location", "Requesting location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, getLocationRequest(), this);
    }

    private LocationRequest getLocationRequest() {
        Util.log("Location", "Requesting for location");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Util.log("Location", "Service started");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.log("Location", "Service destroyed");
        googleApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResponseReceived(String response) {
        Util.log("GPS Service","Token Response : " + response);
       GenericWebServiceAsyncTask genericWebServiceAsyncTask = new GenericWebServiceAsyncTask(new LoginWebService(response,"alerts","9766363775",latitude,longitude));
        genericWebServiceAsyncTask.setWebServiceResponseListener(this);
        genericWebServiceAsyncTask.execute();
    }

    @Override
    public void onGenericResponseReceived(String response) {

        Util.log("Alerts",response);
        Gson gson = new Gson();
        TokenResponse tokenResponse = gson.fromJson(response, TokenResponse.class);
        Util.log("Alerts","Alerts Length : " + tokenResponse.getData().getAlerts().size());
//        NotificationAsyncTask notificationAsyncTask = new NotificationAsyncTask(getApplicationContext());
//        notificationAsyncTask.execute(tokenResponse.getData().getAlerts().toArray(new Alert[tokenResponse.getData().getAlerts().size()]));
        for (Alert alert: tokenResponse.getData().getAlerts()
             ) {
            Util.displayNotification(getApplicationContext(),alert.getAtitle(),alert.getAmsg());
        }
    }
}
