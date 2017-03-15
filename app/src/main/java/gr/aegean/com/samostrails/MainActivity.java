package gr.aegean.com.samostrails;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import gr.aegean.com.samostrails.API.HttpHandler;
import gr.aegean.com.samostrails.Adapters.AdapterTrailList;
import gr.aegean.com.samostrails.DrupalDroid.ServicesClient;
import gr.aegean.com.samostrails.Models.Trail;


public class MainActivity extends AppCompatActivity {

            SearchTrailFragment search = SearchTrailFragment.newInstance();
            LocalTrailsFragment local =  LocalTrailsFragment.newInstance();
            RecordingFragment recording = RecordingFragment.newInstance();
            ProfilFragment profil = ProfilFragment.newInstance();

            ServicesClient client=null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment =  search;
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = local;
                    break;
                case R.id.recording:
                    selectedFragment = recording;
                    break;
                case R.id.profil:
                    selectedFragment = profil;
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        client = new ServicesClient("http://test.samostrails.com", "api");

        if(savedInstanceState!=null){
            profil = (ProfilFragment) getSupportFragmentManager().getFragment(savedInstanceState, "ProfilFragment");
        }


        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SearchTrailFragment.newInstance());
        transaction.commit();




        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void setServiceClient(ServicesClient servicesclient){
        this.client=servicesclient;

    }

    public ServicesClient getServicesClient(){
        return this.client;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
}
