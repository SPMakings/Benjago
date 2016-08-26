package com.sp.benjago.GPSTracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sp.benjago.R;
import com.sp.benjago.application.BenjagoApplication;
import com.sp.benjago.helper.Logger;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saikat's Mac on 29/02/16.
 */

public class TrackingService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String ACTION_START = "com.esolz.su.ezylog.TrackingService.start";
    public static final String ACTION_KILL = "com.esolz.su.ezylog.TrackingService.kill";
    public static final String MY_ACTION = "RESULT_RECEIVER";

    protected final String TAG = "TrackingService";
    protected int NOTIFICATION_ID = 2;
    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationRequest mLocationRequest = null;

    //------Timer handling

    TimerTask timerTask = null;
    Timer timer = null;
    protected int CURRENT_TIME_IN_SEC = 0;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    //--------Location Array for Polyline

    LinkedList<Location> TRACK_LOCATION = null;
    float CURRENt_SPEED = 0.0f, AVG_SPEED = 0.0f;
    float TOTAL_DISTANCE = 0.0f;
    int timeDifference_[];
    int tempIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        initializeTimerTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(ACTION_START)) {
                Logger.showMessage(TAG, "onStartCommand");
                mGoogleApiClient.connect();
//                timer.schedule(timerTask, 0, 1000);
                powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "TrackingServiceTag");
                wakeLock.acquire();
                TRACK_LOCATION = new LinkedList<Location>();
                timeDifference_ = new int[2];

            } else {
                killMe();
                stopLocationUpdates();
                mGoogleApiClient.disconnect();
                wakeLock.release();
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }


    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    public void killMe() {
        CURRENT_TIME_IN_SEC = 0;
        stopForeground(true);
        timerTask.cancel();
        timer.cancel();
        timer.purge();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //----------------GPS tracking
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Logger.showMessage(TAG, "onConnected");
            BenjagoApplication.getInstance().setLAST_KNOWN_LOCATION(LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient));
            Logger.showMessage(TAG, "Started :" + BenjagoApplication.getInstance().getLAST_KNOWN_LOCATION());
            startGPSTracking();
            timer.schedule(timerTask, 0, 1000);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void startGPSTracking() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //-----Kill service if running
    }

    @Override
    public void onLocationChanged(Location location) {

        BenjagoApplication.getInstance().setLAST_KNOWN_LOCATION(location);
        TRACK_LOCATION.add(location);
        if (tempIndex == 2) {
            tempIndex = 0;
        }
        timeDifference_[tempIndex] = CURRENT_TIME_IN_SEC;
        tempIndex++;
        if (TRACK_LOCATION.size() > 1) {
            float distance = TRACK_LOCATION.get(TRACK_LOCATION.size() - 1).distanceTo(TRACK_LOCATION.get(TRACK_LOCATION.size() - 2));
            distance = Math.round(distance);
            if (distance > 1.0f) {
                distance = distance / 1000.00f;
            } else {
                distance = 0.00f;
            }
            getCurrentSpeed(distance);
            TOTAL_DISTANCE = TOTAL_DISTANCE + distance;
            getAverageSpeed();
//            Logger.showMessage(TAG, "LAT ONE :" + TRACK_LOCATION.get(TRACK_LOCATION.size() - 1));
//            Logger.showMessage(TAG, "LAT TWO :" + TRACK_LOCATION.get(TRACK_LOCATION.size() - 2));
//            Logger.showMessage(TAG, "Currentdistance :" + distance);
        }


//        Logger.showMessage(TAG, "CurrentSpeed :" + CURRENt_SPEED);
//        Logger.showMessage(TAG, "TotalSpeed :" + AVG_SPEED);
//        Logger.showMessage(TAG, "TotalDistance :" + TOTAL_DISTANCE);
        Logger.showMessage(TAG, "TotalTime :" + CURRENT_TIME_IN_SEC);

    }


    public void initializeTimerTask() {
        if (timer == null) {
            timer = new Timer();
        }
        timerTask = new TimerTask() {
            public void run() {
                CURRENT_TIME_IN_SEC = CURRENT_TIME_IN_SEC + 1;
                sentMessageToIntent();
                makeMeForGround();
            }
        };
    }


    public void makeMeForGround() {
        int temp_[] = convertHrMin(CURRENT_TIME_IN_SEC);
//        Notification notification = new Notification.Builder(TrackingService.this)
//                .setSmallIcon(R.drawable.ic_action_notification)
//                .setContentText(temp_[0] + " Hrs " + temp_[1] + " min " + temp_[2] + " Sec")
//                .setContentTitle("Driving Timer")
//                .getNotification();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_action_notification)
                        .setContentTitle("Benjago is tracking your location")
                        .setContentText(temp_[0] + " Hrs " + temp_[1] + " min " + temp_[2] + " Sec");

        startForeground(NOTIFICATION_ID, mBuilder.build());
    }


    public void sentMessageToIntent() {
        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        int time_[] = convertHrMin(CURRENT_TIME_IN_SEC);
        intent.putExtra("HOUR", time_[0]);
        intent.putExtra("MINT", time_[1]);
        intent.putExtra("SECOND", time_[2]);
        intent.putExtra("AVG_SPEED", AVG_SPEED);
        intent.putExtra("CURRENT_SPEED", CURRENt_SPEED);
        Logger.showMessage(TAG, "CURRENt_SPEED :" + CURRENt_SPEED);
        Logger.showMessage(TAG, "TOTAL_DISTANCE :" + TOTAL_DISTANCE);

        intent.putExtra("TOTAL_DISTANCE", TOTAL_DISTANCE);
        sendBroadcast(intent);
    }

    public int[] convertHrMin(final int totalSec) {
        int temp_[] = new int[3];
        int min = totalSec / 60;
        int remSec = totalSec % 60;
        int hrs = min / 60;
        int remMin = min % 60;
        temp_[0] = hrs;
        temp_[1] = remMin;
        temp_[2] = remSec;
        return temp_;
    }


    public float getCurrentSpeed(final float distance) {
        int timeSTamp = timeDifference_[0] - timeDifference_[1];
        Logger.showMessage(TAG, "TimeSTamp :" + timeSTamp);
        CURRENt_SPEED = distance / 5.00f;
        CURRENt_SPEED = CURRENt_SPEED * 3600.00f;
        return CURRENt_SPEED;
    }


    public float getAverageSpeed() {
        if (CURRENT_TIME_IN_SEC > 0) {
            AVG_SPEED = TOTAL_DISTANCE / (float) CURRENT_TIME_IN_SEC;
            AVG_SPEED = AVG_SPEED * 3600.00f;
            return AVG_SPEED;
        } else {
            return 0.0f;
        }
    }
}
