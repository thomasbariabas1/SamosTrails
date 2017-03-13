package gr.aegean.com.samostrails.API;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ravi Tamada on 01/09/16.
 * www.androidhive.info
 */
public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

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

    public String makeServiceCallPost(String reqUrl) {
        String response = null;
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
        return response;
    }
    public String makeServiceCallPostCreate(String reqUrl,String body) {
        String response = null;
        try {
            URL url = new URL(reqUrl);


            Log.d(""+url.openConnection(),""+body);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.d("check","");
            conn.setRequestMethod("POST");
            Log.d("check","");
            conn.setRequestProperty("Content-Type", "application/json");
            Log.d("check","");
            conn.setRequestProperty("X-CSRF-Token", "ZxquODB4g4KhpxGJeSzp3y158fZ-0PERfwV4_Roogrw");
            Log.d("check","");
            conn.setRequestProperty("Cookie", "SESS97c3630a166635550417c4d029d6ea6fDl7YDX9Tewo3pISpVALSAecHVjVssPgjoVaDOxsVfHk");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.d("check","");

           // Log.d("",""+body);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
                Log.d("check","");
            writer.write(body);
            Log.d("check","");
            writer.flush();
            Log.d("check","");
            writer.close();
            Log.d("check","");
            os.close();

            conn.connect();
            Log.d("check","");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("check","");
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

    private String convertStreamToString(InputStream is) {
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
}