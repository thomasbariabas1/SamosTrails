package gr.aegean.com.samostrails;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;


public class TrailInfoFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    MapView mMapView;
    private Trail trail;
    private Button SaveTrail;
    private Button DeleteTrail;
    ArrayList<LatLng> fullline = new ArrayList<>();
    ArrayList<LatLng> fullpoints = new ArrayList<>();
    ScrollView hsv;
    View view;
    public static TrailInfoFragment newInstance() {
        return new TrailInfoFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.trail_info, container, false);
        ImageView transparentImageView = (ImageView) view.findViewById(R.id.transparent_image);
        // Inflate the layout for this fragment
        TextView trailTitle = (TextView) view.findViewById(R.id.trail_title);

        Button backbutton = (Button) view.findViewById(R.id.backbutton);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView startingPoint = (TextView) view.findViewById(R.id.startingpoint);
        TextView mainSights = (TextView) view.findViewById(R.id.mainsights);
        TextView tips = (TextView) view.findViewById(R.id.tips);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        TextView kindOfTrail = (TextView) view.findViewById(R.id.kindoftrail);
        TextView difficultyLevel = (TextView) view.findViewById(R.id.difficultylevel);
        TextView childrenFriendly = (TextView) view.findViewById(R.id.childrenfriendly);
        TextView otherTransports = (TextView) view.findViewById(R.id.othertransport);
        TextView connectionToOtherTrails = (TextView) view.findViewById(R.id.connectiontoothertrails);
        Button deleteRecordedTrails = (Button) view.findViewById(R.id.deleterecordedtrail);
        SaveTrail = (Button) view.findViewById(R.id.savetrail);
        DeleteTrail = (Button) view.findViewById(R.id.deletetrail);
        Button startRecordedTrails = (Button) view.findViewById(R.id.startrecordedtrails);
        DeleteTrail.setVisibility(View.GONE);
        Button startTrail = (Button) view.findViewById(R.id.start_trail);
        Button editTrail = (Button) view.findViewById(R.id.edit_trail);
        final Bundle bundle = getArguments();
        trail = bundle.getParcelable("trail");
        editTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putParcelableArrayList("linestring", fullline);
                bundle.putParcelable("trail", trail);
                Fragment fragment = CreateTrailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SearchTrailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        backbutton.setVisibility(View.INVISIBLE);
        startRecordedTrails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("trailtitle", trail.getTrailId());
                bundle.putParcelableArrayList("lines", fullline);
                bundle.putParcelableArrayList("points", fullpoints);
                bundle.putParcelable("trail", trail);
                Fragment fragment = StartTrailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        startTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("trailtitle", trail.getTrailId());
                bundle.putParcelableArrayList("lines", fullline);
                bundle.putParcelableArrayList("points", fullpoints);
                bundle.putParcelable("trail", trail);
                Fragment fragment = StartTrailFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if (!((MainActivity)getActivity()).isFirstTime()) {
            if (!(trail.getTitle().equals(((MainActivity) getActivity()).hasStartedTrail())|| ((MainActivity) getActivity()).hasStartedTrail().equals(""))){
                startTrail.setEnabled(false);
                Toast.makeText(getActivity(), "You have already Started one trail with title:"+((MainActivity) getActivity()).hasStartedTrail(), Toast.LENGTH_LONG).show();
            }else
                startTrail.setEnabled(true);
        } else {
            startTrail.setEnabled(true);
        }
        trailTitle.setText(trail.getTitle());
        description.setText(trail.getDescription());
        startingPoint.setText(trail.getStrartingPoin());
        mainSights.setText(trail.getMainSights());
        tips.setText(trail.getTips());
        distance.setText(String.valueOf(trail.getDistance()));
        kindOfTrail.setText(trail.getKindOfTrail().toString());
        difficultyLevel.setText(trail.getDifficultyLevel().toString());
        childrenFriendly.setText(String.valueOf(trail.isChildren_Friedly()));
        otherTransports.setText(trail.getOtherTransport());
        connectionToOtherTrails.setText(trail.getConnectionToOtherTrails());
        if (TrailDb.ifExists(trail, TrailDb.initiateDB(getActivity()))) {
            SaveTrail.setVisibility(View.GONE);
            DeleteTrail.setVisibility(View.VISIBLE);
        }
        if (trail.isEditable()) {
            startTrail.setVisibility(View.GONE);
            SaveTrail.setVisibility(View.GONE);
            DeleteTrail.setVisibility(View.GONE);
            editTrail.setVisibility(View.VISIBLE);
            startRecordedTrails.setVisibility(View.VISIBLE);

            startRecordedTrails.setEnabled(true);
            deleteRecordedTrails.setVisibility(View.VISIBLE);
        }
        deleteRecordedTrails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                 builder.setTitle("Delete Trail");

                builder.setMessage("Do you want to Delete Record?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TrailDb.deleteRecord(trail, TrailDb.initiateDB(getActivity()));
                        Fragment fragment = LocalTrailsFragment.newInstance();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }


        });
        SaveTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TrailDb.ifExists(trail, TrailDb.initiateDB(getActivity()))) {
                    TrailDb.insertIntoDb(trail, TrailDb.initiateDB(getActivity()));
                    SaveTrail.setVisibility(View.GONE);
                    DeleteTrail.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Trail is already in your collection!!", Toast.LENGTH_LONG).show();

                }
            }
        });
        DeleteTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trail.isEditable()) {
                    TrailDb.deleteRecord(trail, TrailDb.initiateDB(getActivity()));
                } else {
                    TrailDb.delete(trail, TrailDb.initiateDB(getActivity()));
                }
                SaveTrail.setVisibility(View.VISIBLE);
                DeleteTrail.setVisibility(View.GONE);
            }
        });
        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.setClickable(false);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        hsv = (ScrollView) view.findViewById(R.id.sv);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        hsv.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        hsv.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        hsv.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
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
        String[] commatokens = toSplit.split(",");

        for (String commatoken : commatokens) {
            coordinates.add(commatoken);
        }
        for (int i = 0; i < coordinates.size(); i++) {

            String[] tokens = coordinates.get(i).split("\\s");

            for (int j = 0; j < tokens.length; j++) {
                String token = tokens[j];

                if (token.equals("POINT")) {
                    point.add(tokens[j]);
                    point.add(tokens[j + 1]);
                    point.add(tokens[j + 2]);
                    break;
                }

                linestring.add(tokens[j]);
            }
        }
        ArrayList<Double> filtredlinestring = filter(linestring);
        ArrayList<Double> filteredpoints = filter(point);
        for (int i = 0; i < filtredlinestring.size(); i++) {
            fullline.add(new LatLng(filtredlinestring.get(i + 1), filtredlinestring.get(i)));
            filtredlinestring.get(i);
            i++;
        }
        for (int i = 0; i < filteredpoints.size(); i++) {
            fullpoints.add(new LatLng(filteredpoints.get(i + 1), filteredpoints.get(i)));
            i++;
        }
        // list of latlng
        for (int i = 0; i < fullline.size() - 1; i++) {
            LatLng src = fullline.get(i);
            LatLng dest = fullline.get(i + 1);

            // mMap is the Map Object
            mMap.addPolyline(new PolylineOptions().add(
                    new LatLng(src.latitude, src.longitude),
                    new LatLng(dest.latitude, dest.longitude)
            ).width(5).color(Color.BLUE).geodesic(true));
        }
        for (LatLng i : fullpoints) {
            mMap.addMarker(new MarkerOptions().position(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fullpoints.get(0), 14.0f));

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

    public ArrayList<Double> filter(ArrayList<String> lineling) {
        ArrayList<Double> temp = new ArrayList<>();
        for (String i : lineling) {
            if (!i.equals("POINT") && !i.equals("GEOMETRYCOLLECTION") && !i.equals("LINESTRING") && !i.equals("")) {
                temp.add(Double.parseDouble(i));
            }
        }
        return temp;
    }



    }
