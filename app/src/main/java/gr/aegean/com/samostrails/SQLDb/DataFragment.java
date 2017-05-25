package gr.aegean.com.samostrails.SQLDb;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by phantomas on 5/22/2017.
 */

public class DataFragment extends Fragment {

    // data object we want to retain
    private ArrayList<LatLng> TrailLatLonLineString = new ArrayList<>();

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(ArrayList<LatLng>  data) {
        this.TrailLatLonLineString = data;
    }

    public ArrayList<LatLng> getData() {
        return TrailLatLonLineString;
    }
    public void dataClear(){
        TrailLatLonLineString.clear();
    }
}