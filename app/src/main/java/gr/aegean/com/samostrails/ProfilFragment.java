package gr.aegean.com.samostrails;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import gr.aegean.com.samostrails.Services.MyService;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ProfilFragment extends Fragment {

    public static ProfilFragment newInstance() {
        ProfilFragment fragment = new ProfilFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view= inflater.inflate(R.layout.profil_fragment, container, false);
       // showNotification();
     //   view.getContext().stopService(new Intent( view.getContext(), MyService.class));

        return view;
    }
   /* public void showNotification() {
        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), MainActivity.class), 0);
        // Key for the string that's delivered in the action's intent.
        final String KEY_TEXT_REPLY = "key_text_reply";
        String replyLabel = "hiiiiiiiiiiiiii";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
      Action action = new Action.Builder(R.drawable.cast_ic_notification_small_icon,   "wtf", pi)
                        .addRemoteInput(remoteInput)
                        .build();
        Notification notification = new NotificationCompat.Builder( getActivity())
                .setTicker("test")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("TEST")
                .setContentText("TESTING TEST")
                .setContentIntent(pi)
                .setAutoCancel(false).addAction(action)
                .build();

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }*/
}