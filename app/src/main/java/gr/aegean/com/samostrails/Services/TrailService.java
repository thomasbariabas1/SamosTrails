package gr.aegean.com.samostrails.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import gr.aegean.com.samostrails.MainActivity;
import gr.aegean.com.samostrails.R;


public class TrailService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private static final String LOG_TAG = "ForegroundService";
    private boolean mRequestingLocationUpdates = true;
    private final IBinder mIBinder = new LocalBinder();
    private int statechange=0;
    long starttime=0;
    long stoppedtime=0;
    NotificationCompat.Builder  notification=null;
    NotificationManager mNotificationManager;
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            if(mOnServiceListener != null){
                mOnServiceListener.onDataReceived(location);
                statechange=0;
            }
            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           /* PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);*/

        Intent previousIntent = new Intent(this, TrailService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);
        // While making notification
        Intent i = new Intent("do_something");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.white_0);



        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Trail Recording")
                    .setTicker("Trail Recording")
                    .setContentText("Trail Recording")
                    .setSmallIcon(R.drawable.white_0)
                    .setOngoing(mRequestingLocationUpdates)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(mRequestingLocationUpdates ? R.drawable.norecording
                                    : R.drawable.recording,
                            mRequestingLocationUpdates ? "Stop"
                                    : "Start", ppreviousIntent)
                    .setUsesChronometer(true);
            starttime= System.currentTimeMillis();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification.build());

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            tongleLocationUpdates();

            updateNotification(pendingIntent,ppreviousIntent,icon);
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.e(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }
public void updateNotification( PendingIntent pendingIntent ,PendingIntent ppreviousIntent,  Bitmap icon ){



    Notification notification = new NotificationCompat.Builder(this)
            .setContentTitle("Trail Recording")
            .setTicker("Trail Recording")
            .setContentText("Trail Recording")
            .setSmallIcon(R.drawable.white_0)
            .setOngoing(mRequestingLocationUpdates)
            .setLargeIcon(
                    Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(mRequestingLocationUpdates ? R.drawable.norecording
                            : R.drawable.recording,
                    mRequestingLocationUpdates ? "Stop"
                            : "Start", ppreviousIntent)
            .setUsesChronometer(mRequestingLocationUpdates)
            .setWhen(starttime)
            .build();

    startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,   notification);
}
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


    public void tongleLocationUpdates(){
        mOnServiceListener.onChangeState(mRequestingLocationUpdates);
        if(mRequestingLocationUpdates){

            stopLocationUpdates();
        }else{

            startLocationUpdates();
        }
    }

    public void stopLocationUpdates() {

        mRequestingLocationUpdates=false;

        //mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification.build());
        Log.e(TAG,"stoped updates");
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
public void startLocationUpdates(){
 mRequestingLocationUpdates=true;
   // mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification.build());
    try {
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                mLocationListeners[1]);
    } catch (java.lang.SecurityException ex) {
        Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
    }
    try {
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                mLocationListeners[0]);
    } catch (java.lang.SecurityException ex) {
        Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
    }
}
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mIBinder;
    }

    public class LocalBinder extends Binder
    {
        public TrailService getInstance()
        {
            return TrailService.this;
        }
    }

    public interface OnServiceListener{
        public void onDataReceived(Location data );
        public void onChangeState(boolean statechange);
    }
    private OnServiceListener mOnServiceListener = null;

    public void setOnServiceListener(OnServiceListener serviceListener){
        mOnServiceListener = serviceListener;
    }
}