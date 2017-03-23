package gr.aegean.com.samostrails.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import gr.aegean.com.samostrails.LocalFavouriteTrailsFragment;
import gr.aegean.com.samostrails.LocalRecordingTrailsFragment;

/**
 * Created by phantomas on 3/23/2017.
 */

public class FragmentTrailPageAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 2;

    public FragmentTrailPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LocalFavouriteTrailsFragment.newInstance();
            case 1:
                return LocalRecordingTrailsFragment.newInstance();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return  NUM_ITEMS;
    }
    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0)
            return "Favorite Trails";
        return "Recording Trails";
    }
}
