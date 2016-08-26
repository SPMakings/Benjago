package com.sp.benjago;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sp.benjago.GPSTracker.TrackingService;
import com.sp.benjago.application.BenjagoApplication;
import com.sp.benjago.customview.RobotoLight;
import com.sp.benjago.helper.Logger;

public class LandingPage extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected final String TAG = "StartDrivingSAIKAT";
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationRequest mLocationRequest = null;
    protected int REQUEST_CHECK_SETTINGS = 1;
    boolean isFirstTime = true;

    //============Map mangement


    GoogleMap googleMapFinal = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GoogleMapOptions gMapOption = null;
    MapFragment mapFragment = null;
    UpdateReceiver updateReceiver = null;

    RobotoLight speed_text, timerText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tracking...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor("#E91E63"));
//        }
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (updateReceiver == null) {
            updateReceiver = new UpdateReceiver();
        }
        speed_text = (RobotoLight) findViewById(R.id.speed_text);
        timerText = (RobotoLight) findViewById(R.id.timer_text);
        showMap();
    }

    protected void onStart() {
        //-------First get the permission of accessing GPS Location.
        //-----doing this in onstart to re register the API when app resumes.
        Logger.showMessage(TAG, "onStart...");
        registerReceiver(updateReceiver, new IntentFilter("RESULT_RECEIVER"));
        //=================
        getPermission();
        super.onStart();
    }

    @Override
    protected void onPause() {
        Logger.showMessage(TAG, "onPause...");
        unregisterReceiver(updateReceiver);
        super.onPause();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killService();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).addApi(AppIndex.API)
                .build();
    }


    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Logger.showMessage(TAG, "onConnected if in if block ...");

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                // No explanation needed, we can request the permission.
                Logger.showMessage(TAG, "onConnected if in else block ...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            return;
        } else {
            //-------app already have permission to access GPS Location
            //------add your GPS code here......
            processGPSCall();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //-------app already have permission to access GPS Location
                    //------add your GPS code here......
                    processGPSCall();
                } else {
                    Log.i(TAG, "Permission Denied...");
                    Toast.makeText(getApplicationContext(), "Denied by user. Can't move further in this app.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public void processGPSCall() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Logger.showMessage(TAG, "onConnected");
            BenjagoApplication.getInstance().setLAST_KNOWN_LOCATION(LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient));
            Logger.showMessage(TAG, "" + BenjagoApplication.getInstance().getLAST_KNOWN_LOCATION());
            createLocationRequest();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    //final LocationSettingsStates status = result.getLocationSettingsStates();
                    Logger.showMessage(TAG, "setResultCallback");

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Logger.showMessage(TAG, "All location settings are satisfied. The client can");
                            startService();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Logger.showMessage(TAG, "RESOLUTION_REQUIRED");
                            try {
                                status.startResolutionForResult(
                                        LandingPage.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Logger.showMessage(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                            break;
                    }
                }
            });
        }
    }


    public void startService() {
        Intent i = new Intent(LandingPage.this, TrackingService.class);
        i.setAction(TrackingService.ACTION_START);
        startService(i);
        registerReceiver(updateReceiver, new IntentFilter("RESULT_RECEIVER"));

    }


    public void killService() {
        Intent i = new Intent(LandingPage.this, TrackingService.class);
        i.setAction(TrackingService.ACTION_KILL);
        startService(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.showMessage(TAG, "onActivityResult");
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            startService();
        }
    }


    public void showMap() {
        if (gMapOption == null) {
            gMapOption = new GoogleMapOptions();
            gMapOption.ambientEnabled(true);
            gMapOption.compassEnabled(true);
            //gMapOption.zoomControlsEnabled(true);
        }
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = MapFragment.newInstance(gMapOption);
        fragmentTransaction.replace(R.id.serch_map_lay, mapFragment);
        fragmentTransaction.commit();


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMapFinal = googleMap;
                googleMapFinal.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMapFinal.setTrafficEnabled(true);
                googleMapFinal.setBuildingsEnabled(true);
//                googleMapFinal.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                    @Override
//                    public void onMapLoaded() {
//                        //Toast.makeText(getActivity(), "Map is ready....", Toast.LENGTH_SHORT).show();
//                        if (markerLocation != null) {
//
//                        } else {
//
//                        }
//                    }
//                });


            }
        });
    }


    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //You do here like usual using intent


            Log.i(TAG, " onReceive ");

            if (intent != null) {
                speed_text.setText("Current Speed : " + intent.getFloatExtra("CURRENT_SPEED", 0.0f) + " km/hr");
                timerText.setText("Distance : " + intent.getFloatExtra("TOTAL_DISTANCE", 0.0f) + " km\nAvg Speed : " + intent.getFloatExtra("AVG_SPEED", 0.0f) + " km/hr");
            }
            placeMarkeronMap(BenjagoApplication.getInstance().getLAST_KNOWN_LOCATION());
        }
    }

    public void placeMarkeronMap(final Location markerLocation) {

        Log.i(TAG, " LAT : " + markerLocation.getLatitude());
        Log.i(TAG, " LONG : " + markerLocation.getLongitude());
        try {
            final LatLng latLon = new LatLng(markerLocation.getLatitude(), markerLocation.getLongitude());
            if (isFirstTime) {
                isFirstTime = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLon)
                        .zoom(16)
                        .tilt(45)
                        .build();
                googleMapFinal.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        try {
                            googleMapFinal.clear();
                            googleMapFinal.addMarker(new MarkerOptions()
                                    .position(latLon).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker_new)).draggable(true));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } else {
                googleMapFinal.clear();
                googleMapFinal.addMarker(new MarkerOptions()
                        .position(latLon).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker_new)).draggable(true));

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


//    public Bitmap scaleBMap(final Bitmap b, float reqWidth, float reqHeight) {
//        Matrix m = new Matrix();
//        m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
//        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
//    }


}