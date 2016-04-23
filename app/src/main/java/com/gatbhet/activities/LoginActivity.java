package com.gatbhet.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gatbhet.R;
import com.gatbhet.config.Util;
import com.gatbhet.config.WebServiceAsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements WebServiceAsyncTask.WebServiceResponseListener,View.OnClickListener{

    private static final int REQUEST_LOCATION = 14441;
    private static final int REQUEST_CHECK_SETTINGS = 1211;
    private EditText username, password, mobile;
    private Button login;
    private GoogleApiClient googleApiClient;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findAllViews();
        login.setOnClickListener(this);

        Intent intent = new Intent(this,BackgroundGPSService.class);
        startService(intent);

        checkLocationSettings();
//        checkLocationPermission();


        WebServiceAsyncTask webServiceAsyncTask = new WebServiceAsyncTask(this);
        webServiceAsyncTask.execute();
        webServiceAsyncTask.setWebServiceResponseListener(this);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        Util.displayNotification(this);
    }

    private void findAllViews() {
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        mobile = (EditText) findViewById(R.id.et_mobile_number);
        login = (Button) findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                performLogin();
                break;

            default:
                break;
        }
    }

    private void performLogin() {
        int validationResult = validateUserCredentials();
        if (validationResult == 0) {
            executeLogin();
        } else {
            Toast.makeText(this, getResources().getText(validationResult), Toast.LENGTH_SHORT).show();
        }
    }

    private void executeLogin() {
        //TODO Async task should be started here
        new WebServiceAsyncTask(this).execute();
    }

    private int validateUserCredentials() {
        if (username.getText().length() == 0 || password.getText().length() == 0) {
            return R.string.credentials_blank;
        } else if (username.getText().length() >= 7 || password.getText().length() >= 7) {
            return R.string.credentials_minimum_length;
        } else if (mobile.getText().length() != 10) {
            return R.string.mobile_minimum_length;
        } else {
            return 0;
        }
    }

    private void checkLocationSettings(){
        Util.log("Location","Checking location settings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest());
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    LoginActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }


        });
    }

    private LocationRequest getLocationRequest() {
        Util.log("Location", "Requesting for location");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void getLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Util.log("Location", "Location changed : lat : " + location.getLatitude() + " long : " + location.getLongitude());
                Toast.makeText(LoginActivity.this, "getLocation Location changed : lat : " + location.getLatitude() + " long : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onResponseReceived(String response) {
        try {
            Map<String, String> extraHeaders = new HashMap<String, String>();
            String timeStamp = Util.getTimeStamp();
            Util.log("Login","Time Stamp : " + timeStamp);
            HashMap<String,String> requestParams = new HashMap<String, String>();
            requestParams.put("timestamp",timeStamp);
            requestParams.put("request_token", response);
            requestParams.put("request_for","profile");//alerts,audio,profile
            requestParams.put("caller_ref_id","9766363775");
            extraHeaders.put("security_token",Util.createSecurityToken(response,Util.getTimeStamp(),requestParams));
            extraHeaders.put("security_token","SPT-288");
            webView.loadUrl("http://dev.mulikainfotech.com/gathbhet.com/",extraHeaders);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
