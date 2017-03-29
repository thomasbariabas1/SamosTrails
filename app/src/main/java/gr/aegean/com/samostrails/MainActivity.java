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

        int memClass = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapCache = new LruCache<>(cacheSize);
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
    public void onStop(){
        super.onStop();
    }
}
