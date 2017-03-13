package gr.aegean.com.samostrails;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import gr.aegean.com.samostrails.API.HttpHandler;

import static java.net.Proxy.Type.HTTP;

/**
 * Created by phantomas on 3/13/2017.
 */

public class LoginFragment extends Fragment {

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
        // showNotification();
        //   view.getContext().stopService(new Intent( view.getContext(), MyService.class));
        new LoginProcess().execute();
        return view;
    }


    public String session_name;
    public String session_id;


    //background task to login into Drupal
    private class LoginProcess extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            HttpHandler http=new HttpHandler();
              String response =http.makeServiceCallPost("http://test.samostrails.com/api/user/login");

           Log.d("",""+response);
            return 0;
        }


        protected void onPostExecute(Integer result) {

        }
    }
}
