package org.fingerlinks.mobile.android.utils.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

/**
 * Created by Raphael on 07/10/2015.
 */
public abstract class BaseFragment extends Fragment {

    public abstract boolean onSupportBackPressed(Bundle bundle);

    protected abstract void setNavigationOnClickListener();

    public Toolbar getToolbar() {
        return ((BaseActivity) getActivity()).getToolbar();
    }

    public void setIcon(int icon) {
        ((BaseActivity) getActivity()).setIcon(icon);
    }

    public void setTitle(int title) {
        ((BaseActivity) getActivity()).setTitle(title);
    }

    public void setSubtitle(int subtitle) {
        ((BaseActivity) getActivity()).setSubtitle(subtitle);
    }

    public void setTitle(String title) {
        ((BaseActivity) getActivity()).setTitle(title);
    }

    public void setSubtitle(String subtitle) {
        ((BaseActivity) getActivity()).setSubtitle(subtitle);
    }

    public void setNavigationIcon(int icon) {
        ((BaseActivity) getActivity()).setNavigationIcon(icon);
    }

    public void supportNavigationOnClickListener() {
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNavigationOnClickListener();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(getChildFragmentManager().getFragments() != null) {
            for(Fragment fragment: getChildFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

};