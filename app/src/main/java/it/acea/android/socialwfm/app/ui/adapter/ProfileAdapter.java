package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentProfileInfo;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentProfileLavoro;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentProfileSocial;

/**
 * Created by Raphael on 04/11/2015.
 */
public class ProfileAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String userId;
    private String[] title;

    public ProfileAdapter(FragmentManager fm, String userId, Context context) {
        super(fm);
        this.userId = userId;
        this.context = context;
        this.title = context.getResources().getStringArray(R.array.tab_profile);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, FragmentProfileInfo.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(context, FragmentProfileLavoro.class.getName());
                break;
            case 2:
                fragment = Fragment.instantiate(context, FragmentProfileSocial.class.getName());
                break;
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
