package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import it.acea.android.socialwfm.R;

/**
 * Created by fabio on 19/10/15.
 */
public class FragmentMyUpdate extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, null);

        return view;
    }

    @Override
    protected void setNavigationOnClickListener() {
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }
}
