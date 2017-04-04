package gr.aegean.com.samostrails;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

import static android.content.ContentValues.TAG;


public class CreateTrailFragment extends Fragment implements OnMapReadyCallback {

    ArrayList<LatLng> Linestring;
    private GoogleMap mMap;
    MapView mMapView;
    private Button backbutton;
    private TextInputEditText Title;
    private TextInputEditText Description;
    private TextInputEditText StartingPoint;
    private TextInputEditText MainSights;
    private TextInputEditText Tips;
    private TextInputEditText Distance;
    private TextInputEditText OtherTransports;
    private TextInputEditText ConnectionToOtherTrails;
    private RadioGroup KindOfTrail;
    private RadioGroup DifficultyLevel;
    private RadioGroup ChildrenFriendly;
    ServicesClient client = null;
    SystemServices ss = null;
    Button sendTrail;
    Trail  trail;
    boolean local;
    public static CreateTrailFragment newInstance() {
        return new CreateTrailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_trail_fragment, container, false);
        Bundle bundle = getArguments();

        client = ((MainActivity) getActivity()).getServicesClient();
        ss = new SystemServices(client);
        local=bundle.getBoolean("local");
        trail=bundle.getParcelable("trail");
        Linestring = bundle.getParcelableArrayList("linestring");
        backbutton = (Button) view.findViewById(R.id.backbutton);
        Title = (TextInputEditText) view.findViewById(R.id.title_input);
        Description = (TextInputEditText) view.findViewById(R.id.descriptioninput);
        StartingPoint = (TextInputEditText) view.findViewById(R.id.startingpointinput);
        MainSights = (TextInputEditText) view.findViewById(R.id.mainsightsinput);
        Tips = (TextInputEditText) view.findViewById(R.id.tipsinput);
        Distance = (TextInputEditText) view.findViewById(R.id.distanceinput);
        OtherTransports = (TextInputEditText) view.findViewById(R.id.othertransportinput);
        ConnectionToOtherTrails = (TextInputEditText) view.findViewById(R.id.connectiontoothertrailsinput);
        KindOfTrail = (RadioGroup) view.findViewById(R.id.kindoftrailinput);
        DifficultyLevel = (RadioGroup) view.findViewById(R.id.difficultylevelinput);
        ChildrenFriendly = (RadioGroup) view.findViewById(R.id.childrenfriendlyinput);
         sendTrail = (Button) view.findViewById(R.id.sendtrail);
        Button saveTrail = (Button) view.findViewById(R.id.savetraillocally);
        if(local){
            Title.setText(trail.getTitle());
            Description.setText(trail.getDescription());
            StartingPoint.setText(trail.getStrartingPoin());
            MainSights.setText(trail.getMainSights());
            Tips.setText(trail.getTips());
            Distance.setText(Double.toString(trail.getDistance()));
            OtherTransports.setText(trail.getOtherTransport());
            ConnectionToOtherTrails.setText(trail.getConnectionToOtherTrails());
            KindOfTrail.check(trail.getKindOfTrail()==gr.aegean.com.samostrails.Models.KindOfTrail.Loop?R.id.oneway:R.id.loop);
            DifficultyLevel.check(trail.getDifficultyLevel()==gr.aegean.com.samostrails.Models.DifficultyLevel.Easy?R.id.easy:
                    trail.getDifficultyLevel()==gr.aegean.com.samostrails.Models.DifficultyLevel.Moderate?R.id.moderate:
                            trail.getDifficultyLevel()==gr.aegean.com.samostrails.Models.DifficultyLevel.Challenging?R.id.challenging:
                                    trail.getDifficultyLevel()==gr.aegean.com.samostrails.Models.DifficultyLevel.Sport?R.id.sport:R.id.extreme);
            ChildrenFriendly.check(trail.getChildrenFriendly()?R.id.yes:R.id.no);
        }else{
            DecimalFormat df = new DecimalFormat("#.##");
            Distance.setText(String.valueOf(df.format(bundle.getDouble("distance"))));
        }
        saveTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrail();
            }
        });
        sendTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Utilities.isNetworkAvailable(getActivity())) {
                        sendtrail();
                    }else{
                        Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        mMapView = (MapView) view.findViewById(R.id.mapcreatetable);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    public String getGeometryCollectionFormat(ArrayList<LatLng> linestring) {

        StringBuilder sb = new StringBuilder();
        if (linestring.size()>1)
            sb.append("GEOMETRYCOLLECTION (LINESTRING (");
        else
            sb.append("GEOMETRYCOLLECTION (POINT (");

        for (int i = 0; i < linestring.size(); i++) {
            LatLng l = linestring.get(i);
            sb.append(l.longitude);
            sb.append(" ");
            sb.append(l.latitude);
            if (i != linestring.size() - 1)
                sb.append(",");
        }
        sb.append("),");
        sb.append("POINT (");
        sb.append(linestring.get(0).longitude);
        sb.append(" ");
        sb.append(linestring.get(0).latitude);
        sb.append(")");
        sb.append(",");
        sb.append("POINT (");
        sb.append(linestring.get(linestring.size() - 1).longitude);
        sb.append(" ");
        sb.append(linestring.get(linestring.size() - 1).latitude);
        sb.append(")");
        return sb.toString();

    }

    public String req() {
        int radioButtonID = ChildrenFriendly.getCheckedRadioButtonId();
        View radioButton = ChildrenFriendly.findViewById(radioButtonID);
        int ChildrenFriendlyin = ChildrenFriendly.indexOfChild(radioButton);
        int radioButtonIDDi= DifficultyLevel.getCheckedRadioButtonId();
        View radioButtonDif = DifficultyLevel.findViewById(radioButtonIDDi);
        int Diff = DifficultyLevel.indexOfChild(radioButtonDif);

        int radioButtonIDkind= KindOfTrail.getCheckedRadioButtonId();
        View radioButtonkind = KindOfTrail.findViewById(radioButtonIDkind);
        int kind = KindOfTrail.indexOfChild(radioButtonkind);
        return "{" +
                "\"title\":" +
                "\"" +
                Title.getText().toString() +
                "\"" +
                "," +
                "\"type\":" +
                "\"" +
                "trailstobechecked" +
                "\"" +
                "," +
                "\"field_leaflet_\":{\"und\":[{\"geom\":\"" +
                getGeometryCollectionFormat(Linestring) +
                "\"," +
                "\"geo_type\":\"geometrycollection\"," +
                "\t\"lat\":\"37.783025098369\",\n" +
                "\t\t\t\t\t\"lon\":\"26.653555554974\",\n" +
                "\t\t\t\t\t\"left\":\"26.646817177534\",\n" +
                "\t\t\t\t\t\"top\":\"37.789154873322\",\n" +
                "\t\t\t\t\t\"right\":\"26.660293936729\",\n" +
                "\t\t\t\t\t\"bottom\":\"37.776895329526\"," +
                "\t\"geohash\":\"swdyy\"}]}," +
                "\"field_distance\":{\"und\":[{\"value\":\"" +
                Distance.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                Distance.getText() +
                "\"}]}," +
                "\"field_starting_point\":{\"und\":[{\"value\":\"" +
                StartingPoint.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                StartingPoint.getText() +
                "\"}]},\n" +
                "\"field_main_sights\":{\"und\":[{\"value\":\"" +
                MainSights.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                MainSights.getText() +
                "\"}]},\n" +
                "\"field_connection_to_other_trails\":{\"und\":[{\"value\":\"" +
                ConnectionToOtherTrails.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                ConnectionToOtherTrails.getText() +
                "\"}]}," +
                "\"field_other_transport\":{\"und\":[{\"value\":\"" +
                OtherTransports.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                OtherTransports.getText() +
                "\"}]}," +
                "\"field_discription\":{\"und\":[{\"value\":\"" +
                Description.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                Description.getText() +
                "\"}]},\"field_tips\":{\"und\":[{\"value\":\"" +
                Tips.getText() +
                "\",\"format\":null,\"safe_value\":\"" +
                Tips.getText() +
                "\"}]},\n" +
                "\"field_img_gal\":[],\"field_difficulty_level\":{\"und\":{\"value\":\""+(Diff+1)+"\"}},\"field_kind_of_trail\":{\"und\":{\"value\":\""+(kind+1)+"\"}},\"field_children_friedly\":{\"und\":{\"value\":\""+(ChildrenFriendlyin+1)+"\"}}}";
    }

    public void sendtrail() throws JSONException {
        sendTrail.setEnabled(false);
                ss.connect(new AsyncHttpResponseHandler() {
                                       @Override
                                       public void onSuccess ( int statusCode, Header[] headers,byte[] responseBody){

                                           try {
                                               JSONObject jbo = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                                               JSONObject js = jbo.getJSONObject("user");
                                               jbo = js.getJSONObject("roles");

                                               if (jbo.has("1")) {
                                                   sendTrail.setEnabled(true);
                                                   Toast.makeText(getActivity(), "You Must login first To Submit Trails", Toast.LENGTH_LONG).show();

                                               } else {
                                                   JSONObject json = new JSONObject(req());
                                                   Log.e("json", "" + json);
                                                   client.post("node", json, new AsyncHttpResponseHandler() {
                                                       @Override
                                                       public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                           Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                                                           sendTrail.setEnabled(true);
                                                           Toast.makeText(getActivity(), "Submitted Trail with Title " + Title.getText(), Toast.LENGTH_LONG).show();
                                                           if(!local) {
                                                               Fragment fragment = RecordingFragment.newInstance();
                                                               FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                               transaction.replace(R.id.content, fragment);
                                                               transaction.commit();
                                                           }else{
                                                               Fragment fragment = LocalTrailsFragment.newInstance();
                                                               FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                               transaction.replace(R.id.content, fragment);
                                                               transaction.commit();
                                                           }
                                                       }

                                                       @Override
                                                       public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                           Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                                                       }
                                                   });
                                               }

                                           } catch (JSONException e) {
                                               Log.e("JSON parse error: ", "" + e.getMessage());
                                           }

                                       }

                                       @Override
                                       public void onFailure ( int statusCode, Header[] headers,
                                                               byte[] responseBody, Throwable error){
                                           // Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                                       }
                                   });
        sendTrail.setEnabled(true);

    }

    public void saveTrail() {
        int radioButtonID = ChildrenFriendly.getCheckedRadioButtonId();
        View radioButton = ChildrenFriendly.findViewById(radioButtonID);
        int ChildrenFriendlyin = ChildrenFriendly.indexOfChild(radioButton);
        int radioButtonIDDi= DifficultyLevel.getCheckedRadioButtonId();
        View radioButtonDif = DifficultyLevel.findViewById(radioButtonIDDi);
        int Diff = DifficultyLevel.indexOfChild(radioButtonDif);
        int radioButtonIDkind= KindOfTrail.getCheckedRadioButtonId();
        View radioButtonkind = KindOfTrail.findViewById(radioButtonIDkind);
        int kind = KindOfTrail.indexOfChild(radioButtonkind);
        if(!local) {
            Trail trail2 = new Trail(getActivity());
            trail2.setTitle(Title.getText().toString());
            trail2.setGeometryCollection(getGeometryCollectionFormat(Linestring));
            trail2.setEditable(true);
            trail2.setChildren_Friedly(ChildrenFriendlyin==0);
            trail2.setConnectionToOtherTrails(ConnectionToOtherTrails.getText().toString());
            trail2.setDescription(Description.getText().toString());
            trail2.setDifficultyLevel(Diff==0? gr.aegean.com.samostrails.Models.DifficultyLevel.Easy:
                    Diff==1?gr.aegean.com.samostrails.Models.DifficultyLevel.Moderate:
                            Diff==2?gr.aegean.com.samostrails.Models.DifficultyLevel.Challenging:
                                    Diff==3?gr.aegean.com.samostrails.Models.DifficultyLevel.Sport:gr.aegean.com.samostrails.Models.DifficultyLevel.Extreme);
            String tmp = Distance.getText().toString().replaceAll(",",".");
            trail2.setDistance(Distance.getText().toString().equals("")?0:Double.parseDouble(tmp));
            trail2.setKindOfTrail(kind==0?gr.aegean.com.samostrails.Models.KindOfTrail.OneWay:gr.aegean.com.samostrails.Models.KindOfTrail.Loop);
            trail2.setMainSights(MainSights.getText().toString());
            trail2.setOtherTransport(OtherTransports.getText().toString());
            trail2.setStrartingPoin(StartingPoint.getText().toString());
            trail2.setTips(Tips.getText().toString());
            TrailDb.insertIntoDb(trail2, TrailDb.initiateDB(getActivity()));
            Toast.makeText(getActivity(), "Your Trail have been saved Locally", Toast.LENGTH_LONG).show();
            Fragment fragment = RecordingFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
        }else{
            trail.setTitle(Title.getText().toString());
            trail.setChildren_Friedly(ChildrenFriendlyin==0);
            trail.setConnectionToOtherTrails(ConnectionToOtherTrails.getText().toString());
            trail.setDescription(Description.getText().toString());
            trail.setDifficultyLevel(Diff==0? gr.aegean.com.samostrails.Models.DifficultyLevel.Easy:
                    Diff==1?gr.aegean.com.samostrails.Models.DifficultyLevel.Moderate:
                            Diff==2?gr.aegean.com.samostrails.Models.DifficultyLevel.Challenging:
                                    Diff==3?gr.aegean.com.samostrails.Models.DifficultyLevel.Sport:gr.aegean.com.samostrails.Models.DifficultyLevel.Extreme);
            trail.setDistance(Distance.getText().toString().equals("")?0:Double.parseDouble(Distance.getText().toString()));
            trail.setKindOfTrail(kind==0?gr.aegean.com.samostrails.Models.KindOfTrail.OneWay:gr.aegean.com.samostrails.Models.KindOfTrail.Loop);
            trail.setMainSights(MainSights.getText().toString());
            trail.setOtherTransport(OtherTransports.getText().toString());
            trail.setStrartingPoin(StartingPoint.getText().toString());
            trail.setTips(Tips.getText().toString());
            TrailDb.updateDb(trail, TrailDb.initiateDB(getActivity()));
            Toast.makeText(getActivity(), "Your Trail have been saved Locally", Toast.LENGTH_LONG).show();
            Fragment fragment = LocalTrailsFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
        }


    }

    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {


        // list of latlng
        for (int i = 0; i < Linestring.size() - 1; i++) {
            LatLng src = Linestring.get(i);
            LatLng dest = Linestring.get(i + 1);
            // mMap is the Map Object
            mMap.addPolyline(new PolylineOptions().add(
                    new LatLng(src.latitude, src.longitude),
                    new LatLng(dest.latitude,dest.longitude)
            ).width(5).color(Color.BLUE).geodesic(true));
        }

            mMap.addMarker(new MarkerOptions().position(Linestring.get(0)).position(Linestring.get(Linestring.size()-1)));

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(Linestring.get(0) , 14.0f) );
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
}
