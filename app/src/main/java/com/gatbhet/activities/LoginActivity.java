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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_LOCATION = 14441;
    private static final int REQUEST_CHECK_SETTINGS = 1211;
    private EditText username, password, mobile;
    private Button login;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findAllViews();

        Intent intent = new Intent(this,BackgroundGPSService.class);
        startService(intent);

        checkLocationSettings();
//        checkLocationPermission();


        new WebServiceAsyncTask(this).execute();

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        myWebView.loadUrl("http://dev.mulikainfotech.com/gathbhet.com/");
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
}
