package gr.aegean.com.samostrails;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Models.Trail;


public class TrailInfoFragment extends Fragment   implements OnMapReadyCallback {
    private static final double LAT = 32.084;
    private static final double LON = 34.8878;
    private View view;
    private Marker marker;
    private GoogleMap mMap;
    MapView mMapView;
    private ImageView TrailImage;
    private TextView TrailTitle;
    private TextView Description;
    private TextView StartingPoint;
    private TextView MainSights;
    private TextView Tips;
    private TextView Distance;
    private TextView KindOfTrail;
    private TextView DifficultyLevel;
    private TextView ChildrenFriendly;
    private TextView OtherTransports;
    private TextView ConnectionToOtherTrails;
    private Trail trail;
    ScrollView hsv;
    public static TrailInfoFragment newInstance() {
        TrailInfoFragment fragment = new TrailInfoFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.trail_info, container, false);
        // Inflate the layout for this fragment
        //   TrailImage = (ImageView) view.findViewById(R.id.image_trail);
        TrailTitle = (TextView) view.findViewById(R.id.trail_title);
         Description=(TextView) view.findViewById(R.id.description);
         StartingPoint=(TextView) view.findViewById(R.id.startingpoint);
         MainSights=(TextView) view.findViewById(R.id.mainsights);
         Tips=(TextView) view.findViewById(R.id.tips);
         Distance=(TextView) view.findViewById(R.id.distance);
         KindOfTrail=(TextView) view.findViewById(R.id.kindoftrail);
         DifficultyLevel=(TextView) view.findViewById(R.id.difficultylevel);
         ChildrenFriendly=(TextView) view.findViewById(R.id.childrenfriendly);
         OtherTransports=(TextView) view.findViewById(R.id.othertransport);
         ConnectionToOtherTrails=(TextView) view.findViewById(R.id.connectiontoothertrails);
        Bundle bundle = getArguments();
         trail = (Trail) bundle.getParcelable("trail");
        //TrailImage.setImageBitmap(trail.getDownlImage());
        TrailTitle.setText(trail.getTitle());

        Description.setText(trail.getDescription());
        StartingPoint.setText(trail.getStrartingPoin());
        MainSights.setText(trail.getMainSights());
        Tips.setText(trail.getTips());
        Distance.setText(String.valueOf(trail.getDistance()));
        KindOfTrail.setText(trail.getKindOfTrail().toString());
        DifficultyLevel.setText(trail.getDifficultyLevel().toString());
        ChildrenFriendly.setText(String.valueOf(trail.isChildren_Friedly()));
        OtherTransports.setText(trail.getOtherTransport());
        ConnectionToOtherTrails.setText(trail.getConnectionToOtherTrails());


        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        hsv  = (ScrollView) view.findViewById(R.id.scrollView);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.e("To split","asddsds");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        String toSplit = trail.getGeometryCollection();
        toSplit = toSplit.replaceAll("\\(", "");
        toSplit = toSplit.replaceAll("\\)", "");
        ArrayList<String> coordinates = new ArrayList<>();
        ArrayList<String> linestring = new ArrayList<>();
        ArrayList<String> point = new ArrayList<>();
       // Log.e("To split",""+first);
        String[] commatokens = toSplit.split(",");
        for (String commatoken : commatokens) {
            Log.e("commatoken","-" + commatoken + "-");
            coordinates.add(commatoken);
        }
        for (int i = 0; i < coordinates.size(); i++) {

            String[] tokens = coordinates.get(i).split("\\s");

            for (int j=0;j<tokens.length;j++ ) {
                String token=tokens[j];

               if(token.equals("POINT")){
                   point.add(tokens[j]);
                   point.add(tokens[j+1]);
                   point.add(tokens[j+2]);
                    break;
               }

               linestring.add(tokens[j]);

                Log.e("tokens",""+  tokens[j]);
            }


            //Log.e("tokens",""+  coordinates);
        }
        ArrayList<Double> filtredlinestring=filter(linestring);
        ArrayList<Double> filteredpoints=filter(point);
        ArrayList<LatLng> fullline = new ArrayList<>();
        ArrayList<LatLng> fullpoints = new ArrayList<>();
        for(int i=0;i<filtredlinestring.size();i++){
            fullline.add(new LatLng(filtredlinestring.get(i+1),filtredlinestring.get(i))); filtredlinestring.get(i);
            i++;
        }
        for(int i=0;i<filteredpoints.size();i++){
            fullpoints.add(new LatLng(filteredpoints.get(i+1),filteredpoints.get(i)));
            i++;
        }
        // list of latlng
        for (int i = 0; i < fullline.size() - 1; i++) {
            LatLng src = fullline.get(i);
            LatLng dest = fullline.get(i + 1);

            // mMap is the Map Object
            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude,dest.longitude)
                    ).width(5).color(Color.BLUE).geodesic(true)
            );
        }
        for(LatLng i :fullpoints ){
            mMap.addMarker(new MarkerOptions().position(i));
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(fullpoints.get(0) , 13.0f) );



        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        Log.e("mesa","dasdas");
                        hsv.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        hsv.requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return mMapView.onTouchEvent(event);
            }
        });
    }

    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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

    public ArrayList<Double> filter(ArrayList<String> lineling){
        ArrayList<Double> temp= new ArrayList<>();
        for(String i :lineling){
            if(!i.equals("POINT")&&!i.equals("GEOMETRYCOLLECTION")&&!i.equals("LINESTRING")&&!i.equals("")){
                temp.add(Double.parseDouble(i));
            }
        }
        return temp;
    }
}
