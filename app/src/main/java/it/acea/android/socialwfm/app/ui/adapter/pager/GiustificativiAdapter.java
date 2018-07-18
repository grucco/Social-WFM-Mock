package it.acea.android.socialwfm.app.ui.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.app.ui.fragment.inner.giustificativi.GiustificativiCalendarioFragment;
import it.acea.android.socialwfm.app.ui.fragment.inner.giustificativi.GiustificativiListFragment;

/**
 * Created by raphaelbussa on 17/03/16.
 */
public class GiustificativiAdapter extends FragmentStatePagerAdapter {

    private GiustificativiListFragment listFragment;
    private GiustificativiCalendarioFragment calendarioFragment;

    public GiustificativiListFragment getListFragment() {
        return listFragment;
    }

    public GiustificativiCalendarioFragment getCalendarioFragment() {
        return calendarioFragment;
    }

    public GiustificativiAdapter(FragmentManager fm) {
        super(fm);
        listFragment = new GiustificativiListFragment();
        calendarioFragment = new GiustificativiCalendarioFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return listFragment;
            case 1:
                return calendarioFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "GIUSTIFICATIVI";
            case 1:
                return "RIEPILOGO A CALENDARIO";
            default:
                return null;
        }
    }
}
