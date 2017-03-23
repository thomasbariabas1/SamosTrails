package gr.aegean.com.samostrails;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Adapters.AdapterTrailsOffline;
import gr.aegean.com.samostrails.Models.Trail;
import gr.aegean.com.samostrails.SQLDb.TrailDb;

/**
 * Created by phantomas on 3/23/2017.
 */

public class LocalRecordingTrailsFragment extends Fragment{

    private ProgressDialog pDialog;
    private GridView recordingtrails;
    ArrayList<Trail> TrailsArray = new ArrayList<>();
    public static LocalRecordingTrailsFragment newInstance() {
        LocalRecordingTrailsFragment fragment = new LocalRecordingTrailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recording_local_trails_fragment, container, false);
        recordingtrails = (GridView) view.findViewById(R.id.gridview3);

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

            TrailsArray = TrailDb.readFromDb(TrailDb.initiateDB(getActivity()));


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            final ArrayList<Trail> recordedtrailsarray = new ArrayList<>();
            int size = TrailsArray.size();
            for (int i = 0; i < size; i++) {
                Trail trail = TrailsArray.get(i);

                if (trail.isEditable())
                    recordedtrailsarray.add(TrailsArray.get(i));
            }

            AdapterTrailsOffline favoritetrailadapter;
            favoritetrailadapter = new AdapterTrailsOffline(getActivity(), recordedtrailsarray);

            recordingtrails.setAdapter(favoritetrailadapter);
            recordingtrails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("trail", recordedtrailsarray.get(position));
                    bundle.putBoolean("local",true);
                    Fragment fragment = TrailInfoFragment.newInstance();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, fragment);
                    transaction.commit();

                }
            });

        }

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
