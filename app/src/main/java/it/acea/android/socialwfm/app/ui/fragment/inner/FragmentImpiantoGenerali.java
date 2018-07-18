package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
public class FragmentImpiantoGenerali extends BaseFragment {

    private Plant mImpianto;

    @Bind(R.id.plant_city)
    TextView plant_city;

    @Bind(R.id.plant_zone)
    TextView plant_zone;

    @Bind(R.id.plant_flor)
    TextView plant_flor;

    @Bind(R.id.plant_position)
    TextView plant_position;

    @Bind(R.id.google_map_preview)
    ImageView google_map_preview;

    @Bind(R.id.sede_tec_rif)
    TextView sede_tec_rif;

    @Bind(R.id.sede_tec_first_level)
    TextView sede_tec_first_level;

    @Bind(R.id.def_sede_tec)
    TextView def_sede_tec;

    @Bind(R.id.plant_scala_value)
    TextView plant_scala_value;

    @Bind(R.id.plant_interno_value)
    TextView plant_interno_value;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impianto_generali, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        mImpianto = realm.
                where(Plant.class).
                equalTo("sedetec",
                        getArguments().getString("IMPIANTO")).
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
         * http://www.hostedredmine.com/issues/509608
         * Provincia
         */
        String __indirizzo = Utils.cleanNullableValue(mImpianto.getAddressComplete()) + " " +
                "(" + Utils.cleanNullableValue(mImpianto.getProvincia()) + ")";
        plant_city.setText(Utils.cleanNullableValue(__indirizzo));

        plant_zone.setText(
                Utils.cleanNullableValue(mImpianto.getMunicipio()));

        plant_flor.setText(
                Utils.cleanNullableValue(mImpianto.getPiano()));

        plant_position.setText(
                Utils.cleanNullableValue(mImpianto.getUbicazione()));

        sede_tec_rif.setText(Utils.cleanNullableValue(mImpianto.getSettIndRif()));
        sede_tec_first_level.setText(Utils.cleanNullableValue(mImpianto.getSettIndFath()));
        def_sede_tec.setText(Utils.cleanNullableValue(mImpianto.getDefsedetecFat()));

        /**
         * Load odl map preview
         */
        String lat = null;
        String lng = null;
        String address = null;
        if (Utils.isGeoLocationComplete(mImpianto)) {
            lat = mImpianto.getLatitude();
            lng = mImpianto.getLongitude();
        } else {
            address = mImpianto.getAddressComplete();
        }
        Utils.printStaticMap(getActivity(), lat, lng ,google_map_preview);

        /**
         * http://www.hostedredmine.com/issues/509608
         * Scala
         * Interno
         */
        plant_scala_value.setText(Utils.cleanNullableValue(mImpianto.getScala()));
        plant_interno_value.setText(Utils.cleanNullableValue(mImpianto.getInterno()));
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    private final static String TAG = FragmentImpiantoGenerali.class.getName();

};