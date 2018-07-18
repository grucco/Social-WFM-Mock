package it.acea.android.socialwfm.app.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.simeoni on 14/11/2016.
 */

public class DettaglioImpiantoClientePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList   = new ArrayList<>();
    private List<String>   fragmentTitles = new ArrayList<>();

    public DettaglioImpiantoClientePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}
