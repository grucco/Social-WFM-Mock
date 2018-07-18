package it.acea.android.socialwfm.app.ui.fragment;

import android.content.IntentFilter;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.breceiver.NetworkChangeReceiver;
import it.acea.android.socialwfm.app.delegate.SWFMNetworkChangeEventDelegate;
import it.acea.android.socialwfm.app.delegate.SWFMRecyclerViewClickDelegate;
import it.acea.android.socialwfm.app.delegate.SWFMSelectDataInizioFineDialogDelegate;
import it.acea.android.socialwfm.app.model.ess.CartellinoOrologio;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.adapter.CartellinoOrologioAdapter;
import it.acea.android.socialwfm.app.ui.dialog.SelectDataInizioFineDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroCartellinoOrologio;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;


/**
 * Created by a.simeoni on 03/03/2016.
 */
public class CartellinoOrologioFragment extends EssFragment implements SWFMRecyclerViewClickDelegate,
        SWFMSelectDataInizioFineDialogDelegate,
        SWFMNetworkChangeEventDelegate,
        SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    @Bind(R.id.cartellino_orologio_spinner)
    Spinner spinner;
    @Bind(R.id.cartellino_orologio_recycler_view)
    EmptyRecyclerView recyclerView;
    @Bind(R.id.cartellino_orologio_data_inizio_text)
    TextView tvDataInizio;
    @Bind(R.id.cartellino_orologio_data_fine_text)
    TextView tvDataFine;
    @Bind(R.id.error_view)
    ErrorView errorView;
    @Bind(R.id.cartellino_orologio_date_range_button)
    ImageView dateRangeView;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.empty_view)
    public ErrorView emptyView;

    private Date startDate, endDate;

    CartellinoOrologioAdapter adapter;
    NetworkChangeReceiver networkChangeReceiver;
    boolean downloadingPdf = false;
    Object pdfDownloadGroup = new Object();// Per cancellare richieste pendenti di Ion


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartellino_orologio, container, false);
        ButterKnife.bind(this, view);

        // Recyclerview
        initRecyclerViewStyle();

        // Spinner
        this.setSpinnerAdapter();

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                dismissError();
                fetchDataList(getSpinnerSelectedKey());//carico i dati con l'ultima modalità scelta
            }
        });

        // Pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(this);

        //Gestisce la disconnessione se un download è in corso
        networkChangeReceiver = new NetworkChangeReceiver(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.fetchDataList(getSpinnerKeys()[0]);
    }

    private void initRecyclerViewStyle() {
        setRecyclerViewAdapter(new ArrayList<CartellinoOrologio>());
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
    public void onPause() {
        super.onPause();
        unregisterNetworkStatusChangeReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkStatusChangeReceiver();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    private void setSpinnerAdapter() {
        String[] array = getContext().getResources().getStringArray(R.array.cartellino_orologio_spinner_values);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_cartellino_orologio_tipo, array);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_cartellino_orologio_tipo);
        spinner.setAdapter(adapter);
        spinner.setSelection(Adapter.NO_SELECTION, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] keys = getContext().getResources().getStringArray(R.array.cartellino_orologio_spinner_keys);
                String selectedKey = keys[position];
                fetchDataList(selectedKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public ErrorView getErrorView() {
        return this.errorView;
    }

    // Il primo elemento della lista non va visualizzato, ma vanno utilizzate le date per popolare il date picker
    private void handleList(List<CartellinoOrologio> l) {
        this.startDate = l.get(0).getBegda();
        this.endDate = l.get(0).getEndda();
        l.remove(0);
    }


    @Override
    public synchronized void onNetworkDisconnected() {
        Log.d(TAG, "********* Disconnesso");
        if (downloadingPdf) {
            downloadingPdf = false;
            Ion.getDefault(getContext()).cancelAll(this.pdfDownloadGroup);
            dismissDialog();
            showErrorDialog();
        }
    }

    @Override
    public synchronized void onNetworkAvailable() {
        Log.d(TAG, "********* Connesso");
    }

    private void registerNetworkStatusChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getContext().registerReceiver(this.networkChangeReceiver, filter);
    }

    private void unregisterNetworkStatusChangeReceiver() {
        getContext().unregisterReceiver(this.networkChangeReceiver);
    }

    private void downloadPdf(Date d1, Date d2) {
        final Date d1f, d2f;
        d1f = d1;
        d2f = d2;
        showProgressDialog();
        downloadingPdf = true;
        // Ion non va da errore se sparisce la rete durante il download
        // metto in ascolto un receiver che gestisce la disconnessione

        EssClient.fetchCartellinoSelectionPdf(getContext(), d1f, d2f, new FutureCallback<Response<File>>() {
            @Override
            public void onCompleted(Exception e, Response<File> result) {
                downloadingPdf = false;
                try {
                    ResponseManager.getPDFCartellinoOrologio(e, result);
                    dismissDialog();
                    Utils.launchOpenPdfIntent(getContext(),result.getResult());
                } catch (ResponseManager.DataErrorExpection de) {
                    dismissDialog();
                    showErrorDialogWithPossibilityToChooseNewDatesRange(R.string.cartellino_download_fallito, R.string.cartellino_download_fallito_controlla_range);
                } catch (Exception e1) {
                    dismissDialog();
                    showErrorDialogWithRetry(d1f, d2f);
                }
            }
        });
    }

    private void showErrorDialog() {
        showErrorDialog(R.string.cartellino_download_fallito, R.string.cartellino_download_fallito_controllare_la_connessione);
    }

    private void showErrorDialog(int title, int content) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(content)
                .positiveText(R.string.ok)
                .show();
    }

    private void showErrorDialogWithPossibilityToChooseNewDatesRange(int title, int content) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(content)
                .positiveText(R.string.cartellino_download_fallito_reimposta_range)
                .negativeText(R.string.annulla)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        showStartDatePicker();
                    }
                })
                .show();
    }

    private void showErrorDialogWithRetry(final Date d1, final Date d2) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.cartellino_download_fallito)
                .content(R.string.cartellino_download_fallito_controllare_la_connessione)
                .positiveText(R.string.riprova)
                .negativeText(R.string.chiudi)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        downloadPdf(d1, d2);
                    }
                })
                .show();
    }


    private ArrayList<CartellinoOrologio> getFakeCartOrol() {

        ArrayList<CartellinoOrologio> c = new ArrayList<>();

        CartellinoOrologio o1 = new CartellinoOrologio();
        CartellinoOrologio o2 = new CartellinoOrologio();
        CartellinoOrologio o3 = new CartellinoOrologio();
        CartellinoOrologio o4 = new CartellinoOrologio();
        o1.setBegda(new GregorianCalendar(2017,8,1).getTime());
        o1.setEndda(new GregorianCalendar(2017,8,30).getTime());
        o1.setAmount1(0.0);
        o1.setAmount2(8.32);

        o2.setBegda(new GregorianCalendar(2017,7,1).getTime());
        o2.setEndda(new GregorianCalendar(2017,7,31).getTime());
        o2.setAmount1(0.0);
        o2.setAmount2(0.0);

        o3.setBegda(new GregorianCalendar(2017,6,1).getTime());
        o3.setEndda(new GregorianCalendar(2017,6,31).getTime());
        o3.setAmount1(0.0);
        o3.setAmount2(0.0);

        o4.setBegda(new GregorianCalendar(2017,5,1).getTime());
        o4.setEndda(new GregorianCalendar(2017,5,30).getTime());
        o4.setAmount1(0.0);
        o4.setAmount2(6.15);

        c.add(o1);
        c.add(o1);
        c.add(o2);
        c.add(o3);
        c.add(o4);

        return c;

    }

    private void fetchDataList(String selectedKey) {
        cleanCompounds();
        adapter.ordinamentoCorrente=-1;
        showProgressDialog();
        EssClient.fetchCartellinoSelectionSetWithInterval(getContext(), selectedKey,
                new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        List<CartellinoOrologio> lista = null;
                        try {
                            lista = getFakeCartOrol(); //ResponseManager.getListaCartellinoOrologio(e, result);
                            handleList(lista);

                        } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                            dataErrorExpection.printStackTrace();
                        } */catch (Exception e1) {
                            e1.printStackTrace();
                            showError();
                        } finally {
                            setRecyclerViewAdapter(lista);
                            dismissDialog();
                        }
                    }
                });
    }

    private void setRecyclerViewAdapter(@Nullable List<CartellinoOrologio> list) {
        List<CartellinoOrologio> l = (list == null) ? new ArrayList<CartellinoOrologio>() : list;
        adapter = new CartellinoOrologioAdapter(getContext(), l);
        onAdapterListFilled();
        recyclerView.setAdapter(adapter);
        // Se ho i dati mostro intro
        if(!Utils.isEmptyList(list)){
            showIntro();
       }

    }

    void onAdapterListFilled() {
        adapter.setDelegate(this);
    }

    private String[] getSpinnerKeys() {
        return getContext().getResources().getStringArray(R.array.cartellino_orologio_spinner_keys);
    }

    private String[] getSpinnerValues() {
        return getContext().getResources().getStringArray(R.array.cartellino_orologio_spinner_values);
    }

    private String getSpinnerSelectedKey() {
        int position = spinner.getSelectedItemPosition();
        String spinnerSelectedKey = getSpinnerKeys()[position];
        return spinnerSelectedKey;
    }

    private void showSortIcon(int mode){
        switch (mode) {
            case CartellinoOrologioAdapter.ORDINAMENTO_DATA_INIZIO_PIU_RECENTE: {
                cleanCompounds();
                tvDataInizio.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
                break;
            }
            case CartellinoOrologioAdapter.ORDINAMENTO_DATA_INIZIO_MENO_RECENTE: {
                cleanCompounds();
                tvDataInizio.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
                break;
            }
            case CartellinoOrologioAdapter.ORDINAMENTO_DATA_FINE_PIU_RECENTE: {
                cleanCompounds();
                tvDataFine.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
                break;
            }
            case CartellinoOrologioAdapter.ORDINAMENTO_DATA_FINE_MENO_RECENTE: {
                cleanCompounds();
                tvDataFine.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
                break;
            }
        }
    }

    private void cleanCompounds(){
        tvDataInizio.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvDataFine.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void onItemSelected(Object obj) {
        CartellinoOrologio cart = (CartellinoOrologio) obj;
        downloadPdf(cart.getBegda(), cart.getEndda());
    }

    @Override
    public void onDatesChosen(Date startDate, Date endDate) {
        downloadPdf(startDate, endDate);
    }


    @OnClick(R.id.cartellino_orologio_date_range_button)
    public void showStartDatePicker() {
        // Seleziona range di date
        new SelectDataInizioFineDialog(this.getContext(), this.getFragmentManager(),
                this.startDate, this.endDate,
                this);
    }

    public void showSelectFilterDialog() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.cartellino_filtra_data_titolo)
                .items(R.array.cartellino_orologio_filtra_data_items)
                .itemsCallbackSingleChoice(this.adapter.getOrdinamentoCorrente(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        adapter.sortList(which);
                        adapter.reloadList();
                        showSortIcon(which);
                        return true;
                    }
                })
                .show();
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(false);
        this.fetchDataList(this.getSpinnerSelectedKey());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.menu_cartellino_orologio);
        item.setIcon(R.drawable.ic_cartellino_on);
        menu.findItem(R.id.menu_ordina).setVisible(true);
    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.cartellino_orologio_subtitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String tag = Navigator.with(getActivity()).utils().getActualTag();
        if (!tag.endsWith(CartellinoOrologioFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        }
        else{
            switch (item.getItemId()) {
                case R.id.menu_aggiorna:
                    fetchDataList(getSpinnerSelectedKey());
                    return true;
                case R.id.menu_ordina:
                    showSelectFilterDialog();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void showIntro() {
        super.showIntro();
        View v = getActivity().findViewById(R.id.menu_overflow);
        new IntroCartellinoOrologio(getActivity(), spinner, dateRangeView, v).start();
    }
}
