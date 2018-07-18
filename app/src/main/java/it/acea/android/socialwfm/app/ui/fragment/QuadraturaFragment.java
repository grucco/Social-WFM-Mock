package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Quadratura;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.adapter.QuadraturaAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroAnalisiQuadratura;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by a.simeoni on 29/03/2016.
 */
public class QuadraturaFragment extends EssFragment implements CalendarDatePickerDialogFragment.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    @Bind(R.id.quadratura_recycler)
    EmptyRecyclerView quadraturaRecyclerView;
    @Bind(R.id.error_view)
    public ErrorView errorView;
    @Bind(R.id.quadratura_data_select)
    TextView tvMeseAnnoCorrente;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.quadratura_data_inserimento)
    TextView tvDataInserimento;

    @Bind(R.id.empty_view)
    public ErrorView emptyView;

    QuadraturaAdapter adapter;
    Date period;
    int currentsort = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quadratura, container, false);
        ButterKnife.bind(this, view);
        initRecyclerViewStyle();

        period = new Date();

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                dismissError();
            }
        });

        // Pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fetchDataList(period);
    }

    private void resetState() {
        cleanCompounds();
        currentsort = -1;
    }

    private void setRecyclerViewAdapter(List<Quadratura> list) {
        List<Quadratura> l = (list == null) ? new ArrayList<Quadratura>() : list;
        adapter = new QuadraturaAdapter(l);
        quadraturaRecyclerView.setAdapter(adapter);
    }


    private ArrayList<Quadratura> getFakeQuadratura() {

        Quadratura q1 = new Quadratura();
        Quadratura q2 = new Quadratura();
        Quadratura q3 = new Quadratura();
        Quadratura q4 = new Quadratura();

        q1.setEname("Mario Rossi");
        q1.setError("01");
        q1.setEtext("Dipendente assente");
        q1.setKurzt("0015425312");
        q1.setLdate(new GregorianCalendar(2018,2,14).getTime());
        q1.setPernr("0015425312");

        q2.setEname("Gennaro Esposito");
        q2.setError("01");
        q2.setEtext("Dipendente assente");
        q2.setKurzt("0015234511");
        q2.setLdate(new GregorianCalendar(2018,4,7).getTime());
        q2.setPernr("0015234511");

        q3.setEname("Mario Balotelli");
        q3.setError("01");
        q3.setEtext("Dipendente assente");
        q3.setKurzt("0034425120");
        q3.setLdate(new GregorianCalendar(2018,1,22).getTime());
        q3.setPernr("0034425120");

        q4.setEname("Leo Messi");
        q4.setError("01");
        q4.setEtext("Dipendente assente");
        q4.setKurzt("0015235315");
        q4.setLdate(new GregorianCalendar(2018,5,14).getTime());
        q4.setPernr("0015235315");

        ArrayList<Quadratura> qq = new ArrayList<>();
        qq.add(q1);
        qq.add(q2);
        qq.add(q3);
        qq.add(q4);

        return qq;

    }

    private void fetchDataList(final Date date) {

        showProgressDialog();
        resetState();
        EssClient.fetchAnomalieQuadraturaSet(getContext(), date,
                new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        dismissDialog();
                        period = date;
                        showMeseAnnoCorrente(period);
                        ArrayList<Quadratura> lista = new ArrayList<Quadratura>();
                        try {
                            lista = getFakeQuadratura(); //null; //(ArrayList<Quadratura>) ResponseManager.getListaAnomalieQuadratura(e, result);
                        } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                            new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                        } */catch (Exception e1) {
                            showError();
                        } finally {
                            setRecycleViewAdapter(lista);
                        }
                    }
                });
    }

    @OnClick(R.id.quadratura_data_select)
    public void showDatePicker() {
        Calendar day = Calendar.getInstance();
        day.setTime(this.period);
        int y = day.get(Calendar.YEAR);
        int m = day.get(Calendar.MONTH);
        int d = day.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setPreselectedDate(y, m, d);
        cdp.setOnDateSetListener(this);
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getFragmentManager(), "");
    }

    private void cleanCompounds() {
        tvDataInserimento.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void showSortDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.quadratura_orina_per_data)
                .items(R.array.quadratura_sort_items)
                .itemsCallbackSingleChoice(currentsort, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        cleanCompounds();
                        currentsort = which;
                        switch (which) {
                            case 0:
                                sortLdateDisc();
                                break;
                            case 1:
                                sortLDateAsc();
                                break;
                        }
                        return false;
                    }
                })
                .alwaysCallInputCallback().show();
    }

    private void initRecyclerViewStyle() {
        setRecyclerViewAdapter(new ArrayList<Quadratura>());
        quadraturaRecyclerView.setEmptyView(emptyView);
        quadraturaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quadraturaRecyclerView.setHasFixedSize(true);
        quadraturaRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(ContextCompat.getColor(getContext(), R.color.ess_recycler_vier_divider))
                        .sizeResId(R.dimen.divider)
                        .build());
    }


    private void showMeseAnnoCorrente(Date d) {
        String text = getMeseAnnoStringFromDate(d);
        this.tvMeseAnnoCorrente.setText(text);
    }

    private String getMeseAnnoStringFromDate(Date d) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ITALIAN);
        return dateFormat.format(d);
    }

    private void setRecycleViewAdapter(List<Quadratura> list) {
        adapter = new QuadraturaAdapter(list);
        quadraturaRecyclerView.setAdapter(adapter);
        // Se ho i dati mostro intro
        if(!Utils.isEmptyList(list)){
            showIntro();
        }
    }


    private void sort(Comparator<Quadratura> comp, boolean asc) {
        if (adapter.getItemCount() == 0) return;
        List<Quadratura> list = adapter.list;
        Collections.sort(list, comp);
        setRecycleViewAdapter(list);
        int drawable = asc ? R.drawable.ic_arrow_asc : R.drawable.ic_arrow_desc;
        tvDataInserimento.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
    }

    private void sortLDateAsc() {
        sort(Quadratura.QUADRATURA_COMPARATOR_ASC, true);
    }

    private void sortLdateDisc() {
        sort(Quadratura.QUADRATURA_COMPARATOR_DISC, false);
    }

    @Override
    public ErrorView getErrorView() {
        return this.errorView;
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date d = calendar.getTime();
        this.fetchDataList(d);
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(false);
        fetchDataList(period);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_quadratura).setIcon(R.drawable.ic_quadratura_on);
        menu.findItem(R.id.menu_ordina).setVisible(true);

    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.quadratura);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = Navigator.with(getActivity()).utils().getActualTag();
        Log.d(TAG, tag);
        if (!tag.endsWith(QuadraturaFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_aggiorna:{
                    fetchDataList(period);
                    return true;
                }
                case R.id.menu_ordina:{
                    showSortDialog();
                    return true;
                }
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void showIntro() {
        super.showIntro();
        View v = getActivity().findViewById(R.id.menu_overflow);
        new IntroAnalisiQuadratura(getActivity(), tvMeseAnnoCorrente, v).start();
    }
}