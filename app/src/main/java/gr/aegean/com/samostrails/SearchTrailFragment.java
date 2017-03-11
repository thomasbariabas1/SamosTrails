package gr.aegean.com.samostrails;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Movie;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gr.aegean.com.samostrails.API.GetTrails;
import gr.aegean.com.samostrails.API.HttpHandler;
import gr.aegean.com.samostrails.Adapters.AdapterSwipeRefresh;
import gr.aegean.com.samostrails.Adapters.AdapterTrails;
import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

import static android.R.attr.fragment;
import static android.content.ContentValues.TAG;


public class SearchTrailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        lv = (GridView) view.findViewById (R.id.gridview);
        nofoundimage = (ImageView) view.findViewById(R.id.nofoundimage);
        nointernetfound= (TextView) view.findViewById(R.id.nointernetfount);

        swipeRefreshLayout.setOnRefreshListener(this);


        if(Utilities.isNetworkAvailable(getActivity())) {
            lv.setVisibility(View.VISIBLE);
            nofoundimage.setVisibility(View.GONE);
            nointernetfound.setVisibility(View.GONE);

                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                fetchTrails();

                                            }
                                        }
                );

        }else{
            lv.setVisibility(View.INVISIBLE);
            nofoundimage.setVisibility(View.VISIBLE);
            nointernetfound.setVisibility(View.VISIBLE);
        }
       // TrailDb.deleteAll(TrailDb.initiateDB(getActivity()));
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
        return view;
    }

    @Override
    public void onRefresh() {

            if (Utilities.isNetworkAvailable(getActivity())) {

                lv.setVisibility(View.VISIBLE);
                nofoundimage.setVisibility(View.GONE);
                nointernetfound.setVisibility(View.GONE);
                

                    fetchTrails();

            } else {
                lv.setVisibility(View.INVISIBLE);
                nofoundimage.setVisibility(View.VISIBLE);
                nointernetfound.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }




    private void fetchTrails() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        if (response!=null) {

                            JSONArray trails = null;
                            // Getting JSON Array node
                            try {
                                trails= response.getJSONArray("nodes");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            TrailsArray.clear();
                            // looping through json and adding to movies list
                            for (int i = 0; i < trails.length(); i++) {
                                try {
                                    JSONObject z = trails.getJSONObject(i);
                                    JSONObject c = z.getJSONObject("node");
                                    JSONObject image = c.getJSONObject("Image");

                                    TrailsArray.add(new Trail(!c.getString("Children Friedly").equals("No") , Integer. parseInt(c.getString("Entity ID")),
                                            DifficultyLevel.valueOf(c.getString("Difficulty Level")), DistanceLevel.valueOf(c.getString("Distance Level").equals("Long (>3km)") ? "Long" : "Short"),
                                            KindOfTrail.valueOf(c.getString("Kind of trail").equals("One Way") ? "OneWay" : "Loop"), image.getString("src").replace("\\", ""), c.getString("Leaflet"),
                                            Double.parseDouble(c.getString("Distance").replaceAll("\\D+", "")), c.getString("Title"),c.getString("CONNECTION TO OTHER TRAILS"),
                                            c.getString("Description"),c.getString("MAIN SIGHTS"),c.getString("Other Transport"),c.getString("STARTING POINT"),c.getString("Tips"),c.getString("Video")));





                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }


                            lv.setAdapter(new AdapterSwipeRefresh(getActivity(), TrailsArray));


                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        // Adding request to request queue
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(req);
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
