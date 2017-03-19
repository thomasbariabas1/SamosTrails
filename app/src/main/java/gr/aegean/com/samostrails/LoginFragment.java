package gr.aegean.com.samostrails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.DrupalDroid.UserServices;

import static android.content.ContentValues.TAG;


public class LoginFragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Activity activity;
    ProgressDialog progressDialog;
    ServicesClient client;


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        client = ((MainActivity) getActivity()).getServicesClient();
        final UserServices us;
        us = new UserServices(client);
        final SystemServices ss;
        ss = new SystemServices(client);
        //PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        // client.setCookieStore(myCookieStore);
        // showNotification();
        username = (EditText) view.findViewById(R.id.editUsername);
        password = (EditText) view.findViewById(R.id.editPassword);
        login = (Button) view.findViewById(R.id.buttonlogin);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(us);

            }
        });




        return view;
    }

    public void login(UserServices us) {
        activity = getActivity();
        final String[] token = new String[1];
        final Fragment fragment = ProfilFragment.newInstance();
        if(!username.getText().toString().equals("")&&!password.getText().toString().equals("")) {
            progressDialog = ProgressDialog.show(activity, "", "Logging you in", true, false);
            us.login(username.getText().toString(), password.getText().toString(), new AsyncHttpResponseHandler() {


                @Override
                public void onFinish() {
                    progressDialog.hide();
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));
                    try {
                        JSONObject jsonObject = new JSONObject(new String(responseBody, StandardCharsets.UTF_8));
                        token[0] = jsonObject.getString("token");
                        client.setToken(token[0]);
                        Log.e(TAG, token[0]);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new AlertDialog.Builder(activity).setMessage("Login was successful.").setPositiveButton("OK", null).setCancelable(true).create().show();
                    username.setText("");
                    password.setText("");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, error.getMessage());
                    Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));

                    new AlertDialog.Builder(activity).setMessage("Login failed. For:" + new String(responseBody, StandardCharsets.UTF_8)).setPositiveButton("OK", null).setCancelable(true).create().show();
                }
            });

        }else{
            Toast.makeText(getActivity(),"Please Fill All the Fields",Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username.getText().toString());
        outState.putString("password", password.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            username.setText(savedInstanceState.getString("username"));
            password.setText(savedInstanceState.getString("password"));
        }
    }
    public void onPause(){
        super.onPause();
    }
    public void onStart(){
        super.onStart();
    }
    public void onResume(){
        super.onResume();
    }
    public void onStop(){
        super.onStop();
    }
}
