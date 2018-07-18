package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.ImageSquare;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Customer;

/**
 * Created by Raphael on 16/11/2015.
 */
public class FragmentClientiGenerali extends BaseFragment {

    private Customer mCliente;

    @Bind(R.id.ruolo_utenza)
    TextView ruolo_utenza;

    @Bind(R.id.business_partner)
    TextView business_partner;

    @Bind(R.id.tipologia)
    TextView tipologia;

    @Bind(R.id.citta)
    TextView citta;

    @Bind(R.id.indirizzo)
    TextView indirizzo;

    @Bind(R.id.zona)
    TextView zona;

    @Bind(R.id.piano)
    TextView piano;

    @Bind(R.id.ubicazione)
    TextView ubicazione;

    @Bind(R.id.mappa)
    ImageSquare mappa;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente_generali, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        mCliente = realm.
                where(Customer.class).
                equalTo("bp", getArguments().getString("CLIENTE")).
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
        ruolo_utenza.setText(Utils.cleanNullableValue(mCliente.getRuoloutenza()));
        business_partner.setText(Utils.cleanNullableValue(mCliente.getBp()));
        tipologia.setText(Utils.cleanNullableValue(mCliente.getTiputenza()));
        citta.setText(Utils.cleanNullableValue(mCliente.getCity()));

        String __indirizzo = Utils.cleanNullableValue(mCliente.getFullAddress()) + " " +
                Utils.cleanNullableValue(mCliente.getCity()) +
                "(" + Utils.cleanNullableValue(mCliente.getRegion()) + ")";
        indirizzo.setText(Utils.cleanNullableValue(__indirizzo));
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
