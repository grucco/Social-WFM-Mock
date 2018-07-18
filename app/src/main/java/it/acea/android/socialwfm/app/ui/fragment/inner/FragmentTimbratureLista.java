package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.ui.adapter.TimbratureRecyclerViewAdapter;
import it.acea.android.socialwfm.app.ui.fragment.TimbratureFragment;
import it.acea.android.socialwfm.app.ui.intro.IntroGiustificativiLista;
import it.acea.android.socialwfm.app.ui.intro.IntroTimbratureLista;
import tr.xip.errorview.ErrorView;

/**
 * Created by a.simeoni on 16/03/2016.
 */
public class FragmentTimbratureLista extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FragmentTimbratureLista.class.getSimpleName();
    @Bind(R.id.timbrature_rv)
    public EmptyRecyclerView rvTimbrature;

    @Bind(R.id.empty_view)
    public ErrorView emptyView;

    @Bind(R.id.timbrature_lista_date_select)
    TextView dateSelect;

    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.trimbrature_lista_data_label)
    public TextView dataLabel;

    private int currentItemsSort = -1;
    /*@Bind(R.id.timbrature_add)
    CardView timbratureAggiungi;*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timbrature_lista, container, false);
        ButterKnife.bind(this, v);
        initRecyclerViewStyle();
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void initRecyclerViewStyle() {
        bindData(null);
        rvTimbrature.setEmptyView(emptyView);
        rvTimbrature.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTimbrature.setHasFixedSize(true);
        rvTimbrature.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(ContextCompat.getColor(getContext(), R.color.ess_recycler_vier_divider))
                        .sizeResId(R.dimen.divider)
                        .build());
    }

    @Subscribe
    public void showSortDialog(TimbratureFragment.MessageOrdinaLista event) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.timbrature_ordina_per_data)
                .items(R.array.timbrature_sort_items)
                .itemsCallbackSingleChoice(currentItemsSort, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                currentItemsSort = which;
                                sortTimbraturaPiuRecente();
                                break;
                            case 1:
                                currentItemsSort = which;
                                sortTimbraturaMenoRecente();
                                break;
                        }
                        return false;
                    }
                })
                .alwaysCallInputCallback().show();
    }

    private void sortTimbraturaMenoRecente() {
        TimbratureRecyclerViewAdapter ad = (TimbratureRecyclerViewAdapter) this.rvTimbrature.getAdapter();
        ad.sortTimbraturaMenoRecente();
        ad.notifyDataSetChanged();
        dataLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
    }

    private void sortTimbraturaPiuRecente() {
        TimbratureRecyclerViewAdapter ad = (TimbratureRecyclerViewAdapter) this.rvTimbrature.getAdapter();
        ad.sortTimbraturaPiuRecente();
        ad.notifyDataSetChanged();
        dataLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
    }

    @Subscribe(sticky = true)
    public void onDataFetched(TimbratureFragment.MessageTimbraburaEvent event) {
        cleanCompounds();
        bindData(event.timbratura);
    }

    @Subscribe(sticky = true)
    public void setDateField(TimbratureFragment.MessageDateSet event) {
        Calendar c = Calendar.getInstance();
        c.set(event.year, event.monthOfYear, event.dayOfMonth);
        dateSelect.setText(Utils.dateFormat(c.getTime()));
    }

    private void bindData(@Nullable Timbratura timbratura) {
        List<Timbratura.TimbraturaOrario> list = null;
        if (timbratura != null) {
            list = timbratura.getOrari();
        } else {
            list = new ArrayList<>();
        }
        TimbratureRecyclerViewAdapter adapter = new TimbratureRecyclerViewAdapter(getContext(), list);
        adapter.sortTimbraturaPiuRecente();
        rvTimbrature.setAdapter(adapter);
    }

    @OnClick(R.id.timbrature_lista_date_select)
    public void showDatePicker() {
        EventBus.getDefault().post(new TimbratureFragment.MessageShowDatePicker());
    }

   /*
    @OnClick(R.id.timbrature_add)
    public void aggiungiTimbratura() {
        EventBus.getDefault().post(new TimbratureFragment.MessageAggiungiTimbratura());
    }*/

    public void showIntro(){
        // TODO: Capire perchè il getActivity() restituisce null
        // dobbiamo invocarlo per poter recuperare un riferimento al menu contestuale
    }
    
    @Override
    public void onRefresh() {
        cleanCompounds();
        currentItemsSort = -1;
        this.swipeRefreshLayout.setRefreshing(false);
        EventBus.getDefault().post(new TimbratureFragment.MessageRequestUpdate());
    }

    private void cleanCompounds() {
        dataLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
