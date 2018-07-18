package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.app.ui.adapter.DettaglioImpiantoClientePagerAdapter;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentPlant;

/**
 * Created by a.simeoni on 14/11/2016.
 */

public class ImpiantoClienteDialog extends DialogFragment {

    @Bind(R.id.htab_maincontent)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.htab_header)
    ImageView imageView;
    @Bind(R.id.htab_tabs)
    SlidingTabLayout tabLayout;
    @Bind(R.id.htab_viewpager)
    ViewPager viewPager;
    @Bind(R.id.htab_toolbar)
    Toolbar toolbar;

    DettaglioImpiantoClientePagerAdapter pagerAdapter;

    private Odl odl;

    public static ImpiantoClienteDialog newInstance(String odl){
        Bundle bundle = new Bundle();
        bundle.putString("id_odl",odl);
        ImpiantoClienteDialog fragment = new ImpiantoClienteDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setNoTitle();//Va eseguito prima dell'inflate del layout
        View view = inflater.inflate(R.layout.dialog_dettaglio_impianto_cliente,container,false);
        ButterKnife.bind(this,view);
        initFromArguments();
        initStaticMap();
        initViewPager();
        return view;
    }

    private void setNoTitle() {
        try{
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //imposta il valore dell'ombra
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.5f;
        window.setAttributes(windowParams);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO:Impostare animazione per entrata ed uscita fragment
        /*getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;*/
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity != null && activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    private void initViewPager(){
        pagerAdapter = new DettaglioImpiantoClientePagerAdapter(getChildFragmentManager());
        pagerAdapter.addFrag(new FragmentPlant(),"Impianto");
        pagerAdapter.addFrag(new FragmentPlant(),"Cliente");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setSelectedIndicatorColors(Color.WHITE);
        tabLayout.setTextColor(R.color.white);
        tabLayout.setViewPager(viewPager);

    }

    private void initFromArguments(){
        String odlId = (String)getArguments().get("id_odl");
        if(odlId!=null){
            this.odl = Realm.getDefaultInstance().where(Odl.class)
                            .equalTo("customKey",odlId)
                            .findFirst();
        }
    }

    private void initStaticMap(){
        if(odl != null){
            Plant plant = odl.getPlant();
            if(plant!=null){
                try{
                    int latInt,longInt;
                    latInt  = (int)Double.parseDouble(plant.getLatitude());
                    longInt = (int)Double.parseDouble(plant.getLongitude());
                    if(latInt!=0&&longInt!=0){
                        Utils.printStaticMap(getActivity(),plant.getLatitude(),plant.getLongitude(),imageView);
                    }
                }
                catch(NumberFormatException ex){
                    //In questo caso lascio il placeholder
                    ex.printStackTrace();
                }

            }

        }
    }
}
