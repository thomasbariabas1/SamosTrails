package gr.aegean.com.samostrails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.Adapters.AdapterSwipeRefresh;
import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;

import static android.content.ContentValues.TAG;


public class SearchTrailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomGridView gridView;
    private ImageView nofoundimage;
    private TextView nointernetfound;
    private SearchView sv;
    private static String url = "http://www.samostrails.com/samostrails/trail-webservice";
    public boolean firsttime=true;
    boolean isActive=true;
    AsyncHttpClient client = new AsyncHttpClient();
    ArrayList<Trail> TrailsArray = new ArrayList<>();
    int i = 0;

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
        View view = inflater.inflate(R.layout.search_trail_fragment, container, false);


        // Inflate the layout for this fragment
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        gridView = (CustomGridView) view.findViewById(R.id.gridview);
        gridView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = gridView.computeVerticalScrollOffset();
                if(scrollY == 0) swipeRefreshLayout.setEnabled(true);
                else swipeRefreshLayout.setEnabled(false);

            }
        });
        nofoundimage = (ImageView) view.findViewById(R.id.nofoundimage);
        nointernetfound = (TextView) view.findViewById(R.id.nointernetfount);
        sv = (SearchView) view.findViewById(R.id.searchview);
        sv.setQueryHint("Search Trails");
        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    if (i != 0)
                        onRefresh();
                }
            }
        });


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);


        if (Utilities.isNetworkAvailable(getActivity())) {
            gridView.setVisibility(View.VISIBLE);
            nofoundimage.setVisibility(View.GONE);
            nointernetfound.setVisibility(View.GONE);
            if(firsttime)
            fetchTrails();

        } else {
            gridView.setVisibility(View.INVISIBLE);
            nofoundimage.setVisibility(View.VISIBLE);
            nointernetfound.setVisibility(View.VISIBLE);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                Trail trail = TrailsArray.get(position);
                trail.setDownlImage(null);
                Log.e("trail",""+trail.getDownlImage());
                bundle.putParcelable("trail",trail );
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

            gridView.setVisibility(View.VISIBLE);
            nofoundimage.setVisibility(View.GONE);
            nointernetfound.setVisibility(View.GONE);


            fetchTrails();

        } else {
            gridView.setVisibility(View.INVISIBLE);
            nofoundimage.setVisibility(View.VISIBLE);
            nointernetfound.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private void fetchTrails() {
        firsttime=false;

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));

                    JSONArray trails = null;
                    // Getting JSON Array node
                    try {
                        trails = response.getJSONArray("nodes");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TrailsArray.clear();
                    // looping through json and adding to movies list
                    assert trails != null;
                    for (int i = 0; i < trails.length(); i++) {
                        try {
                            JSONObject z = trails.getJSONObject(i);
                            JSONObject c = z.getJSONObject("node");
                            JSONObject image = c.getJSONObject("Image");

                            TrailsArray.add(new Trail(!c.getString("Children Friedly").equals("No"), Integer.parseInt(c.getString("Vid")),
                                    DifficultyLevel.valueOf(c.getString("Difficulty Level")), DistanceLevel.valueOf(c.getString("Distance Level").equals("Long (>3km)") ? "Long" : "Short"),
                                    KindOfTrail.valueOf(c.getString("Kind of trail").equals("One Way") ? "OneWay" : "Loop"), image.getString("src").replace("\\", ""), c.getString("Leaflet_trails").equals("no")?c.getString("Leaflet _trailstobechecked"):c.getString("Leaflet_trails"),
                                    Double.parseDouble(c.getString("Distance").replaceAll(",",".")), c.getString("Title"), c.getString("CONNECTION TO OTHER TRAILS"),
                                    c.getString("Description"), c.getString("MAIN SIGHTS"), c.getString("Other Transport"), c.getString("STARTING POINT"), c.getString("Tips"), c.getString("Video"),"http://www.samostrails.com"+c.getString("Path")));

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    if(isActive)
                    gridView.setAdapter(new AdapterSwipeRefresh(getActivity(), TrailsArray, ((MainActivity) getActivity()).getCache()));


                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "Server Error: " + error.getMessage());


                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });




    }

    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setEnabled(true);
        isActive=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        isActive=false;
    }

    public void search(String searchword) {
        ArrayList<Trail> FilteredTrails = new ArrayList<>();
        for (Trail trail : TrailsArray) {

            if (trail.getTitle().toLowerCase().contains(searchword.toLowerCase()))
                FilteredTrails.add(trail);
        }
        gridView.setAdapter(new AdapterSwipeRefresh(getActivity(), FilteredTrails, ((MainActivity) getActivity()).getCache()));
        gridView.invalidateViews();
    }

    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setEnabled(true);
        isActive=true;
    }

    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        isActive=false;
    }



}
