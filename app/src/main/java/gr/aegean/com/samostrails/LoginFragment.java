package gr.aegean.com.samostrails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.TokenParser;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.SystemServices;
import gr.aegean.com.samostrails.DrupalDroid.UserServices;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Activity activity;
    ProgressDialog progressDialog;
    ServicesClient client;
    LoginButton  loginButton;
    CallbackManager callbackManager;
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
        FacebookSdk.setApplicationId("631461603708736");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        client = ((MainActivity) getActivity()).getServicesClient();
        final UserServices us;
        us = new UserServices(client);
        final SystemServices ss;
        ss = new SystemServices(client);
        username = (EditText) view.findViewById(R.id.editUsername);
        password = (EditText) view.findViewById(R.id.editPassword);
        login = (Button) view.findViewById(R.id.buttonlogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(us);

            }
        });
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        if(isLoggedIn())
            try {
                fblogin(AccessToken.getCurrentAccessToken().getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.e("OnSuccess",loginResult.getAccessToken().getToken());
                try {
                    fblogin(loginResult.getAccessToken().getToken());
                } catch (JSONException e) {
                   Log.e("exception",""+e.getMessage());
                }
            }

            @Override
            public void onCancel() {
                Log.e("OnCansel","");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("OnError","");
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
                        transaction.addToBackStack(null);
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

    public void fblogin(String fbtoken) throws JSONException {
        activity = getActivity();
        Log.e("inside fblogin","");
        final String[] token = new String[1];
        final Fragment fragment = ProfilFragment.newInstance();
        JSONObject params = new JSONObject();
        try {
            params.put("access_token", fbtoken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
            progressDialog = ProgressDialog.show(activity, "", "Logging you in", true, false);
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
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new AlertDialog.Builder(activity).setMessage("Login was successful.").setPositiveButton("OK", null).setCancelable(true).create().show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, error.getMessage());
                    Log.e(TAG, new String(responseBody, StandardCharsets.UTF_8));

                    new AlertDialog.Builder(activity).setMessage("Login failed. For:" + new String(responseBody, StandardCharsets.UTF_8)).setPositiveButton("OK", null).setCancelable(true).create().show();
                }
            });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
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
