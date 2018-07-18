package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.MonteOre;
import it.acea.android.socialwfm.app.model.ess.MonteOreTipo;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.adapter.MonteOreAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroMonteOre;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by n.fiorillo on 01/03/2016.
 */
public class MonteOreFragment extends EssFragment implements CalendarDatePickerDialogFragment.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MonteOreFragment.class.getSimpleName();
    @Bind(R.id.monte_ore_recycler)
    public EmptyRecyclerView recyclerView;

    @Bind(R.id.monte_ore_tipi_spinner)
    public Spinner spinner;

    @Bind(R.id.monte_ore_data_select)
    public TextView dataSelect;

    @Bind(R.id.error_view)
    public ErrorView errorView;

    @Bind(R.id.empty_view)
    public ErrorView emptyView;

    @Bind(R.id.monte_ore_inizio_riduzione_tv)
    public TextView inizioRiduzioneTv;

    @Bind(R.id.monte_ore_fine_riduzione_tv)
    public TextView fineRiduzioneTv;

    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private List<MonteOre> lista = new ArrayList<>();
    private Calendar dataRequest = Calendar.getInstance();

    private int ordinamentoCorrente = -1;

    private View aggiornaView;
    private View ordinaView;

    public MonteOreFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monte_ore, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
        dataSelect.setText(Utils.dateFormat(new Date()));
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                fetchData();
                dismissError();
            }
        });
        return view;
    }

    private void initRecyclerView() {
        setRecycleViewAdapter(lista);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(ContextCompat.getColor(getContext(), R.color.ess_recycler_vier_divider))
                        .sizeResId(R.dimen.divider)
                        .build());
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = Navigator.with(getActivity()).utils().getActualTag();
        if (!tag.endsWith(MonteOreFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_aggiorna:
                    fetchMonteOre();
                    return true;
                case R.id.menu_ordina:
                    showSortDialog();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_aggiorna);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void showIntro() {
        View actionView = getActivity().findViewById(R.id.menu_overflow);
        new IntroMonteOre(getActivity(), spinner, dataSelect, actionView).start();
    }

    @OnClick(R.id.monte_ore_data_select)
    public void showDatePicker() {
        int y = dataRequest.get(Calendar.YEAR);
        int m = dataRequest.get(Calendar.MONTH);
        int d = dataRequest.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setPreselectedDate(y, m, d);
        cdp.setOnDateSetListener(this);
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getFragmentManager(), "");
    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.monte_ore);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        dataRequest.set(year, monthOfYear, dayOfMonth);
        dataSelect.setText(Utils.dateFormat(dataRequest.getTime()));
        fetchMonteOre();
    }

    private void setSpinnerAdapter(List<MonteOreTipo> listaMonteOre) {
        ArrayAdapter<MonteOreTipo> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_monte_ore_tipo, listaMonteOre);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_monte_ore_tipo);
        spinner.setAdapter(adapter);
        spinner.setSelection(Adapter.NO_SELECTION, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchMonteOre();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<MonteOreTipo> getFakeMonteOreTipo() {

        ArrayList<MonteOreTipo> m = new ArrayList<>();
        MonteOreTipo m1 = new MonteOreTipo();
        MonteOreTipo m2 = new MonteOreTipo();
        MonteOreTipo m3 = new MonteOreTipo();
        MonteOreTipo m4 = new MonteOreTipo();
        m1.setId("1");
        m1.setDescrizione("Ferie Anno Corrente GG");
        m2.setId("2");
        m2.setDescrizione("Conto Ore AC");
        m3.setId("3");
        m3.setDescrizione("Festività Soppresse AC");
        m4.setId("4");
        m4.setDescrizione("Assemblea Sindacale");
        m.add(m1);
        m.add(m2);
        m.add(m3);
        m.add(m4);
        return m;

    }

    private void fetchMonteOreTipo() {
        showProgressDialog();
        EssClient.fetchMonteOreTipoList(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    List<MonteOreTipo> list = getFakeMonteOreTipo(); //ResponseManager.getListaMonteOreTipo(e, result);
                    setSpinnerAdapter(list);
                } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    showError();
                    dataErrorExpection.printStackTrace();

                }*/ catch (Exception exception) {
                    showError();
                    exception.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });

    }


    private ArrayList<MonteOre> getFakeMonteOre() {

        ArrayList<MonteOre> m = new ArrayList<>();
        MonteOre m1 = new MonteOre();
        MonteOre m2 = new MonteOre();
        MonteOre m3 = new MonteOre();
        MonteOre m4 = new MonteOre();

        m1.setTipo("Ferie Anno Corrente GG");
        m1.setDescrizione("Ferie Anno Corrente GG");
        m1.setInizioRiduzione(new GregorianCalendar(2018,0,1).getTime());
        m1.setFineRiduzione(new GregorianCalendar(2018,12,31).getTime());
        m1.setPianificato("26,00 Giorni");
        m1.setGoduto("0,00 Giorni");

        m2.setTipo("Conto Ore AC");
        m2.setDescrizione("Conto Ore AC");
        m2.setInizioRiduzione(new GregorianCalendar(2018,1,2).getTime());
        m2.setFineRiduzione(new GregorianCalendar(2018,11,30).getTime());
        m2.setPianificato("96,00 Ore");
        m2.setGoduto("0,00 Ore");

        m3.setTipo("Festività Soppresse AC");
        m3.setDescrizione("Festività Soppresse AC");
        m3.setInizioRiduzione(new GregorianCalendar(2018,0,1).getTime());
        m3.setFineRiduzione(new GregorianCalendar(2018,12,31).getTime());
        m3.setPianificato("28,80 Ore");
        m3.setGoduto("0,00 Ore");

        m4.setTipo("Assemblea Sindacale");
        m4.setDescrizione("Assemblea Sindacale");
        m4.setInizioRiduzione(new GregorianCalendar(2018,1,2).getTime());
        m4.setFineRiduzione(new GregorianCalendar(2018,11,30).getTime());
        m4.setPianificato("12,00 Ore");
        m4.setGoduto("0,00 Ore");

        m.add(m1);
        m.add(m2);
        m.add(m3);
        m.add(m4);

        return m;

    }

    private void fetchMonteOre() {
        cleanCompounds();
        ordinamentoCorrente=-1;
        showProgressDialog();
        String id = "";
        try {
            MonteOreTipo m = (MonteOreTipo) spinner.getSelectedItem(); //PROBLEMA: è null
            //id = "900946e2-7db9-4368-b98b-9accb3afec14";
            id = ((MonteOreTipo) spinner.getSelectedItem()).getId();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        EssClient.fetchMonteOreList(getContext(), id, dataRequest.getTime(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                List<MonteOre> list = null;
                try {
                    list = getFakeMonteOre(); //ResponseManager.getListaMonteOre(e, result);
                } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                    dataErrorExpection.printStackTrace();
                }*/ catch (Exception exception) {
                    showError();
                    exception.printStackTrace();
                } finally {
                    dismissDialog();
                    if (list == null) list = new ArrayList<>();
                    setRecycleViewAdapter(list);

                   if(!Utils.isEmptyList(list)){
                     showIntro();
                   }
                }
            }
        });
    }

    private void setRecycleViewAdapter(List<MonteOre> listaMonteOre) {
        ArrayList<MonteOre> tmplist = new ArrayList<MonteOre>(listaMonteOre);
        lista.clear();
        lista.addAll(tmplist);
        MonteOreAdapter adapter = new MonteOreAdapter(getContext(), listaMonteOre);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public ErrorView getErrorView() {
        return errorView;
    }

    private void fetchData() {
        fetchMonteOre();
        fetchMonteOreTipo();
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(false);
        fetchMonteOre();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.menu_monte_ore);
        item.setIcon(R.drawable.ic_monte_ore_on);
        menu.findItem(R.id.menu_ordina).setVisible(true);
    }

    public void showSortDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.monte_ore_ordina_per_data)
                .items(R.array.monte_ore_sort_items)
                .itemsCallbackSingleChoice(ordinamentoCorrente, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        cleanCompounds();
                        switch (which) {
                            case 0:
                                sortInizioRiduzioneAsc();
                                break;
                            case 1:
                                sortInizioRiduzioneDesc();
                                break;
                            case 2:
                                sortFineRiduzioneAsc();
                                break;
                            case 3:
                                sortFineRiduzioneDesc();
                                break;
                        }
                        ordinamentoCorrente = which;
                        return false;
                    }
                })
                .alwaysCallInputCallback().show();
    }

    private void sortInizioRiduzioneAsc() {
        if (lista == null) return;

        Collections.sort(lista, MonteOre.INIZIO_RIDUZIONE_ASC);
        setRecycleViewAdapter(lista);
        inizioRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
    }

    private void sortInizioRiduzioneDesc() {
        if (lista == null) return;
        Collections.sort(lista, MonteOre.INIZIO_RIDUZIONE_DESC);
        setRecycleViewAdapter(lista);
        inizioRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
    }

    private void sortFineRiduzioneAsc() {
        if (lista == null) return;
        Collections.sort(lista, MonteOre.FINE_RIDUZIONE_ASC);
        setRecycleViewAdapter(lista);
        fineRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
    }

    private void sortFineRiduzioneDesc() {
        if (lista == null) return;
        Collections.sort(lista, MonteOre.FINE_RIDUZIONE_DESC);
        setRecycleViewAdapter(lista);
        fineRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
    }

    private void cleanCompounds() {
        inizioRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        fineRiduzioneTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}
