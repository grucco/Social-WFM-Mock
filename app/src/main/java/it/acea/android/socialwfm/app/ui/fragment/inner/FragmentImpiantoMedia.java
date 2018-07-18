package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.odl.Plant;

/**
 * Created by Raphael on 16/11/2015.
 */
public class FragmentImpiantoMedia extends BaseFragment {

    private Plant mImpianto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impianto_media, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        mImpianto = realm.
                where(Plant.class).
                equalTo("sedetec", getArguments().getString("IMPIANTO")).
                findFirst();
    }

    private boolean isStart = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            buildFragmentData();
        }
    }

    private void buildFragmentData() {

    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
