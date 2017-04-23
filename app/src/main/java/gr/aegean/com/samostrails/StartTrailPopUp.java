package gr.aegean.com.samostrails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.aegean.com.samostrails.Models.Trail;

public class StartTrailPopUp extends DialogFragment {
    CallbackManager callbackManager;
    ShareButton shareButton;
    TextView distance;
    TextView StartTime;
    TextView endtime;
    TextView walkingtime;
    TextView avgspeed;
    TextView time;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.setApplicationId("631461603708736");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.start_trail_pop_up, container, false);
        distance = (TextView) view.findViewById(R.id.distancelast);
        StartTime = (TextView) view.findViewById(R.id.starttimelast);
        endtime = (TextView) view.findViewById(R.id.endtimelast);
        walkingtime = (TextView) view.findViewById(R.id.walkingtimelast);
        avgspeed = (TextView) view.findViewById(R.id.avgspeedlast);
        time = (TextView) view.findViewById(R.id.timelast);



        long endtimed;
        String starttime;
        long starttimelong;
        double pausetime;
        double distanced;
        double totaltime;
        double avgspeedd;
        Trail trail;
        final Bundle bundle = getArguments();
        endtimed=bundle.getLong("endtime");
        starttime = bundle.getString("starttime");
        pausetime=bundle.getLong("pausedtime");
        distanced= bundle.getDouble("distance");
        starttimelong=bundle.getLong("starttimelong");
        trail=bundle.getParcelable("trail");
        distance.setText(String.valueOf(distanced/1000));
        StartTime.setText(starttime);
        endtime.setText(bundle.getString("end"));
        totaltime = endtimed-starttimelong;
        time.setText(getRealTime((long)totaltime));
        Log.e("pauseTime",""+pausetime);
        walkingtime.setText(getRealTime((long)(totaltime-pausetime)));
        Log.e("totaltime",""+totaltime);
        double tmp = ((totaltime-pausetime) / 3600000);
        Log.e("tmp" , " "+tmp);
        avgspeedd = (distanced/1000)/tmp;
        Log.e("avgspeed",""+avgspeedd);
        avgspeed.setText(String.valueOf(avgspeedd));
        callbackManager = CallbackManager.Factory.create();
        shareButton = (ShareButton) view.findViewById(R.id.login_button);
        // If using in a fragment
        shareButton.setFragment(this);

        // Create an object
        assert trail != null;
        int duration = (int)((totaltime-pausetime)/1000);
        int distanceface = (int)(distanced/1000);
        int speed = (int)(distanced/((totaltime-pausetime)/1000));
        Log.e("trailimage",""+trail.getImage());


        String toSplit = trail.getGeometryCollection();
        toSplit = toSplit.replaceAll("\\(", "");
        toSplit = toSplit.replaceAll("\\)", "");
        ArrayList<String> coordinates = new ArrayList<>();
        ArrayList<String> linestring = new ArrayList<>();
        ArrayList<String> point = new ArrayList<>();
        String[] commatokens = toSplit.split(",");

        for (String commatoken : commatokens) {
            Log.e("commatokens", "" + commatoken);
            coordinates.add(commatoken);
        }
        for (int i = 0; i < coordinates.size(); i++) {

            String[] tokens = coordinates.get(i).split("\\s");

            for (int j = 0; j < tokens.length; j++) {
                String token = tokens[j];

                if (token.equals("POINT")) {
                    point.add(tokens[j]);
                    point.add(tokens[j + 1]);
                    point.add(tokens[j + 2]);
                    break;
                }

                linestring.add(tokens[j]);
            }
        }
        ArrayList<LatLng> fullline = new ArrayList<>();
        ArrayList<LatLng> fullpoints = new ArrayList<>();
        ArrayList<Double> filtredlinestring = filter(linestring);
        ArrayList<Double> filteredpoints = filter(point);
        for (int i = 0; i < filtredlinestring.size(); i++) {
            fullline.add(new LatLng(filtredlinestring.get(i + 1), filtredlinestring.get(i)));
            filtredlinestring.get(i);
            i++;
        }
        for (int i = 0; i < filteredpoints.size(); i++) {
            fullpoints.add(new LatLng(filteredpoints.get(i + 1), filteredpoints.get(i)));
            i++;
        }
        ShareOpenGraphObject.Builder object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "fitness.course")
                .putString("og:title",trail.getTitle())
                .putString("og:url",trail.getUrl())
                .putString("og:image:url",trail.getImage())
                .putDouble("fitness:duration:value", duration)
                .putString("fitness:duration:units", "s")
                .putDouble("fitness:distance:value",  distanceface)
                .putString("fitness:distance:units", "km")
                .putDouble("fitness:speed:value", speed)
                .putString("fitness:speed:units", "m/s");
        for (int i = 0; i < fullline.size(); i++) {

            object.putDouble("fitness:metrics[" + i + "]:location:latitude", fullline.get(i).latitude);
            object.putDouble("fitness:metrics["+i+"]:location:longitude",fullline.get(i).longitude);


        }
        // Create an action
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.walks")
                .putObject("course", object.build())
                .build();

        // Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("course")
                .setAction(action)
                .build();


        // Callback registration
        shareButton.setShareContent(content);


        return view;
    }





    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        dismiss();

    }
    public String getRealTime(Long time){
        String test;
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);
        int hours   = (int) ((time / (1000*60*60)) % 24);
        test=hours +":"+minutes+":"+seconds;
        return  test;
    }
    public int gettimetosec(String time){
        String[] split = time.split(":");
        return  Integer.parseInt(split[0])*3600 + Integer.parseInt(split[1])*60 +Integer.parseInt(split[2]);
    }

    public ArrayList<Double> filter(ArrayList<String> lineling) {
        ArrayList<Double> temp = new ArrayList<>();
        for (String i : lineling) {
            if (!i.equals("POINT") && !i.equals("GEOMETRYCOLLECTION") && !i.equals("LINESTRING") && !i.equals("")) {
                temp.add(Double.parseDouble(i));
            }
        }
        return temp;
    }
    public void onPause(){super.onPause();}
    public void onStart(){
        super.onStart();
    }
    public void onResume(){super.onResume(); }
    public void onStop(){super.onStop();}
}
