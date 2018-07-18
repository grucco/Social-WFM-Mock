package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.WeatherRecycleAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ImpiantoClienteDialog;
import it.acea.android.socialwfm.app.ui.fragment.ClientiFragment;
import it.acea.android.socialwfm.app.ui.fragment.ImpiantiFragment;
import it.acea.android.socialwfm.app.ui.fragment.SearchPlantFeedsFragment;

/**
 * Created by fabio on 27/10/15.
 */
public class FragmentMyWorkDetail extends BaseFragment {

    private final static String TAG = FragmentMyWorkDetail.class.getName();
    @Bind(R.id.odl_time)
    TextView odl_time;
    @Bind(R.id.odl_address_1)
    TextView odl_address_1;
    @Bind(R.id.odl_address_2)
    TextView odl_address_2;
    @Bind(R.id.odl_static_map)
    ImageView odl_static_map;
    @Bind(R.id.odl_plant_info)
    TextView odl_plant_info;
    @Bind(R.id.odl_plant_user_type)
    TextView odl_plant_user_type;
    @Bind(R.id.odl_plant_location)
    TextView odl_plant_location;
    @Bind(R.id.odl_customer_info)
    TextView odl_customer_info;
    @Bind(R.id.odl_customer_payment)
    com.mikepenz.iconics.view.IconicsTextView odl_customer_payment;
    @Bind(R.id.odl_customer_terms)
    TextView odl_customer_terms;
    @Bind(R.id.progress)
    ProgressWheel progress;
    @Bind(R.id.content_data)
    View content_data;

    @Bind(R.id.linearlayout_myworkdetail_no_odl_selected)
    View noOdlSelected;

    @Bind(R.id.click_cliente)
    View click_cliente;

    @Bind(R.id.empty_view_layout)
    View emptyView;

    @Bind(R.id.empty_text_value)
    View empty_text_value;


    WeatherRecycleAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmet_mywork_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Odl mActualOdlBean;

    public void loadOdlData(String id) {
        progress.setVisibility(View.VISIBLE);
        Realm realm = Realm.getDefaultInstance();
        Odl odlBean = realm.where(Odl.class).equalTo("aufnr", id).findFirst();

        //prendo il primo ordine di lavoro (quello "più completo") dal DB al fine di
        //popolare gli altri ordini di lavoro nella lista
        String id_first = "950032412";
        Odl first_odl = realm.where(Odl.class).equalTo("aufnr", id_first).findFirst();


        if (odlBean == null) {
            content_data.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            return;
        }

        mActualOdlBean = odlBean;


        content_data.setVisibility(View.GONE);

        String lat, lng;
        if(odlBean.getPlant().getLatitude() == null)
            lat = first_odl.getPlant().getLatitude();
        else
            lat = odlBean.getPlant().getLatitude();

        if(odlBean.getPlant().getLongitude() == null)
            lng = first_odl.getPlant().getLongitude();
        else
            lng = odlBean.getPlant().getLongitude();


        Utils.printStaticMap(getActivity(), lat, lng, odl_static_map);

        /**
         * ODL Data
         */
        String _hour = Utils.formattaData(odlBean.getDatetime());
        odl_time.setText(_hour);

        /**
         * Plant info
         */
        String impianto = getContext().getString(R.string.impianto)+":";
        odl_plant_info.setText(impianto+" "+Utils.cleanNullableValue(odlBean.getPlant().getCategoria()));

        odl_address_1.setText(Utils.cleanNullableValue(((odlBean.getPlant().getAddressComplete() != null) && !odlBean.getPlant().getAddressComplete().equalsIgnoreCase("--") ) ? odlBean.getPlant().getAddressComplete() : first_odl.getPlant().getAddressComplete()));
        odl_address_2.setText(Utils.cleanNullableValue(( (odlBean.getPlant().getLocalita() != null) && !odlBean.getPlant().getLocalita().equalsIgnoreCase("--") ) ? odlBean.getPlant().getLocalita() : first_odl.getPlant().getLocalita()));

        odl_plant_user_type.setText(( Utils.cleanNullableValue(odlBean.getPlant().getRuoloutenza()).equalsIgnoreCase("--") ) ? first_odl.getPlant().getRuoloutenza() : odlBean.getPlant().getRuoloutenza());
        //odl_plant_user_type.setText(Utils.cleanNullableValue(((odlBean.getPlant().getRuoloutenza() != null) && !odlBean.getPlant().getRuoloutenza().equalsIgnoreCase("--")) ? odlBean.getPlant().getRuoloutenza() : first_odl.getPlant().getRuoloutenza()));
        odl_plant_location.setText(( Utils.cleanNullableValue(odlBean.getPlant().getUbicazione()).equalsIgnoreCase("--") ) ? first_odl.getPlant().getUbicazione() : odlBean.getPlant().getUbicazione());
        //odl_plant_location.setText(Utils.cleanNullableValue(((odlBean.getPlant().getUbicazione() != null) && !odlBean.getPlant().getUbicazione().equalsIgnoreCase("--")) ? odlBean.getPlant().getUbicazione() : first_odl.getPlant().getUbicazione()));

        if (odlBean.getCustomer() != null && !TextUtils.isEmpty(odlBean.getCustomer().getBp()) && !odlBean.getCustomer().getBp().equalsIgnoreCase("--")) {
            emptyView.setVisibility(View.GONE);
            odl_customer_info.setText(
                    getActivity().getResources()
                            .getString(R.string.mywork_customer_id, odlBean.getCustomer().getBp()));

            odl_customer_payment.setText(odlBean.getCustomer().getBpstatus());
            if (!TextUtils.isEmpty(odlBean.getCustomer().getBpstatus()) && odlBean.getCustomer().getBpstatus().equalsIgnoreCase("Good Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_good, 0);
            } else if (!TextUtils.isEmpty(odlBean.getCustomer().getBpstatus()) && odlBean.getCustomer().getBpstatus().equalsIgnoreCase("Intermediate Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_normal, 0);
            } else if (!TextUtils.isEmpty(odlBean.getCustomer().getBpstatus()) && odlBean.getCustomer().getBpstatus().equalsIgnoreCase("Bad Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_bad, 0);
            } else {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            odl_customer_terms.setText(( Utils.cleanNullableValue(odlBean.getCustomer().getCondpagamento()).equalsIgnoreCase("--") ) ? first_odl.getCustomer().getCondpagamento() : odlBean.getCustomer().getCondpagamento());
            //odl_customer_terms.setText(Utils.cleanNullableValue(odlBean.getCustomer().getCondpagamento()));
        } else {

            emptyView.setVisibility(View.GONE);
            odl_customer_info.setText(
                    getActivity().getResources()
                            .getString(R.string.mywork_customer_id, first_odl.getCustomer().getBp()));

            odl_customer_payment.setText(first_odl.getCustomer().getBpstatus());
            if (!TextUtils.isEmpty(first_odl.getCustomer().getBpstatus()) && first_odl.getCustomer().getBpstatus().equalsIgnoreCase("Good Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_good, 0);
            } else if (!TextUtils.isEmpty(first_odl.getCustomer().getBpstatus()) && first_odl.getCustomer().getBpstatus().equalsIgnoreCase("Intermediate Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_normal, 0);
            } else if (!TextUtils.isEmpty(first_odl.getCustomer().getBpstatus()) && first_odl.getCustomer().getBpstatus().equalsIgnoreCase("Bad Payer")) {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payer_bad, 0);
            } else {
                odl_customer_payment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            odl_customer_terms.setText(Utils.cleanNullableValue(first_odl.getCustomer().getCondpagamento()));
            /*
            //TODO Block customer box
            emptyView.setVisibility(View.VISIBLE);
            ((TextView) empty_text_value).setText(R.string.custom_empty_label);
            */
        }

        progress.setVisibility(View.GONE);
        noOdlSelected.setVisibility(View.GONE);
        content_data.setVisibility(View.VISIBLE);

        initPlantFeeds(odlBean);
    }

    private void initPlantFeeds(Odl odl) {
        Fragment plantFeedsFragment = new SearchPlantFeedsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("plantId", odl.getPlant().getSedetec());
        bundle.putString("anlage",  odl.getPlant().getAnlage());
        bundle.putString("activityDesc", odl.getTaskdesc());
        plantFeedsFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.feeds_impianto_fragment, plantFeedsFragment).commit();
    }


    @OnClick(R.id.click_cliente)
    public void clickOnCustomerArea() {
        Bundle bundle = new Bundle();
        bundle.putString("actual.odl.selected", mActualOdlBean.getAufnr());

        Bundle supportBundle = new Bundle();
        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
        supportBundle.putString(Constant.SUPPORT_FRAGMENT, ClientiFragment.class.getName());

        Navigator.with(getActivity())
                .build()
                .goTo(SupportActivity.class, supportBundle)
                .commit();
    }

    @OnClick(R.id.odl_click_area)
    public void clickOnPlantArea() {
        Bundle bundle = new Bundle();
        bundle.putString("actual.odl.selected", mActualOdlBean.getAufnr());

        Bundle supportBundle = new Bundle();
        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
        supportBundle.putString(Constant.SUPPORT_FRAGMENT, ImpiantiFragment.class.getName());

        Navigator.with(getActivity())
                .build()
                .goTo(SupportActivity.class, supportBundle)
                .commit();
        //ImpiantoClienteDialog dialog = ImpiantoClienteDialog.newInstance(mActualOdlBean.getCustomKey());
        //dialog.show(getFragmentManager(),"impiantoClienteDialog");
    }


    @Override
    protected void setNavigationOnClickListener() {
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    public View getContentData(){
        return content_data;
    }

    public void hideContentData() {
        noOdlSelected.setVisibility(View.VISIBLE);
        content_data.setVisibility(View.GONE);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) outRect.top = space;
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }
};