package gr.aegean.com.samostrails;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gr.aegean.com.samostrails.API.HttpHandler;
import gr.aegean.com.samostrails.Adapters.AdapterTrails;
import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

import static android.R.attr.fragment;
import static android.content.ContentValues.TAG;


public class SearchTrailFragment extends Fragment{
    private ProgressDialog pDialog;
    private GridView lv;
    private ImageView nofoundimage;
    private TextView nointernetfound;
    private static String url = "http://test.samostrails.com/trail-webservice";
    ArrayList<Trail> TrailsArray = new ArrayList<>();
    public static SearchTrailFragment newInstance() {
        SearchTrailFragment fragment = new SearchTrailFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.explore, container, false);
        // Inflate the layout for this fragment

        lv = (GridView) view.findViewById (R.id.gridview);
        nofoundimage = (ImageView) view.findViewById(R.id.nofoundimage);
        nointernetfound= (TextView) view.findViewById(R.id.nointernetfount);

        if(Utilities.isNetworkAvailable(getActivity())) {
            lv.setVisibility(View.VISIBLE);
            nofoundimage.setVisibility(View.GONE);
            nointernetfound.setVisibility(View.GONE);
            if(TrailsArray.size()==0) {
                new GetTrails().execute();
            }else{
                initiate();
            }
        }else{
            lv.setVisibility(View.INVISIBLE);
            nofoundimage.setVisibility(View.VISIBLE);
            nointernetfound.setVisibility(View.VISIBLE);
        }
       // TrailDb.deleteAll(TrailDb.initiateDB(getActivity()));
        return view;
    }
    private class GetTrails extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);


            if (jsonStr != null) try {

                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray trails = jsonObj.getJSONArray("nodes");


                // looping through All Contacts
                for (int i = 0; i < trails.length(); i++) {
                    JSONObject z = trails.getJSONObject(i);
                    JSONObject c = z.getJSONObject("node");
                    JSONObject image = c.getJSONObject("Image");

                    TrailsArray.add(new Trail(!c.getString("Children Friedly").equals("No") , Integer. parseInt(c.getString("Entity ID")),
                            DifficultyLevel.valueOf(c.getString("Difficulty Level")), DistanceLevel.valueOf(c.getString("Distance Level").equals("Long (>3km)") ? "Long" : "Short"),
                            KindOfTrail.valueOf(c.getString("Kind of trail").equals("One Way") ? "OneWay" : "Loop"), image.getString("src").replace("\\", ""), c.getString("Leaflet"),
                            Double.parseDouble(c.getString("Distance").replaceAll("\\D+", "")), c.getString("Title"),c.getString("CONNECTION TO OTHER TRAILS"),
                            c.getString("Description"),c.getString("MAIN SIGHTS"),c.getString("Other Transport"),c.getString("STARTING POINT"),c.getString("Tips"),c.getString("Video")));




                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());


            }
            else {
                Log.e(TAG, "Couldn't get json from server.");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            initiate();



        }

    }


    public void initiate(){

        AdapterTrails adbTrails;


//then populate myListItems


        adbTrails= new AdapterTrails (getActivity(),  TrailsArray);

        lv.setAdapter(adbTrails);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putParcelable("trail",TrailsArray.get(position));
                Fragment fragment = TrailInfoFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();

            }
        });
    }
    public void onResume() {
        Log.e("DEBUG", "onResume of Search");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of Search");
        super.onPause();
    }
}
