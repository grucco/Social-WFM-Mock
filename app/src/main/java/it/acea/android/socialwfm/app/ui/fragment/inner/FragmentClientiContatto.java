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
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Customer;

/**
 * Created by Raphael on 16/11/2015.
 */
public class FragmentClientiContatto extends BaseFragment {

    private Customer mCliente;

    @Bind(R.id.nome)
    TextView nome;

    @Bind(R.id.telefono_fisso)
    TextView telefono_fisso;

    @Bind(R.id.telefono_cellulare)
    TextView telefono_cellulare;

    @Bind(R.id.fax)
    TextView fax;

    @Bind(R.id.ruolo)
    TextView ruolo;

    @Bind(R.id.mail)
    TextView mail;

    @Bind(R.id.address_line)
    TextView address_line;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente_contatto, container, false);
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
        nome.setText(Utils.cleanNullableValue(mCliente.getFullName()));
        telefono_fisso.setText(Utils.cleanNullableValue(mCliente.getMobphone()));
        telefono_cellulare.setText(Utils.cleanNullableValue(mCliente.getCellphone()));
        fax.setText(Utils.cleanNullableValue(mCliente.getFax()));
        ruolo.setText(Utils.cleanNullableValue(mCliente.getRole()));
        mail.setText(Utils.cleanNullableValue(mCliente.getEmail()));

        String __indirizzo = Utils.cleanNullableValue(mCliente.getFullAddress()) + " " +
                Utils.cleanNullableValue(mCliente.getCity()) +
                "(" + Utils.cleanNullableValue(mCliente.getRegion()) + ")";
        address_line.setText(Utils.cleanNullableValue(__indirizzo));
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }
};