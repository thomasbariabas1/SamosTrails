package gr.aegean.com.samostrails;

import android.os.Bundle;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import cz.msebera.android.httpclient.Header;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.DrupalDroid.UserServices;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/19/2017.
 */

public class RegisterFragment extends Fragment {
    ServicesClient client;
    Button register;
    EditText username;
    EditText password;
    EditText email;
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        client = ((MainActivity) getActivity()).getServicesClient();
        final UserServices us;
        us = new UserServices(client);
        register = (Button) view.findViewById(R.id.registerreg);
        username = (EditText) view.findViewById(R.id.editusernamereg);
        password = (EditText) view.findViewById(R.id.editpasswordreg);
        email = (EditText) view.findViewById(R.id.editemail);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().equals("")&&!password.getText().toString().equals("")&&!email.getText().toString().equals("")){
              client.getToken(new AsyncHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                      String token = new String(responseBody, StandardCharsets.UTF_8);
                      client.setToken(token);
                      JSONObject json = new JSONObject();
                      try {
                          json.put("name",username.getText().toString());
                          json.put("pass",password.getText().toString());
                          json.put("mail",email.getText().toString());
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }

                      client.post("user/register", json, new AsyncHttpResponseHandler() {
                          @Override
                          public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                              Toast.makeText(getActivity(),"You have Successfully registered",Toast.LENGTH_LONG).show();
                              client.getToken(new AsyncHttpResponseHandler() {
                                  @Override
                                  public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                      String token = new String(responseBody, StandardCharsets.UTF_8);
                                      client.setToken(token);
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

                                  @Override
                                  public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                  }
                              });

                          }

                          @Override
                          public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                              Toast.makeText(getActivity(),"Something wrong",Toast.LENGTH_LONG).show();
                          }
                      });


                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                  }
              });
            }else{
                    Toast.makeText(getActivity(),"Please Fill All the Fields",Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }









}
