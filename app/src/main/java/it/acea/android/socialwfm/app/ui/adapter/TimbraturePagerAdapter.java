package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentTimbratureCalendario;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentTimbratureLista;

/**
 * Created by a.simeoni on 16/03/2016.
 */
public class TimbraturePagerAdapter extends FragmentStatePagerAdapter {

    static String[] titles;
    private final int NUM_PAGES = 2;

    public TimbraturePagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        titles = context.getResources().getStringArray(R.array.tabstrip_timbrature);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentTimbratureLista();
            case 1:
                return new FragmentTimbratureCalendario();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
