package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentImpiantoGenerali;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentImpiantoTecniche;

/**
 * Created by Raphael on 04/11/2015.
 */
public class ImpiantiAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Plant impianto;
    private String[] title;

    public ImpiantiAdapter(FragmentManager fm, Plant impianto, Context context) {
        super(fm);
        this.impianto = impianto;
        this.context = context;
        this.title = context.getResources().getStringArray(R.array.tab_impianto);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("IMPIANTO", impianto.getSedetec());

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, FragmentImpiantoGenerali.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(context, FragmentImpiantoTecniche.class.getName());
                break;
            /*case 2:
                fragment = Fragment.instantiate(context, FragmentImpiantoMedia.class.getName());
                break;*/
        }
        if (fragment != null) fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
