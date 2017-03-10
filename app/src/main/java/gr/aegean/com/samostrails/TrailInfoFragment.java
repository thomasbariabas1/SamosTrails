package gr.aegean.com.samostrails;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import gr.aegean.com.samostrails.SQLDb.TrailDb;


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
    private Button SaveTrail;
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
        SaveTrail = (Button) view.findViewById(R.id.savetrail);
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
        if (TrailDb.ifExists(trail,TrailDb.initiateDB(getActivity()))){
          SaveTrail.setVisibility(View.GONE);
        }
        SaveTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TrailDb.ifExists(trail,TrailDb.initiateDB(getActivity()))){
                    TrailDb.insertIntoDb(trail, TrailDb.initiateDB(getActivity()));
            }else{
                    Toast.makeText(getActivity(),"Trail is already in your collection!!",Toast.LENGTH_LONG).show();

                }
            }
        });
        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) view.findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        hsv  = (ScrollView) view.findViewById(R.id.sv);
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v,MotionEvent ev) {
                Log.e("inside event","-------------------------");
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("inside event","-------------------------");
                        ((ScrollView) v.findViewById(R.id.sv)).requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.e("inside event","-------------------------");
                        ((ScrollView) v.findViewById(R.id.sv)).requestDisallowInterceptTouchEvent(false);
                        break;
                }


                return true;
            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
           // Log.e("commatoken","-" + commatoken + "-");
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

              //  Log.e("tokens",""+  tokens[j]);
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
             mMap.addPolyline(new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude,dest.longitude)
                    ).width(5).color(Color.BLUE).geodesic(true));
        }
        for(LatLng i :fullpoints ){
            mMap.addMarker(new MarkerOptions().position(i));
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(fullpoints.get(1) , 13.0f) );




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
