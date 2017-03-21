package gr.aegean.com.samostrails;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/13/2017.
 */

public class CreateTrailFragment extends Fragment {

    ArrayList<LatLng> Linestring;
    ArrayList<LatLng> Point;
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
    private Button SendTrail;
    private Button SaveTrail;
    ServicesClient client = null;
    SystemServices ss = null;

    public static CreateTrailFragment newInstance() {
        CreateTrailFragment fragment = new CreateTrailFragment();
        return fragment;
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
        Linestring = bundle.getParcelableArrayList("linestring");
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
        SendTrail = (Button) view.findViewById(R.id.sendtrail);
        SaveTrail = (Button) view.findViewById(R.id.savetraillocally);
        SaveTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrail();
            }
        });
        SendTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendtrail();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public String getGeometryCollectionFormat(ArrayList<LatLng> linestring) {

        StringBuilder sb = new StringBuilder();
        sb.append("GEOMETRYCOLLECTION (LINESTRING (");
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
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"title\":");
        sb.append("\"");
        sb.append(Title.getText());
        sb.append("\"");
        sb.append(",");
        sb.append("\"type\":");
        sb.append("\"");
        sb.append("trailstobechecked");
        sb.append("\"");
        sb.append(",");
        sb.append("\"field_leaflet_\":{\"und\":[{\"geom\":\"");
        sb.append(getGeometryCollectionFormat(Linestring));
        sb.append("\",");
        sb.append("\"geo_type\":\"geometrycollection\",");
        sb.append("\t\"lat\":\"37.783025098369\",\n" +
                "\t\t\t\t\t\"lon\":\"26.653555554974\",\n" +
                "\t\t\t\t\t\"left\":\"26.646817177534\",\n" +
                "\t\t\t\t\t\"top\":\"37.789154873322\",\n" +
                "\t\t\t\t\t\"right\":\"26.660293936729\",\n" +
                "\t\t\t\t\t\"bottom\":\"37.776895329526\",");
        sb.append("\t\"geohash\":\"swdyy\"}]},");
        sb.append("\"field_distance\":{\"und\":[{\"value\":\"");
        sb.append(Distance.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(Distance.getText());
        sb.append("\"}]},");
        sb.append("\"field_starting_point\":{\"und\":[{\"value\":\"");
        sb.append(StartingPoint.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(StartingPoint.getText());
        sb.append("\"}]},\n" +
                "\"field_main_sights\":{\"und\":[{\"value\":\"");
        sb.append(MainSights.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(MainSights.getText());
        sb.append("\"}]},\n" +
                "\"field_connection_to_other_trails\":{\"und\":[{\"value\":\"");
        sb.append(ConnectionToOtherTrails.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(ConnectionToOtherTrails.getText());
        sb.append("\"}]},");
        sb.append("\"field_other_transport\":{\"und\":[{\"value\":\"");
        sb.append(OtherTransports.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(OtherTransports.getText());
        sb.append("\"}]},");
        sb.append("\"field_discription\":{\"und\":[{\"value\":\"");
        sb.append(Description.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(Description.getText());
        sb.append("\"}]},\"field_tips\":{\"und\":[{\"value\":\"");
        sb.append(Tips.getText());
        sb.append("\",\"format\":null,\"safe_value\":\"");
        sb.append(Tips.getText());
        sb.append("\"}]},\n" +
                "\"field_img_gal\":[]}");

        return sb.toString();
    }

    public void sendtrail() throws JSONException {
        ss.connect(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject jbo = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    JSONObject js = jbo.getJSONObject("user");
                    jbo = js.getJSONObject("roles");

                    if (jbo.has("1")) {
                        Toast.makeText(getActivity(), "You Must login first To Submit Trails", Toast.LENGTH_LONG).show();

                    } else {
                        JSONObject json = new JSONObject(req());
                        client.post("node", json, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                                Toast.makeText(getActivity(), "Submitted Trail with Title " + Title.getText(), Toast.LENGTH_LONG).show();
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
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
            }
        });

    }

    public void saveTrail() {
        Trail trail = new Trail(getActivity());
        trail.setGeometryCollection(getGeometryCollectionFormat(Linestring));
        TrailDb.insertIntoDb(trail, TrailDb.initiateDB(getActivity()));
        Toast.makeText(getActivity(), "Your Trail have been saved Locally", Toast.LENGTH_LONG).show();
        Fragment fragment = RecordingFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();

    }

    public void onPause() {
        super.onPause();
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }
}
