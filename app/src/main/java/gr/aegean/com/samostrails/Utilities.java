package gr.aegean.com.samostrails;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by phantomas on 3/8/2017.
 */

public class Utilities {

    public static boolean hasActiveInternetConnection(Context context) {
        final boolean[] results = {false};
        final Context contexts=context;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isNetworkAvailable(contexts)) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(1500);
                        urlc.connect();
                        results[0] = (urlc.getResponseCode() == 200);
                        Log.e("connection test",""+urlc.getResponseCode());
                    } catch (IOException e) {
                        Log.e(TAG, "Error checking internet connection", e);
                    }
                }else {
                    Log.d(TAG, "No network available!");
                    results[0] = false;
                }

                return null;
            }

        };
        Log.e("results",""+results[0]);
        return results[0];
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
