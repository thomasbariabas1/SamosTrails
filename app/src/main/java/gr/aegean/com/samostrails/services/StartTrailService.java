package gr.aegean.com.samostrails.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RemoteViews;
import com.google.android.gms.maps.model.LatLng;
import java.text.DecimalFormat;
import gr.aegean.com.samostrails.MainActivity;
import gr.aegean.com.samostrails.R;

public class StartTrailService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static int LOCATION_INTERVAL = 1000;
    private static float LOCATION_DISTANCE = 0;
    private static final String LOG_TAG = "ForegroundService";
    private boolean mRequestingLocationUpdates = false;
    private final IBinder mIBinder = new StartTrailService.LocalBinder();
    long base = 0;
    double distance = 0;
    long startedtime=0;
    Chronometer chr;
    RemoteViews views;
    RemoteViews bigViews;
    private boolean firsttime = true;
    boolean backpressed = false;
    int i=0;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            if(i!=0)
            distance=distance+CalculationByDistance(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

            if (mOnServiceListener != null) {
                mOnServiceListener.onDataReceived(location,round(distance,2));
                Log.e("DistanceNotification", "" + distance);
                updatenotification();
            }
            i++;
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

    StartTrailService.LocationListener[] mLocationListeners = new StartTrailService.LocationListener[]{
            new StartTrailService.LocationListener(LocationManager.GPS_PROVIDER)
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent previousIntent = new Intent(this, StartTrailService.class);
        previousIntent.setAction(Constants.ACTION.TONGLE_ACTION);

        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);

        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);


        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            StartTrailService.LOCATION_INTERVAL = intent.getIntExtra("interval", LOCATION_INTERVAL);
            StartTrailService.LOCATION_DISTANCE = intent.getFloatExtra("distance", LOCATION_DISTANCE);
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            if (mOnServiceListener != null)
                backpressed=mOnServiceListener.backpressed();
            showNotification();

        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            base = intent.getLongExtra("base", base);

            Log.e(LOG_TAG, "Received Start Foreground Intent ");
            tongleLocationUpdates();
            showNotification();

        } else if (intent.getAction().equals(Constants.ACTION.CHECK_STATE)) {
            if (mOnServiceListener != null)
                mOnServiceListener.onCheckState(mRequestingLocationUpdates,startedtime,distance);

        }  else if (intent.getAction().equals(Constants.ACTION.BACK_PRESSED)) {
            if (mOnServiceListener != null)
                backpressed=mOnServiceListener.backpressed();

        }else if (intent.getAction().equals(Constants.ACTION.TONGLE_ACTION)) {
            tongleLocationUpdates();
            if(!backpressed)
            base = mOnServiceListener.onChangeState(mRequestingLocationUpdates);
            showNotification();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.e(LOG_TAG, "Received Stop Foreground Intent");
            distance=0;
            stopLocationUpdates();
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (StartTrailService.LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


    public void tongleLocationUpdates() {


        if (mRequestingLocationUpdates) {

            stopLocationUpdates();
        } else {

            startLocationUpdates();
        }
    }

    public void stopLocationUpdates() {

        mRequestingLocationUpdates = false;

        Log.e(TAG, "stoped updates");
        if (mLocationManager != null) {
            for (StartTrailService.LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public void startLocationUpdates() {
        mRequestingLocationUpdates = true;

        if(firsttime){
            startedtime=SystemClock.elapsedRealtime();
        }
        firsttime=false;

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
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public class LocalBinder extends Binder {
        public StartTrailService getInstance() {
            return StartTrailService.this;
        }
    }

    public interface OnServiceListener {
        double onDataReceived(Location data,double distancesum);

        long onChangeState(boolean statechange);

        void onCheckState(boolean statechange, long base , double distance);

        boolean backpressed();
    }


    private StartTrailService.OnServiceListener mOnServiceListener = null;

    public void setOnServiceListener(StartTrailService.OnServiceListener serviceListener) {
        mOnServiceListener = serviceListener;
    }

    Notification status;

    private void showNotification() {

        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, StartTrailService.class);
        previousIntent.setAction(Constants.ACTION.TONGLE_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_recording, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_recordinglayout, ppreviousIntent);

        views.setTextViewText(R.id.status_bar_track_name, "SamosTrails");
        bigViews.setTextViewText(R.id.status_bar_track_name, "SamosTrails");

        if (!mRequestingLocationUpdates) {

            views.setTextViewText(R.id.status_bar_artist_name, "Stopped Trail");
            bigViews.setTextViewText(R.id.status_bar_recordingstatus, "Stopped Trail");
            bigViews.setImageViewResource(R.id.status_bar_recording, R.drawable.start_pressed);
            views.setImageViewResource(R.id.status_bar_recording, R.drawable.start_pressed);
            bigViews.setTextViewText(R.id.status_bar_recording_text, "Start");
        } else {

            views.setTextViewText(R.id.status_bar_artist_name, "Start ...");
            bigViews.setTextViewText(R.id.status_bar_recordingstatus, "Start ...");
            bigViews.setTextViewTextSize(R.id.status_bar_artist_name,0,20);
            bigViews.setImageViewResource(R.id.status_bar_recording, R.drawable.pause);
            views.setImageViewResource(R.id.status_bar_recording, R.drawable.pause);
            bigViews.setTextViewText(R.id.status_bar_recording_text, "Pause");
        }


        bigViews.setChronometer(R.id.chronometer, base, null, mRequestingLocationUpdates);
        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        status.icon = R.drawable.white_0;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    public void updatenotification() {
        bigViews.setTextViewText(R.id.distancenotification, "" + round(distance,2));
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {

        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c * 1000;
    }

    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
