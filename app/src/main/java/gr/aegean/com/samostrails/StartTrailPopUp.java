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

public class StartTrailPopUp extends DialogFragment {
    CallbackManager callbackManager;
    ShareButton loginButton;
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
        final Bundle bundle = getArguments();
        endtimed=bundle.getLong("endtime");
        starttime = bundle.getString("starttime");
        pausetime=bundle.getLong("pausedtime");
        distanced= bundle.getDouble("distance");
        starttimelong=bundle.getLong("starttimelong");

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
        loginButton = (ShareButton) view.findViewById(R.id.login_button);
        // If using in a fragment
        loginButton.setFragment(this);
        // Create an object
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "fitness.course")
                .putString("og:title","test")
                .putString("og:url","http://www.samostrails.com")
                .putString("og:image:url","http://www.samostrails.com")
                .putInt("fitness:duration:value", 100)
                .putString("fitness:duration:units", "s")
                .putInt("fitness:distance:value",  5)
                .putString("fitness:distance:units", "km")
                .putInt("fitness:speed:value", 10)
                .putString("fitness:speed:units", "m/s")
                .putString("fitness:somethins:units","fuel")
                .putInt("fitness:somethins:value",10)
                .build();

        // Create an action
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.walks")
                .putObject("course", object)
                .build();

        // Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("course")
                .setAction(action)
                .build();


        // Callback registration
        loginButton.setShareContent(content);


        return view;
    }





    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public String getRealTime(Long time){
        String test;
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);
        int hours   = (int) ((time / (1000*60*60)) % 24);
        test=hours +":"+minutes+":"+seconds;
        return  test;
    }
    public void onPause(){super.onPause();}
    public void onStart(){
        super.onStart();
    }
    public void onResume(){super.onResume(); }
    public void onStop(){super.onStop();}
}
