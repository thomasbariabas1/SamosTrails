package gr.aegean.com.samostrails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import gr.aegean.com.samostrails.Adapters.FragmentTrailPageAdapter;

public class LocalTrailsFragment extends Fragment {

    FragmentStatePagerAdapter adapterViewPager;
    public static LocalTrailsFragment newInstance() {
        LocalTrailsFragment fragment = new LocalTrailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_trails_fragment, container, false);
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        adapterViewPager = new FragmentTrailPageAdapter(getActivity().getSupportFragmentManager());

        vpPager.setAdapter(adapterViewPager);

        return view;
    }



    public void onPause() {
        super.onPause();
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

}