package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.ui.fragment.FragmentMyWorkContainer;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentMyUpdate;

/**
 * Created by Raphael on 07/09/2015.
 */
public class HomeStatePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public HomeStatePagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(mContext, FragmentMyWorkContainer.class.getName());
                return fragment;
            case 1:
                fragment = Fragment.instantiate(mContext, FragmentMyUpdate.class.getName());
                return fragment;
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
        String value = "";
        int pattern = position % 2;
        switch (pattern) {
            case 0:
                value = mContext.getString(R.string.tab_mywork);
                break;
            case 1:
                value = mContext.getString(R.string.tab_update);
                break;
        }
        return value;
    }

};