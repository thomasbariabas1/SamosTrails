package gr.aegean.com.samostrails;

/**
 * Created by phantomas on 3/7/2017.
 */

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class RecordingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;
    private ArrayList<LatLng> TrailLatLonLineString = new ArrayList<>();
    private ArrayList<LatLng> TrailLatLonPoint = new ArrayList<>();
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    Animation animation = null;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates=false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 5 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 0; // 10 meters
    private MapView mMapView;
    GoogleMap mMap;
    private double LastLat;
    private double LastLon;
    // UI elements
    private TextView lblLocation;
    private ImageButton btnStartLocationUpdates;
    private ImageButton layers;
    private ImageButton savebutton;

    public static RecordingFragment newInstance() {
        RecordingFragment fragment = new RecordingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recording_fragment, container, false);
        lblLocation = (TextView) view.findViewById(R.id.recordingmapinfo);
        btnStartLocationUpdates = (ImageButton) view.findViewById(R.id.setRangeButton);
        layers=(ImageButton) view.findViewById(R.id.maplayers) ;
        savebutton=(ImageButton) view.findViewById(R.id.savetrail);
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.norecording));
        mMapView = (MapView) view.findViewById(R.id.recordingmap);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in



        // Toggling the periodic location updates
        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                togglePeriodicLocationUpdates();
            }
        });
        layers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PopUpFragment dialogFragment = new PopUpFragment ();
                dialogFragment.show(fm, "Sample Fragment");
            }
        });
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("linestring",TrailLatLonLineString);
                bundle.putParcelableArrayList("point",TrailLatLonPoint);
                Fragment fragment = CreateTrailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void onResume() {
        super.onResume();
        mMapView.onResume();
        checkPlayServices();
        drawMap();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    public void drawMap(){
        if(TrailLatLonLineString.size()!=0) {
            for (LatLng latlng : TrailLatLonLineString) {
                mMap.addPolyline(new PolylineOptions().add(latlng));
            }
            mMap.addMarker(new MarkerOptions().position(TrailLatLonPoint.get(0)));

        }
    }
    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {


        Log.e("insdide display ln", "locattions");
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLastLocation != null) {
            LastLat= mLastLocation.getLatitude();
            LastLon = mLastLocation.getLongitude();
            TrailLatLonPoint.add(new LatLng(LastLat,LastLon));
        } else {

            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }


        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(LastLat,LastLon)  , 18.0f) );

    }

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {

        Log.e(TAG,"inside toggle periodic"+mRequestingLocationUpdates);
        if (!mRequestingLocationUpdates) {



            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else { // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        mRequestingLocationUpdates = true;
        btnStartLocationUpdates.startAnimation(animation);
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.recording) );
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        double Latitude = location.getLatitude();
                        double Longtitude = location.getLongitude();
                        mMap.addPolyline(new PolylineOptions().add(new LatLng(LastLat,LastLon)).add(new LatLng(Latitude,Longtitude)).width(5).color(Color.BLUE).geodesic(true));
                        LastLat=Latitude;
                        LastLon=Longtitude;
                        lblLocation.setText(LastLat + ", " + LastLon);
                        TrailLatLonLineString.add(new LatLng(LastLat,LastLon));
                        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(LastLat,LastLon)  , 18.0f) );
                       // Log.d("inside periodic",""+new LatLng(LastLat,LastLon));
                    }
                });

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        btnStartLocationUpdates.clearAnimation();
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.norecording) );
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                location.getLatitude();
                location.getLongitude();
                //Log.d("inside periodic stop",""+new LatLng( location.getLatitude(),location.getLongitude()));
            }
        });

    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getActivity(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {

        drawMap();


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}