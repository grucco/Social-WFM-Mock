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

import com.mikepenz.iconics.view.IconicsTextView;

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
import it.acea.android.socialwfm.app.ui.adapter.ClientiAdapter;

/**
 * Created by Raphael on 16/11/2015.
 */
public class ClientiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.cliente)
    TextView cliente;

    @Bind(R.id.impianto)
    TextView impianto;

    @Bind(R.id.pagamenti)
    IconicsTextView pagamenti;

    @Bind(R.id.list_post)
    RecyclerView listPost;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.tab_layout)
    SlidingTabLayout tabLayout;

    @Bind(R.id.pager)
    ViewPager pager;

    private Odl mOdl;
    private Customer mCliente;
    private Plant mImpianto;

    private ClientiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cliente, container, false);
        setSubtitle(R.string.cliente);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        mOdl = realm.where(Odl.class)
                .equalTo("aufnr",
                        getArguments().getString("actual.odl.selected")).findFirst();


        mCliente = mOdl.getCustomer();
        mImpianto = mOdl.getPlant();

        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        refreshLayout.setOnRefreshListener(this);
        buildFragmentData();
    }

    private void buildFragmentData() {

        adapter = new ClientiAdapter(getChildFragmentManager(), mCliente, getActivity());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        tabLayout.setSelectedIndicatorColors(Color.WHITE);
        tabLayout.setTextColor(R.color.white);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(pager);

        cliente.setText(getActivity().getResources()
                .getString(R.string.mywork_customer_id, mCliente.getBp()));
        if (!TextUtils.isEmpty(mCliente.getBpstatus()) && mCliente.getBpstatus().equalsIgnoreCase("Good Payer")) {
            pagamenti.setText(
                    getActivity().getResources().getString(R.string.good_customer_icon, mCliente.getBpstatus()));
        } else {
            pagamenti.setText(Utils.cleanNullableValue(mCliente.getBpstatus()));
        }

        impianto.setText(getActivity().getResources().getString(R.string.impianto_label, Utils.cleanNullableValue(mImpianto.getAnlage())));
        impianto.setTag(R.id.actual_odl_selected, mOdl.getAufnr());
        impianto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof SupportActivity) {
                    ((SupportActivity) getActivity()).navigateToNextStep(
                            (String) v.getTag(R.id.actual_odl_selected), ImpiantiFragment.class.getName()
                    );
                }
                //navigateToNextStep((ODLBean) v.getTag(R.id.actual_odl_selected), ImpiantiFragment.class.getName());
            }
        });

        if (!TextUtils.isEmpty(mCliente.getBpstatus()) && mCliente.getBpstatus().equalsIgnoreCase("Good Payer")) {
            pagamenti.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_good, 0);
        } else if (!TextUtils.isEmpty(mCliente.getBpstatus()) && mCliente.getBpstatus().equalsIgnoreCase("Intermediate Payer")) {
            pagamenti.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_normal, 0);
        } else if (!TextUtils.isEmpty(mCliente.getBpstatus()) && mCliente.getBpstatus().equalsIgnoreCase("Bad Payer")) {
            pagamenti.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_bad, 0);
        } else {
            pagamenti.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
