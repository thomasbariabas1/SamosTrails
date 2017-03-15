package gr.aegean.com.samostrails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.voidberg.drupaldroid.ServicesClient;
import com.voidberg.drupaldroid.UserServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/13/2017.
 */

public class LoginFragment extends Fragment {

    EditText username;
    EditText password;
    Button login;
    Activity activity;
    ProgressDialog progressDialog;



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
        View view= inflater.inflate(R.layout.login_fragment, container, false);
        final ServicesClient client;
        client = new ServicesClient("http://test.samostrails.com", "api");
        final UserServices us;
        us = new UserServices(client);


        // showNotification();
        username = (EditText)view.findViewById(R.id.editUsername);
        password = (EditText)view.findViewById(R.id.editPassword);
        login = (Button) view.findViewById(R.id.buttonlogin);
        final String[] response = new String[1];
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    response[0] =  makeServiceCallPost("http://test.samostrails.com/api/user/login",username.getText().toString(),password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPreferences sp = getActivity().getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                try {
                    editor.putString("Token",   getToken(response[0]));
                    editor.putString("Session", getSession(response[0]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                editor.commit();*/
                login(us);
            }
        });



        return view;
    }

public void login(UserServices us){
    activity = getActivity();




    progressDialog = ProgressDialog.show(activity, "", "Logging you in", true, false);
    us.login(username.getText().toString(), password.getText().toString(), new AsyncHttpResponseHandler() {




        @Override
        public void onFinish() {
            progressDialog.hide();
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.v(TAG, responseBody.toString());

            new AlertDialog.Builder(activity).setMessage("Login was successful.").setPositiveButton("OK", null).setCancelable(true).create().show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.v(TAG, error.getMessage());
            Log.v(TAG, responseBody.toString());

            new AlertDialog.Builder(activity).setMessage("Login failed.").setPositiveButton("OK", null).setCancelable(true).create().show();
        }
    });

}

    public static String  makeServiceCallPost(String reqUrl,String username,String password) throws JSONException, IOException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String json = "{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        final String[] res = new String[1];

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(reqUrl)
                .post(body)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        };
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                         res[0] = response.body().string();
                        Log.e("Response", "" + res[0]);


                    }
                });
                return res[0];

                }

    public static String getToken(String response) throws JSONException {
        JSONObject jbo = new JSONObject(response);


        return  jbo.getString("token");
    }
    public static String getSession(String response) throws JSONException {
        JSONObject jbo = new JSONObject(response);
        return  jbo.getString("session_name")+jbo.getString("sessid");
    }






}
