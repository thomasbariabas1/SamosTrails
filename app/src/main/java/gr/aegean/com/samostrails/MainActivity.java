package gr.aegean.com.samostrails;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.MenuItem;

import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;


public class MainActivity extends AppCompatActivity  {

    SearchTrailFragment search = SearchTrailFragment.newInstance();

    RecordingFragment recording = RecordingFragment.newInstance();
    ProfilFragment profil = ProfilFragment.newInstance();
    LruCache<Integer, Bitmap> bitmapCache;
    String TAG = "";
    ServicesClient client = null;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = search;
                    TAG = "search";
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = LocalTrailsFragment.newInstance();
                    TAG = "local";
                    break;
                case R.id.recording:
                    selectedFragment = recording;
                    TAG = "recording";
                    break;
                case R.id.profil:
                    selectedFragment = profil;
                    TAG = "profil";
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment, TAG);
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new ServicesClient("http://test.samostrails.com", "api");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }
        int memClass = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapCache = new LruCache<Integer, Bitmap>(cacheSize);
        if (savedInstanceState != null) {
            profil = (ProfilFragment) getSupportFragmentManager().getFragment(savedInstanceState, "ProfilFragment");
        }
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SearchTrailFragment.newInstance());
        transaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void setServiceClient(ServicesClient servicesclient) {
        this.client = servicesclient;

    }

    public ServicesClient getServicesClient() {
        return this.client;
    }

    public LruCache<Integer, Bitmap> getCache() {
        return this.bitmapCache;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }








    @Override
    public void onStop(){
        super.onStop();
    }
}
