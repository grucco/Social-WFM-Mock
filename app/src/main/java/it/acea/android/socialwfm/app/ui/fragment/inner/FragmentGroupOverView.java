package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentGroupOverView extends BaseFragment {

    private static final String TAG = FragmentGroupOverView.class.getName();

    private String description;

    @Bind(R.id.descrizione)
    TextView descrizione;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        description = getArguments().getString("DESCRIPTION");
        View view = inflater.inflate(R.layout.fragment_group_overview, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descrizione.setText(description);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
