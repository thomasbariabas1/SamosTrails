package gr.aegean.com.samostrails;

import android.content.ComponentName;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.services.Constants;
import gr.aegean.com.samostrails.services.StartTrailService;


public class StartTrailFragment extends Fragment implements OnMapReadyCallback, StartTrailService.OnServiceListener  {
    ImageButton starttrail;
    boolean hasStarted = false;
    ArrayList<LatLng> lines;
    ArrayList<LatLng> points;
    private MapView mMapView;
    GoogleMap mMap;
    TextView timer;
    private TextView distance;
    private long stoppedtime = 0;
    private long starttime=0;
    private double distancesum = 0;
    private ImageButton stop;
    private double LastLat;
    private double LastLon;
    StartTrailService service;
    boolean mIsBound;
    int trailid;
    Trail trail;
    boolean backpressed = false;
    private TextView avgSpeed;
    boolean firsttime=true;
    String start;
    double pausedtime=0;
    long sumpausedtime=0;
    Chronometer chrono;
    public static StartTrailFragment newInstance() {
        return new StartTrailFragment();
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
        stop = (ImageButton) view.findViewById(R.id.stopbuttonstart);
        ImageButton back = (ImageButton) view.findViewById(R.id.backtrailbutton);
        timer = (TextView) view.findViewById(R.id.timecreatetrail);
        timer.setText("0");
        final Bundle bundle = getArguments();
        trail = bundle.getParcelable("trail");
        trailid= trail.getTrailId();
        lines = bundle.getParcelableArrayList("lines");
        points = bundle.getParcelableArrayList("points");
        avgSpeed = (TextView) view.findViewById(R.id.avgspeedstart);
        chrono = (Chronometer) view.findViewById(R.id.chrono);
        mMapView = (MapView) view.findViewById(R.id.starttrailmap);
        mMapView.onCreate(savedInstanceState);
        backpressed=false;
        Intent startIntent = new Intent(getActivity(), StartTrailService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        getActivity().startService(startIntent);
        doBindService();
        starttrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonggleStart();
            }
        });
        distance = (TextView) view.findViewById(R.id.distancetrail);
        mMapView.getMapAsync(this);

        if(!(backpressed&&hasStarted)){
            stop.setVisibility(View.GONE);
            starttrail.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.start_unpressed));
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               backpressed=true;
                FragmentManager fm = getFragmentManager();
                StartTrailPopUpInfo dialogFragment = new StartTrailPopUpInfo();
                bundle.putParcelable("trail", trail);
                dialogFragment.setArguments(bundle);
                dialogFragment.setTargetFragment(StartTrailFragment.this, 300);
                dialogFragment.show(fm, "Sample Fragment");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setHasStartedTrail(0);
                ((MainActivity)getActivity()).setFirstTime(true);
                stoppedtime = System.currentTimeMillis();
                hasStarted=false;
                starttrail.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.start_unpressed));
                stop.setVisibility(View.INVISIBLE);
                doUnbindService();
                FragmentManager fm = getFragmentManager();
                StartTrailPopUp dialogFragment = new StartTrailPopUp();
                Bundle bundle = getArguments();
                bundle.putLong("endtime",stoppedtime);
                bundle.putString("starttime",start);
                bundle.putString("end",getRealTime(stoppedtime));
                bundle.putDouble("distance",distancesum);
                bundle.putLong("pausedtime",sumpausedtime);
                bundle.putLong("starttimelong",starttime);
                dialogFragment.setArguments(bundle);
                dialogFragment.setTargetFragment(StartTrailFragment.this, 300);
                dialogFragment.show(fm, "Sample Fragment");
                stoppedtime=0;
                distancesum=0;
                sumpausedtime=0;
                starttime=0;
                firsttime=true;
            }
        });
        return view;
    }

    public void tonggleStart() {
        if (!hasStarted) {

            Intent startIntent = new Intent(getActivity(), StartTrailService.class);
            startIntent.putExtra("base",SystemClock.elapsedRealtime() );
            startIntent.setAction(Constants.ACTION.PLAY_ACTION);
            getActivity().startService(startIntent);

            startTrail();
        } else {
            Intent startIntent = new Intent(getActivity(), StartTrailService.class);
            startIntent.setAction(Constants.ACTION.PLAY_ACTION);
            startIntent.putExtra("base",SystemClock.elapsedRealtime());
            getActivity().startService(startIntent);
            stopTrail();
        }
    }

    public void startTrail() {
        if(firsttime){
            starttime = System.currentTimeMillis();
            start = getRealTime(starttime);
            timer.setText(start);
            chrono.setBase(SystemClock.elapsedRealtime() + stoppedtime);
            chrono.start();
            Log.e("startedtime",""+starttime);
            firsttime=false;
        }
        else
        sumpausedtime += System.currentTimeMillis()-pausedtime;

        Log.e("sumpausedtime",""+sumpausedtime);
        Log.e("pausedtime",""+pausedtime);
        Log.e("System current time",""+System.currentTimeMillis());
        if(stop.getVisibility()!=View.VISIBLE)
            stop.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setHasStartedTrail(trailid);
        ((MainActivity)getActivity()).setFirstTime(false);
        hasStarted = true;

        starttrail.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.pause));

    }

    public void stopTrail() {
        if(stop.getVisibility()!=View.VISIBLE)
            stop.setVisibility(View.VISIBLE);
        hasStarted = false;
        stoppedtime = chrono.getBase() - SystemClock.elapsedRealtime();
        chrono.stop();
        pausedtime=System.currentTimeMillis();
        Log.e("pausedtime",""+pausedtime);
        starttrail.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.start_pressed));

    }

    public void onStart() {
        super.onStart();
        mMapView.onStart();

    }

    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if(mConnection !=null) {
            Log.e("Connection",""+mConnection);
            Intent startIntent = new Intent(getActivity(), StartTrailService.class);
            startIntent.setAction(Constants.ACTION.CHECK_STATE);
            getActivity().startService(startIntent);
        }
        if(mMap!=null)
        setUpMap();

    }

    public void onStop() {
        super.onStop();
        mMapView.onStart();

    }

    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    public void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();

    }

    private void setUpMap() {

        PolylineOptions polyline = new PolylineOptions();

        if (lines.size() > 0) {
            for (LatLng latlng : lines) {
                polyline.add(latlng);
            }
            polyline.width(5).color(Color.BLUE).geodesic(true);
            mMap.addPolyline(polyline);
        }
        if (points.size() > 0) {
            for (LatLng latLng : points) {
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


    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((StartTrailService.LocalBinder) iBinder).getInstance();
            service.setOnServiceListener(StartTrailFragment.this);
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
                StartTrailService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            Intent startIntent = new Intent(getActivity(), StartTrailService.class);
            startIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            getActivity().startService(startIntent);
            // Detach our existing connection.
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public double onDataReceived(Location location,double distancefromservice) {
        if(getActivity()!=null){
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return 0;
            }}
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        double Latitude = location.getLatitude();
        double Longtitude = location.getLongitude();

        float seconds = 0;
        float mins = seconds/60;
        float hours = mins/60;


        if (LastLat != 0 && LastLon != 0) {
            avgSpeed.setText(String.valueOf(round((distancefromservice*1000)/hours,2)));
            distance.setText(String.valueOf(round(distancefromservice, 2)));
            LastLat = Latitude;
            LastLon = Longtitude;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LastLat, LastLon), 18.0f));
        } else {
            LastLat = Latitude;
            LastLon = Longtitude;
        }
        return round(distancesum, 2);
    }

    @Override
    public long onChangeState(boolean statechange) {
        hasStarted = statechange;
        if(hasStarted) {
            startTrail();
            return SystemClock.elapsedRealtime() + stoppedtime;
        }else {
            stopTrail();
            return chrono.getBase() - SystemClock.elapsedRealtime();
        }

    }

    @Override
    public void onCheckState(boolean statechange,long base,double distance) {
        hasStarted = statechange;
        this.distance.setText(String.valueOf(round(distance,2)));
        Log.e("base:",""+base);
        if(hasStarted) {
            startTrail();
        }else {
            stopTrail();
        }
    }

    @Override
    public boolean backpressed() {
        return backpressed;
    }

    public String getRealTime(Long time){
        String test;
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);
        int hours   = (int) ((time / (1000*60*60)) % 24);
        test=hours +3+":"+minutes+":"+seconds;
        return  test;
    }
}
