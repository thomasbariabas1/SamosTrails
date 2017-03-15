package gr.aegean.com.samostrails.API;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    private String strurl;
    private String body;
    static String response = null;
    String Token=null;
    String Session=null;
    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public static String makeServiceCallPost(String reqUrl) throws JSONException {
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        try {
            URL url = new URL(reqUrl);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write("{\n" +
                    "\t\"username\":\"Bariampas Thomas\",\n" +
                    "\t\"password\":\"123456\"\n" +
                    "\t\n" +
                    "\t\n" +
                    "}");
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return  response;
    }

    public static String makeServiceCallPostCreate(String reqUrl, String body, Context context) {
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        String response = null;
        String strurl=reqUrl;
        String bodysds=body;
        SharedPreferences sp = context.getSharedPreferences("Prefs", Activity.MODE_PRIVATE);
       String  Token = sp.getString("Token","");
        String Session=sp.getString("Session","");
      new CheckSomeStaff().execute(strurl,bodysds,Token,Session);
        return response;
}

    @NonNull
    private static String convertStreamToString(InputStream is) {
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public static class CheckSomeStaff extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);

                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-CSRF-Token", params[2]);
                conn.setRequestProperty("Cookie", params[3]);
               Log.e("Request Properties",""+ conn.getRequestProperties());


               // Log.e("Headers",""+conn.getHeaderFields());
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[1]);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int responseCode = conn.getResponseCode();

                if(responseCode == 200) {
                    // response code is OK
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = convertStreamToString(in);
                }else{

                    Log.e("Response Code",""+responseCode);

                }


            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

            return null;
        }
    }
}