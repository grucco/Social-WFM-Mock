package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Plant;

/**
 * Created by Raphael on 16/11/2015.
 */
public class FragmentImpiantoTecniche extends BaseFragment {

    private Plant mImpianto;

    @Bind(R.id.plant_macrocat_value)
    TextView plant_macrocat_value;

    @Bind(R.id.plant_tec_value)
    TextView plant_tec_value;

    @Bind(R.id.plant_codice_value)
    TextView plant_codice_value;

    @Bind(R.id.plant_desc_value)
    TextView plant_desc_value;


    @Bind(R.id.toggle_fanghi)
    ToggleButton toggle_fanghi;

    @Bind(R.id.toggle_clorazione)
    ToggleButton toggle_clorazione;

    @Bind(R.id.toggle_sost_chimiche)
    ToggleButton toggle_sost_chimiche;

    @Bind(R.id.toggle_elettrico)
    ToggleButton toggle_elettrico;

    @Bind(R.id.toggle_elettromeccanici)
    ToggleButton toggle_elettromeccanici;

    @Bind(R.id.toggle_misto)
    ToggleButton toggle_misto;


    @Bind(R.id.toggle_videosorveglianza)
    ToggleButton toggle_videosorveglianza;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impianto_tecniche, container, false);
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

        /**
         * http://www.hostedredmine.com/issues/499596
         * Tecniche
         * i campi di dettaglio sono: macrocategoria, impianto (codice), definiziona sede tecnica e descrizione apparecchiatura
         */
        plant_macrocat_value.setText(Utils.cleanNullableValue(mImpianto.getCategoria()));
        plant_codice_value.setText(Utils.cleanNullableValue(mImpianto.getAnlage()));
        plant_tec_value.setText(Utils.cleanNullableValue(mImpianto.getDefsedetec()));
        plant_desc_value.setText(Utils.cleanNullableValue(mImpianto.getDescappar()));

        enabledTechnicalDetail(
                mImpianto.isFanghi(),
                mImpianto.isCloro(),
                mImpianto.isChimic(),
                mImpianto.isQuadEl(),
                mImpianto.isElmecc(),
                mImpianto.isVideosorv(),
                mImpianto.isMisto());
    }

    private void enabledTechnicalDetail(boolean... values) {
        toggle_fanghi.setChecked(values[0]);
        toggle_clorazione.setChecked(values[1]);
        toggle_sost_chimiche.setChecked(values[2]);
        toggle_elettrico.setChecked(values[3]);
        toggle_elettromeccanici.setChecked(values[4]);
        toggle_videosorveglianza.setChecked(values[5]);
        toggle_misto.setChecked(values[6]);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {
    }

};