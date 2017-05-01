package gr.aegean.com.samostrails;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;

import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;


public class MainActivity extends AppCompatActivity {

    SearchTrailFragment search = SearchTrailFragment.newInstance();
    RecordingFragment recording = RecordingFragment.newInstance();
    LruCache<Integer, Bitmap> bitmapCache;
    String TAG = "";
    ServicesClient client = null;
    int hasStartedTrail=-1;
    boolean isFirstTime = true;
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
        client = new ServicesClient("http://www.samostrails.com/samostrails", "api");

        int memClass = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapCache = new LruCache<>(cacheSize);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SearchTrailFragment.newInstance());
        transaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
    public void onBackPressed() {
        showDialog(this, "Exit", "Are you sure you want to exit?");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setHasStartedTrail(int startedTrail) {
        this.hasStartedTrail=startedTrail;
    }
    public int hasStartedTrail(){
        return hasStartedTrail;
    }
    public void setFirstTime(boolean isFirstTime){
        this.isFirstTime=isFirstTime;
    }
    public boolean isFirstTime(){
        return this.isFirstTime;
    }
    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, ProfilFragment.newInstance(), TAG);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
