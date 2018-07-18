package it.acea.android.socialwfm.app.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Customer;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.ImpiantiAdapter;

/**
 * Created by Raphael on 16/11/2015.
 */
public class ImpiantiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.cliente)
    TextView cliente;

    @Bind(R.id.impianto)
    TextView impianto;

    @Bind(R.id.ruolo_utenza)
    TextView ruoloUtenza;

    @Bind(R.id.list_post)
    RecyclerView listPost;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.tab_layout)
    SlidingTabLayout tabLayout;

    @Bind(R.id.pager)
    ViewPager pager;

    private String mOdlId;
    private Customer mCliente;
    private Plant mImpianto;
    private ImpiantiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impianto, null);
        ButterKnife.bind(this, view);
        mOdlId = getArguments().getString("actual.odl.selected");
        setSubtitle(R.string.impianto);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        Odl odl = realm.
                where(Odl.class).
                equalTo("aufnr", mOdlId).
                findFirst();

        mCliente = odl.getCustomer();
        mImpianto = odl.getPlant();

        buildFragmentData();
    }

    private void buildFragmentData() {
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        refreshLayout.setOnRefreshListener(this);

        adapter = new ImpiantiAdapter(getChildFragmentManager(),
                mImpianto,
                getActivity());

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        tabLayout.setSelectedIndicatorColors(Color.WHITE);
        tabLayout.setTextColor(R.color.white);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(pager);

        if (mCliente != null) {
            cliente.setText(getActivity().getResources().getString(R.string.mywork_customer_id,
                    mCliente.getBp()));
            cliente.setTag(R.id.actual_odl_selected, mOdlId);
            cliente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof SupportActivity) {
                        ((SupportActivity) getActivity()).navigateToNextStep(
                                (String) v.getTag(R.id.actual_odl_selected), ClientiFragment.class.getName()
                        );
                    }
                    //navigateToNextStep((ODLBean) v.getTag(R.id.actual_odl_selected), ClientiFragment.class.getName());
                }
            });
        } else {
            cliente.setText(Utils.cleanNullableValue(null));
        }
        /**
         * http://www.hostedredmine.com/issues/499596
         * sostituire con Sede tecnica e relativo codice
         * se non c'è impianto (anlage vuoto) nascondere il campo
         */
        //if(!TextUtils.isEmpty(mImpianto.getAnlage())) {
        //    impianto.setText(getActivity().getResources().getString(R.string.impianto_label,
        //            Utils.cleanNullableValue(mImpianto.getAnlage())));
        //} else {
        impianto.setText(getActivity().getResources().getString(R.string.sede_tec_label, Utils.cleanNullableValue(mImpianto.getSedetec())));
        //}
        if (TextUtils.isEmpty(mImpianto.getRuoloutenza())) {
            ruoloUtenza.setVisibility(View.INVISIBLE);
        } else {
            ruoloUtenza.setVisibility(View.VISIBLE);
            ruoloUtenza.setText(
                    getActivity().getResources().getString(R.string.mywork_plant_user_type) + " " +
                            Utils.cleanNullableValue(mImpianto.getRuoloutenza()));
        }
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onRefresh() {

    }
}
