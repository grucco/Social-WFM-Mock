package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.events.ErroreDownloadEvent;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.adapter.pager.GiustificativiAdapter;
import it.acea.android.socialwfm.app.ui.dialog.LegendaDialog;
import tr.xip.errorview.ErrorView;

/**
 * Created by raphaelbussa on 17/03/16.
 */
public class GiustificativiFragment extends EssFragment implements FutureCallback<JsonObject> {

    @Bind(R.id.tabs)
    SlidingTabLayout slidingTabLayout;

    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.error_view)
    ErrorView errorView;

    private GiustificativiAdapter adapter;

    @Subscribe
    public void onError(ErroreDownloadEvent event) {
        showError();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_giustificativi, null);
        ButterKnife.bind(this, view);
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                errorView.setVisibility(View.GONE);
                aggiorna();
            }
        });
        return view;
    }

    private Menu menu;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        MenuItem item = menu.findItem(R.id.menu_giustificativi);
        item.setIcon(R.drawable.ic_giustificativi_on);
        menu.findItem(R.id.menu_legenda).setVisible(false);
        menu.findItem(R.id.menu_messaggi).setVisible(false);
        menu.findItem(R.id.menu_ordina).setVisible(true);
        menu.findItem(R.id.menu_aggiorna).setVisible(true);

    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.giustificativi);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = Navigator.with(getActivity()).utils().getActualTag();
        if (!tag.endsWith(GiustificativiFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_ordina:
                    if (pager.getCurrentItem() == 0) {
                        adapter.getListFragment().filterData();
                    }
                    return true;
                case R.id.menu_aggiorna:
                    aggiorna();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    private void aggiorna() {
        if (pager.getCurrentItem() == 0) {
            adapter.getListFragment().reloadData();
        } else {
            adapter.getCalendarioFragment().reloadData();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GiustificativiAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    menu.findItem(R.id.menu_ordina).setVisible(true);
                } else {
                    menu.findItem(R.id.menu_ordina).setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.secondary_ess));
        slidingTabLayout.setTextColor(R.color.md_blue_grey_800);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onCompleted(Exception e, JsonObject result) {

    }

    @Override
    public ErrorView getErrorView() {
        return errorView;
    }

    @Override
    public void showIntro() {
        // La gestione delle intro è delegata ai fragment nel pager:
        // GiustificativiListFragment e GiustificativiCalendarioFragment
    }

}
