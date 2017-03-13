package gr.aegean.com.samostrails;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gr.aegean.com.samostrails.API.HttpHandler;
import gr.aegean.com.samostrails.Adapters.AdapterSwipeRefresh;
import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/13/2017.
 */

public class CreateTrailFragment extends Fragment{
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
    String url="http://test.samostrails.com/api/node";
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
        View view= inflater.inflate(R.layout.create_trail_fragment, container, false);
        Bundle bundle = getArguments();

        Linestring = bundle.getParcelableArrayList("linestring");
        Log.e("Linestring:",""+Linestring);
        Title = (TextInputEditText) view.findViewById(R.id.title_input);
        Description= (TextInputEditText) view.findViewById(R.id.descriptioninput);
        StartingPoint= (TextInputEditText) view.findViewById(R.id.startingpointinput);
        MainSights= (TextInputEditText) view.findViewById(R.id.mainsightsinput);
        Tips= (TextInputEditText) view.findViewById(R.id.tipsinput);
        Distance= (TextInputEditText) view.findViewById(R.id.distanceinput);
        OtherTransports= (TextInputEditText) view.findViewById(R.id.othertransportinput);
        ConnectionToOtherTrails= (TextInputEditText) view.findViewById(R.id.connectiontoothertrailsinput);
        KindOfTrail = (RadioGroup) view.findViewById(R.id.kindoftrailinput);
        DifficultyLevel= (RadioGroup) view.findViewById(R.id.difficultylevelinput);
        ChildrenFriendly= (RadioGroup) view.findViewById(R.id.childrenfriendlyinput);
        SendTrail = (Button) view.findViewById(R.id.sendtrail);

        SendTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Button:","clicked");
                sendtrail();
            }
        });
        return view;
    }

    public String getGeometryCollectionFormat(ArrayList<LatLng> linestring,ArrayList<LatLng> point){

        StringBuilder sb = new StringBuilder();
        sb.append("GEOMETRYCOLLECTION (LINESTRING (");
        for(int i =0;i<linestring.size();i++){
            LatLng l = linestring.get(i);
            sb.append(l.latitude);
            sb.append(" ");
            sb.append(l.longitude);
            if(i!=linestring.size()-1)
            sb.append(",");
        }
        sb.append("),");

            sb.append("POINT (");
            sb.append(linestring.get(0).latitude);
            sb.append(" ");
            sb.append(linestring.get(0).longitude);
            sb.append(")");
        sb.append(",");
        sb.append("POINT (");
        sb.append(linestring.get(linestring.size()-1).latitude);
        sb.append(" ");
        sb.append(linestring.get(linestring.size()-1).longitude);
        sb.append(")");


        return sb.toString();

    }
    public String req(){
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
        sb.append(getGeometryCollectionFormat(Linestring,Point));
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

    public void sendtrail(){
        HttpHandler http = new HttpHandler();
        http.makeServiceCallPostCreate(url,req());
    }
}