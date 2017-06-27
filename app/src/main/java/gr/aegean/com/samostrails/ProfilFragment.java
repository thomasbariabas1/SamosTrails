package gr.aegean.com.samostrails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.DrupalDroid.UserServices;

import static android.content.ContentValues.TAG;

public class ProfilFragment extends Fragment {
    Button login;
    Button logout;
    Button register;
    ServicesClient client;
    SystemServices ss;
    UserServices us;
    ScrollView sv;
    TextView aboutus;
    boolean clicked = false;

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
        FacebookSdk.setApplicationId("631461603708736");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.profil_fragment, container, false);
        final Fragment fragment = LoginFragment.newInstance();
        aboutus = (TextView) view.findViewById(R.id.aboutus);
        client = ((MainActivity) getActivity()).getServicesClient();
        sv = (ScrollView) view.findViewById(R.id.aboutussv);
        final int[] i = {0};
        new CountDownTimer(31000, 10) {

            public void onTick(long millisUntilFinished) {
                if(!clicked)
                sv.scrollTo(0, i[0]++);
                else
                    cancel();
            }

            public void onFinish() {

            }
        }.start();
        aboutus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clicked=true;
                return false;
            }
        });


        ss = new SystemServices(client);
        us = new UserServices(client);
        login = (Button) view.findViewById(R.id.login);
        logout = (Button) view.findViewById(R.id.logout);
        register = (Button) view.findViewById(R.id.register);
        login.setVisibility(View.GONE);
        logout.setVisibility(View.GONE);
        register.setVisibility(View.GONE);
        if(isLoggedIn()){
            try {
                fblogin(AccessToken.getCurrentAccessToken().getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            login.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
        }else{
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
                    } else {
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


            }
        });
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn()) {
                    LoginManager.getInstance().logOut();
                   // Toast.makeText(getActivity(), "You have Successfully Logout", Toast.LENGTH_LONG).show();
                    us.logout(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(getActivity(), "You have Successfully Logout", Toast.LENGTH_LONG).show();

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content, ProfilFragment.newInstance());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                        }
                    });
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, ProfilFragment.newInstance());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                us.logout(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getActivity(), "You have Successfully Logout", Toast.LENGTH_LONG).show();

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, ProfilFragment.newInstance());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                    }
                });}
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, RegisterFragment.newInstance());
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return view;
    }
    public void fblogin(String fbtoken) throws JSONException {
       final Activity activity = getActivity();
        Log.e("inside fblogin","");
        final String[] token = new String[1];

        JSONObject params = new JSONObject();
        try {
            params.put("access_token", fbtoken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Logging you in with Facebook", true, false);
        client.post("fboauth/connect", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
               progressDialog.hide();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                    token[0] = jsonObject.getString("token");
                    client.setToken(token[0]);
                    Log.e(TAG, token[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // new AlertDialog.Builder(activity).setMessage("Login was successful.").setPositiveButton("OK", null).setCancelable(true).create().show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, error.getMessage());
                Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));

                //new AlertDialog.Builder(activity).setMessage("Login failed. For:" + new String(responseBody, StandardCharsets.UTF_8)).setPositiveButton("OK", null).setCancelable(true).create().show();
            }
        });

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
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