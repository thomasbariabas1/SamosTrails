package gr.aegean.com.samostrails;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by phantomas on 3/18/2017.
 */

public class StartTrailFragment extends Fragment implements OnMapReadyCallback {

    ImageButton starttrail;
    boolean hasStarted = false;
    ArrayList<LatLng> lines;
    ArrayList<LatLng> points;
    private MapView mMapView;
    GoogleMap mMap;
    Chronometer timer;
    private long stoppedtime = 0;

    public static StartTrailFragment newInstance() {
        StartTrailFragment fragment = new StartTrailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_trail_fragment, container, false);
        starttrail = (ImageButton) view.findViewById(R.id.starttrailbutton);
        timer = (Chronometer) view.findViewById(R.id.timecreatetrail);
        final Bundle bundle = getArguments();
        lines = bundle.getParcelableArrayList("lines");
        points = bundle.getParcelableArrayList("points");
        mMapView = (MapView) view.findViewById(R.id.starttrailmap);
        mMapView.onCreate(savedInstanceState);
        starttrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonggleStart();
            }
        });
        mMapView.getMapAsync(this);
        return view;
    }


    public void tonggleStart() {
        if (!hasStarted) {
            startTrail();
        } else {
            stopTrail();
        }
    }

    public void startTrail() {
        hasStarted = true;
        timer.setBase(SystemClock.elapsedRealtime() + stoppedtime);
        timer.start();
        starttrail.setImageDrawable(getResources().getDrawable(R.drawable.start_pressed));
    }

    public void stopTrail() {
        hasStarted = false;
        stoppedtime = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        starttrail.setImageDrawable(getResources().getDrawable(R.drawable.start_unpressed));

    }


    public void onStart() {
        super.onStart();
        mMapView.onStart();

    }

    public void onResume() {
        super.onResume();
        mMapView.onResume();


    }

    public void onStop() {
        super.onStop();
        mMapView.onStart();

    }

    public void onPause() {
        super.onPause();
        mMapView.onPause();


    }


    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        PolylineOptions polyline = new PolylineOptions();

        if (lines.size() > 0) {
            for (LatLng latlng : lines) {
                polyline.add(latlng);
            }
            polyline.width(5).color(Color.BLUE).geodesic(true);
            mMap.addPolyline(polyline);
        }
        if(points.size()>0){
            for(LatLng latLng:points){
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 14.0f));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
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
