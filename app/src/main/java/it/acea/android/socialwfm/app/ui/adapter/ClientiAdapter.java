package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.odl.Customer;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentClientiContatto;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentClientiFornitura;

/**
 * Created by Raphael on 04/11/2015.
 */
public class ClientiAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Customer cliente;
    private String[] title;

    public ClientiAdapter(FragmentManager fm, Customer cliente, Context context) {
        super(fm);
        this.cliente = cliente;
        this.context = context;
        this.title = context.getResources().getStringArray(R.array.tab_cliente);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("CLIENTE", cliente.getBp());
        Fragment fragment = null;
        switch (position) {
            /*case 0:
                fragment = Fragment.instantiate(context, FragmentClientiGenerali.class.getName());
                break;*/
            case 0:
                fragment = Fragment.instantiate(context, FragmentClientiFornitura.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(context, FragmentClientiContatto.class.getName());
                break;
        }
        if (fragment != null) fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
