package gr.aegean.com.samostrails;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Adapters.AdapterTrailsOffline;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

public class LocalTrailsFragment extends Fragment {

    private ProgressDialog pDialog;
    private GridView lv;
    ArrayList<Trail> TrailsArray = new ArrayList<>();
    public static LocalTrailsFragment newInstance() {
        LocalTrailsFragment fragment = new LocalTrailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.local_trails_fragment, container, false);

        lv = (GridView) view.findViewById (R.id.gridview2);
        new GetTrails().execute();

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

            TrailsArray= TrailDb.readFromDb(TrailDb.initiateDB(getActivity()));


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.e("I LIsta",TrailsArray.toString());


            AdapterTrailsOffline adbTrails;


//then populate myListItems


            adbTrails= new AdapterTrailsOffline(getActivity(),  TrailsArray);

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
                    Log.e("Gridviewclicked",""+TrailsArray.get(position).getTitle());
                }
            });

        }

    }

}