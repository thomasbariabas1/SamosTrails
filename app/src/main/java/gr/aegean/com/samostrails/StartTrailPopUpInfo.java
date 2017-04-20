package gr.aegean.com.samostrails;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gr.aegean.com.samostrails.Models.Trail;


/**
 * Created by phantomas on 4/20/2017.
 */

public class StartTrailPopUpInfo  extends DialogFragment  {
        Trail trail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_trail_pop_up_info, container, false);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView startingPoint = (TextView) view.findViewById(R.id.startingpoint);
        TextView mainSights = (TextView) view.findViewById(R.id.mainsights);
        TextView tips = (TextView) view.findViewById(R.id.tips);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        TextView kindOfTrail = (TextView) view.findViewById(R.id.kindoftrail);
        TextView difficultyLevel = (TextView) view.findViewById(R.id.difficultylevel);
        TextView childrenFriendly = (TextView) view.findViewById(R.id.childrenfriendly);
        TextView otherTransports = (TextView) view.findViewById(R.id.othertransport);
        TextView connectionToOtherTrails = (TextView) view.findViewById(R.id.connectiontoothertrails);
        final Bundle bundle = getArguments();
        trail = bundle.getParcelable("trail");
        description.setText(trail.getDescription());
        startingPoint.setText(trail.getStrartingPoin());
        mainSights.setText(trail.getMainSights());
        tips.setText(trail.getTips());
        distance.setText(String.valueOf(trail.getDistance()));
        kindOfTrail.setText(trail.getKindOfTrail().toString());
        difficultyLevel.setText(trail.getDifficultyLevel().toString());
        childrenFriendly.setText(String.valueOf(trail.isChildren_Friedly()));
        otherTransports.setText(trail.getOtherTransport());
        connectionToOtherTrails.setText(trail.getConnectionToOtherTrails());


        return view;
    }






    public void onPause(){super.onPause();}
    public void onStart(){
        super.onStart();
    }
    public void onResume(){super.onResume(); }
    public void onStop(){super.onStop();}
}
