package gr.aegean.com.samostrails.DrupalDroid;

import android.os.Parcel;
import android.os.Parcelable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;

public class ServicesClient implements Parcelable {
    private String url;
    private String rootUrl;
    private String token;

    public static AsyncHttpClient client = new AsyncHttpClient();

    public ServicesClient(String server, String base) {
        this.url = server + '/' + base + '/';
        this.rootUrl = server + '/';
        this.token = "";
        client.setTimeout(60000);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String Token) {
        this.token = Token;
    }

    public ServicesClient getClient() {
        return this;
    }

    private void setHeaders() {
        if (!token.equals("")) {
            client.addHeader("X-CSRF-Token", token);
        }
    }

    public void setCookieStore(PersistentCookieStore cookieStore) {
        client.setCookieStore(cookieStore);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return this.url + relativeUrl;
    }

    private String getAbsoluteRootUrl(String relativeUrl) {
        return this.rootUrl + relativeUrl;
    }

    public void getRoot(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteRootUrl(url), params, responseHandler);

    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        StringEntity se = null;
        se = new StringEntity(params.toString(), HTTP.UTF_8);
        se.setContentType(new cz.msebera.android.httpclient.message.BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(null, getAbsoluteUrl(url), se, "application/json", responseHandler);

    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        this.setHeaders();
        StringEntity se = null;
        se = new StringEntity(params.toString(), HTTP.UTF_8);
        se.setContentType(new cz.msebera.android.httpclient.message.BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.put(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }

    public void getToken(AsyncHttpResponseHandler responseHandler) {
        this.getRoot("services/session/token", new RequestParams(), responseHandler);
    }

    protected ServicesClient(Parcel in) {
        url = in.readString();
        rootUrl = in.readString();
        token = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(rootUrl);
        dest.writeString(token);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ServicesClient> CREATOR = new Parcelable.Creator<ServicesClient>() {
        @Override
        public ServicesClient createFromParcel(Parcel in) {
            return new ServicesClient(in);
        }

        @Override
        public ServicesClient[] newArray(int size) {
            return new ServicesClient[size];
        }
    };
}
