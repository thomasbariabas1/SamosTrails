package gr.aegean.com.samostrails;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.DrupalDroid.UserServices;
import gr.aegean.com.samostrails.Services.MyService;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ProfilFragment extends Fragment {
    Button login;
    Button logout;
    Button register;
    ServicesClient client;
    SystemServices ss;
    UserServices us;
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
        View view = inflater.inflate(R.layout.profil_fragment, container, false);
        final Fragment fragment = LoginFragment.newInstance();
        //   view.getContext().stopService(new Intent( view.getContext(), MyService.class));

        client = ((MainActivity) getActivity()).getServicesClient();
        ss = new SystemServices(client);
        us = new UserServices(client);
        login = (Button) view.findViewById(R.id.login);
        logout = (Button) view.findViewById(R.id.logout);
        register=(Button) view.findViewById(R.id.register);
        ss.connect(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                try {
                    JSONObject jbo = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    JSONObject js = jbo.getJSONObject("user");
                    jbo = js.getJSONObject("roles");

                    if (jbo.has("1")) {
                        login.setVisibility(View.VISIBLE);
                        logout.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                    } else{
                        login.setVisibility(View.GONE);
                        register.setVisibility(View.GONE);
                        logout.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Log.e("JSON parse error: ", "" + e.getMessage());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                us.logout(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                       Toast.makeText(getActivity(),"You have Successfully Logout",Toast.LENGTH_LONG).show();

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content,  ProfilFragment.newInstance());
                        transaction.commit();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                    }
                });
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, RegisterFragment.newInstance());
                transaction.commit();

            }
        });
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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