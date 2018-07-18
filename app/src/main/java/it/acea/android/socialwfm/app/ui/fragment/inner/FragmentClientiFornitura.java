package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Customer;

/**
 * Created by Raphael on 16/11/2015.
 */
public class FragmentClientiFornitura extends BaseFragment {

    private Customer mCliente;

    @Bind(R.id.tipologia_contratto)
    TextView tipologia_contratto;

    @Bind(R.id.condcontr_value)
    TextView condcontr_value;

    @Bind(R.id.role_user_value)
    TextView role_user_value;

    @Bind(R.id.tariffa)
    TextView tariffa;

    @Bind(R.id.optional)
    TextView optional;

    @Bind(R.id.term_special_value)
    TextView term_special_value;

    @Bind(R.id.importo_scaduto_value)
    TextView importo_scaduto_value;

    @Bind(R.id.importo_moroso_value)
    TextView importo_moroso_value;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente_fornitura, container, false);
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

        //numero_contratto.setText(Utils.cleanNullableValue(mCliente.getDispAgg()));
        /**
         * http://www.hostedredmine.com/issues/499598
         * Fornitura
         * campi da mostrare
         * Contratto
         * ruolo utenza
         * tipologia contratto
         * condizioni contrattuali
         * Optional - Modificare label in Disposizioni aggiuntive
         * Term. speciali - Modificare label in Esigenze speciali
         */
        role_user_value.setText(Utils.cleanNullableValue(mCliente.getRuoloutenza()));
        tipologia_contratto.setText(Utils.cleanNullableValue(mCliente.getTiputenza()));
        optional.setText(Html.fromHtml(mCliente.getDispAgg()));
        condcontr_value.setText(Html.fromHtml(Utils.cleanNullableValue(mCliente.getCondcontract())));
        term_special_value.setText(Utils.cleanNullableValue(mCliente.getEsigspecial()));

        /**
         * http://www.hostedredmine.com/issues/509626
         * tariffa
         * Impscad
         * Impmor
         */
        tariffa.setText(Utils.cleanNullableValue(mCliente.getTariftype()));
        importo_scaduto_value.setText(Utils.cleanNullableValue(mCliente.getImpscad()));
        importo_moroso_value.setText(Utils.cleanNullableValue(mCliente.getImpmor()));

    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
}
