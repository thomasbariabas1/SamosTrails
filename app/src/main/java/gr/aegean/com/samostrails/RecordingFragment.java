package gr.aegean.com.samostrails;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;

import gr.aegean.com.samostrails.Services.Constants;
import gr.aegean.com.samostrails.Services.TrailService;

public class RecordingFragment extends Fragment implements OnMapReadyCallback, PopUpFragment.EditNameDialogListener, TrailService.OnServiceListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<LatLng> TrailLatLonLineString = new ArrayList<>();
    TrailService service;
    boolean mIsBound;
    Animation animation = null;
    TextView distance;
    Chronometer time;
    // booean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 6000; // 5 sec
    private int DISPLACEMENT = 0; // 10 meters//100metre is the best for better
    private MapView mMapView;
    GoogleMap mMap;
    private double LastLat;
    private double LastLon;
    private double distancesum = 0;
    private ImageButton btnStartLocationUpdates;
    private ImageButton layers;
    private ImageButton savebutton;
    private ImageButton clear;
    private long stoppedtime = 0;

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
        btnStartLocationUpdates = (ImageButton) view.findViewById(R.id.setRangeButton);
        layers = (ImageButton) view.findViewById(R.id.maplayers);
        savebutton = (ImageButton) view.findViewById(R.id.savetrail);
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.norecording));
        mMapView = (MapView) view.findViewById(R.id.recordingmap);
        distance = (TextView) view.findViewById(R.id.distancerecording);
        time = (Chronometer) view.findViewById(R.id.timerecording);
        clear = (ImageButton) view.findViewById(R.id.clearbuttonrecording);
        clear.setVisibility(View.GONE);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete map")
                        .setMessage("Are you sure you want to delete this map?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                initiateMap();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // First we need to check availability of play services


        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); //duration - half a second
        animation.setInterpolator(new LinearInterpolator()); //do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); //Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); //Reverse animation at the end so the button will fade back in


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
                PopUpFragment dialogFragment = new PopUpFragment();
                dialogFragment.setTargetFragment(RecordingFragment.this, 300);
                dialogFragment.show(fm, "Sample Fragment");

            }
        });
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TrailLatLonLineString.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("linestring", TrailLatLonLineString);
                    Fragment fragment = CreateTrailFragment.newInstance();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, fragment);
                    transaction.commit();
                } else
                    Toast.makeText(getActivity(), "Nothing to Save", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();

    }

    public void onResume() {
        super.onResume();
        mMapView.onResume();
        setUpMap();


    }


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }


    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();
            Log.e(TAG, "Periodic location updates started!");
        } else {

            // Stopping the location updates
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
            Log.e(TAG, "Periodic location updates stopped!");
        }
    }


    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        time.setBase(SystemClock.elapsedRealtime() + stoppedtime);
        time.start();
        clear.setVisibility(View.GONE);
        Intent startIntent = new Intent(getActivity(), TrailService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        getActivity().startService(startIntent);
        doBindService();
        btnStartLocationUpdates.startAnimation(animation);
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.recording));
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {

        Intent stopIntent = new Intent(getActivity(), TrailService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        getActivity().startService(stopIntent);
        doUnbindService();
        stoppedtime = time.getBase() - SystemClock.elapsedRealtime();
        time.stop();
        clear.setVisibility(View.VISIBLE);
        btnStartLocationUpdates.clearAnimation();
        btnStartLocationUpdates.setImageDrawable(getResources().getDrawable(R.drawable.norecording));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {

        PolylineOptions polyline = new PolylineOptions();

        if (TrailLatLonLineString.size() > 0) {

            for (LatLng latlng : TrailLatLonLineString) {
                polyline.add(latlng);
            }
            polyline.width(5).color(Color.BLUE).geodesic(true);
            mMap.addPolyline(polyline);


        }
        // displayLocation();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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

    public void initiateMap() {
        TrailLatLonLineString.clear();
        distancesum = 0;
        distance.setText(String.valueOf(distancesum));
        time.setBase(SystemClock.elapsedRealtime());
        mMap.clear();
        clear.setVisibility(View.GONE);
        stoppedtime = 0;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFinishEditDialog(int displacement, int interval) {
        DISPLACEMENT = displacement;
        UPDATE_INTERVAL = interval * 1000;
        Log.e("popupresult", "" + DISPLACEMENT);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((TrailService.LocalBinder) iBinder).getInstance();
            service.setOnServiceListener(RecordingFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        getActivity().bindService(new Intent(getActivity(),
                TrailService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }


    @Override
    public void onDataReceived(Location location) {
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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        double Latitude = location.getLatitude();
        double Longtitude = location.getLongitude();
        if (LastLat != 0 && LastLon != 0) {
            PolylineOptions pl = new PolylineOptions().add(new LatLng(LastLat, LastLon)).add(new LatLng(Latitude, Longtitude)).width(5).color(Color.BLUE).geodesic(true);
            Log.e("width:", "" + CalculationByDistance(new LatLng(LastLat, LastLon), new LatLng(Latitude, Longtitude)));
            mMap.addPolyline(pl);
            distancesum = distancesum + CalculationByDistance(new LatLng(LastLat, LastLon), new LatLng(Latitude, Longtitude));
            distance.setText(String.valueOf(round(distancesum, 2)));
            LastLat = Latitude;
            LastLon = Longtitude;
            TrailLatLonLineString.add(new LatLng(LastLat, LastLon));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LastLat, LastLon), 18.0f));
        } else {
            LastLat = Latitude;
            LastLon = Longtitude;
        }


    }


}