package gr.aegean.com.samostrails.SQLDb;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by phantomas on 5/22/2017.
 */

public class DataStartFragment  extends Fragment {


    // data object we want to retain
    HashMap<String,String> data = new HashMap<>();

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(HashMap<String,String> data) {
        this.data=data;
    }

    public HashMap<String,String> getData() {
        return data;
    }
    public void clearData(){
        data.clear();
    }
}