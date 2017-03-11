package gr.aegean.com.samostrails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

            SearchTrailFragment search = SearchTrailFragment.newInstance();
            LocalTrailsFragment local =  LocalTrailsFragment.newInstance();
            RecordingFragment recording = RecordingFragment.newInstance();
            ProfilFragment profil = ProfilFragment.newInstance();

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



        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SearchTrailFragment.newInstance());
        transaction.commit();




        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
