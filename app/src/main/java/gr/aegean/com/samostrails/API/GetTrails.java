package gr.aegean.com.samostrails.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Adapters.AdapterTrails;
import gr.aegean.com.samostrails.Models.DifficultyLevel;
import gr.aegean.com.samostrails.Models.DistanceLevel;
import gr.aegean.com.samostrails.Models.KindOfTrail;
import gr.aegean.com.samostrails.Models.Trail;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/7/2017.
 */

public abstract class GetTrails extends AsyncTask<Void, Void, Void> {
    public interface MyAsyncTaskListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded(String result);
    }
    private static String url = "http://test.samostrails.com/trail-webservice";
    ArrayList<Trail> TrailsArray = new ArrayList<Trail>();

    private MyAsyncTaskListener mListener;

    final public void setListener(MyAsyncTaskListener listener) {
        mListener = listener;
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url);

        Log.e("", "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {

                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray trails = jsonObj.getJSONArray("nodes");




                // looping through All Contacts
                for (int i = 0; i < trails.length(); i++) {
                    JSONObject z = trails.getJSONObject(i);
                    JSONObject c = z.getJSONObject("node");
                    JSONObject image = c.getJSONObject("Image");

                  /*  TrailsArray.add(new Trail(c.getString("Children Friedly").equals("No")?false:true,Integer.parseInt(c.getString("Entity ID")),
                            DifficultyLevel.valueOf(c.getString("Difficulty Level")), DistanceLevel.valueOf(c.getString("Distance Level").equals("Long (>3km)")?"Long":"Short"),
                            KindOfTrail.valueOf(c.getString("Kind of trail").equals("One Way")?"OneWay":"Loop"),image.getString("src").replace("\\", ""),c.getString("Leaflet"),
                            Double.parseDouble(c.getString("Distance").replaceAll("\\D+","")),c.getString("Title")));

                    Log.e("", i+"+" + c);*/


                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());



            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");



        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        // common stuff

        if (mListener != null)
            mListener.onPreExecuteConcluded();
    }


    final protected void onPostExecute(String result) {
        // common stuff

        if (mListener != null)
            mListener.onPostExecuteConcluded(result);
    }
}

