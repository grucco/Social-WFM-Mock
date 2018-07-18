package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentGroupMedia;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentGroupMembri;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentGroupOverView;

/**
 * Created by Raphael on 04/11/2015.
 */
public class GroupAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String groupId;
    private String description;
    private String[] title;

    public GroupAdapter(FragmentManager fm, String groupId, String description, Context context) {
        super(fm);
        this.groupId = groupId;
        this.description = description;
        this.context = context;
        this.title = context.getResources().getStringArray(R.array.tab_group);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", groupId);
        bundle.putString("DESCRIPTION", description);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = Fragment.instantiate(context, FragmentGroupOverView.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(context, FragmentGroupMembri.class.getName());
                break;
            case 2:
                fragment = Fragment.instantiate(context, FragmentGroupMedia.class.getName());
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
