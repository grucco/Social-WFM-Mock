package it.acea.android.socialwfm.app.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.delegate.SWFMNetworkChangeEventDelegate;
import it.acea.android.socialwfm.app.delegate.SWFMRecyclerViewClickDelegate;
import it.acea.android.socialwfm.app.model.ess.CudCedolino;
import it.acea.android.socialwfm.app.ui.adapter.CudCedolinoAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroCudCedolino;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by a.simeoni on 16/05/2016.
 */
public class CudCedolinoFragment extends EssFragment implements SWFMRecyclerViewClickDelegate,
        SWFMNetworkChangeEventDelegate,
        SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    private enum BeanType {CUD, CEDOLINO}

    private int currentYear;


    @Bind(R.id.cud_cedolino_tipi_spinner)
    public Spinner spinner;
    @Bind(R.id.cud_cedolino_data_select)
    TextView tvAnno;
    @Bind(R.id.cud_cedolino_recycler_view)
    EmptyRecyclerView rvRecyclerView;
    @Bind(R.id.error_view)
    ErrorView errorView;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.empty_view)
    public ErrorView emptyView;

    private static final BigDecimal MIN_YEAR_ALLOWED = new BigDecimal(1960);

    CudCedolinoAdapter adapter;
    CudCedolino mSelectedItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cud_cedolino, container, false);
        ButterKnife.bind(this, view);

        currentYear = getYearFromDate(new Date());
        setYear(currentYear);

        setSpinnerAdapter();
        initRecyclerViewStyle();

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                dismissError();
                fetchDataList(getSpinnerSelectedType(), currentYear);
            }
        });

        // Pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchDataList(getSpinnerSelectedType(), currentYear);
    }

    private void setYear(int year) {
        tvAnno.setText(year + "");
    }

    private BeanType getSpinnerSelectedType() {
        int index = spinner.getSelectedItemPosition();
        switch (index) {
            case 0: {
                return BeanType.CEDOLINO;
            }
            case 1: {
                return BeanType.CUD;
            }
        }
        return null;
    }

    private int getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    @Override
    public void showIntro() {
        View actionView = getActivity().findViewById(R.id.menu_overflow);
        new IntroCudCedolino(getActivity(), this.spinner, this.tvAnno, actionView).start();
    }


    private ArrayList<CudCedolino> getFakeCud() {

        CudCedolino c1 = new CudCedolino();
        CudCedolino c2 = new CudCedolino();
        CudCedolino c3 = new CudCedolino();
        CudCedolino c4 = new CudCedolino();
        CudCedolino c5 = new CudCedolino();

        c1.setAnno("2018");
        c1.setMonthText("Gennaio");
        c1.setFilename("gennaio.pdf");
        c1.setSocieta("Reply");
        c1.setMatPayroll("abc");

        c2.setAnno("2018");
        c2.setMonthText("Febbraio");
        c2.setFilename("febbraio.pdf");
        c2.setSocieta("Reply");
        c2.setMatPayroll("abc");

        c3.setAnno("2018");
        c3.setMonthText("Marzo");
        c3.setFilename("marzo.pdf");
        c3.setSocieta("Reply");
        c3.setMatPayroll("abc");

        c4.setAnno("2018");
        c4.setMonthText("Aprile");
        c4.setFilename("aprile.pdf");
        c4.setSocieta("Reply");
        c4.setMatPayroll("abc");

        c5.setAnno("2018");
        c5.setMonthText("Maggio");
        c5.setFilename("maggio.pdf");
        c5.setSocieta("Reply");
        c5.setMatPayroll("abc");

        ArrayList<CudCedolino> cc = new ArrayList<>();
        cc.add(c1);
        cc.add(c2);
        cc.add(c3);
        cc.add(c4);
        cc.add(c5);

        return cc;

    }

    private void fetchDataList(BeanType type, int year) {
        showProgressDialog();
        //CALLBACK
        FutureCallback<Response<JsonObject>> callback = new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                List<CudCedolino> lista = new ArrayList<CudCedolino>();
                try {
                    lista = getFakeCud();
                    //ResponseManager.getListaCudCedolino(e, result);
                } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                    dataErrorExpection.printStackTrace();
                    new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                }*/ catch (Exception e1) {
                    e1.printStackTrace();
                    showError();
                } finally {
                    setRecyclerViewAdapter(lista);
                    dismissDialog();
                    if (!Utils.isEmptyList(lista)) {
                        showIntro();
                    }
                }
            }
        };

        switch (type) {
            case CEDOLINO: {
                EssClient.fetchCedoliniSet(getContext(), year, callback);
                break;
            }
            case CUD: {
                EssClient.fetchCudSet(getContext(), year, callback);
                break;
            }
        }
    }

    private void initRecyclerViewStyle() {
        // Inizializzo
        setRecyclerViewAdapter(new ArrayList<CudCedolino>());
        this.rvRecyclerView.setEmptyView(emptyView);
        this.rvRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.rvRecyclerView.setHasFixedSize(true);
        this.rvRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(ContextCompat.getColor(getContext(), R.color.ess_recycler_vier_divider))
                        .sizeResId(R.dimen.divider)
                        .build());
    }


    private void setSpinnerAdapter() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_monte_ore_tipo,
                getContext().getResources().getStringArray(R.array.cud_cedolino_spinner_values));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_monte_ore_tipo);
        spinner.setAdapter(adapter);
        spinner.setSelection(Adapter.NO_SELECTION, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchDataList(getSpinnerSelectedType(), currentYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setRecyclerViewAdapter(@Nullable List<CudCedolino> list) {
        adapter = new CudCedolinoAdapter(list);
        onAdapterListFilled();
        this.rvRecyclerView.setAdapter(adapter);
    }

    void onAdapterListFilled() {
        adapter.setDelegate(this);
    }

    private void downloadPdf(BeanType type, String filename) {
        if (this.isStoragePermissionGranted()) {
            showProgressDialog();
            FutureCallback<Response<File>> callback = new FutureCallback<Response<File>>() {
                @Override
                public void onCompleted(Exception e, Response<File> result) {
                    try {
                        ResponseManager.getPDFCudCedolino(e, result);
                        Utils.launchOpenPdfIntent(getContext(), result.getResult());
                    } catch (Exception e1) {
                        showFileErrorMessage();
                    } finally {
                        dismissDialog();
                    }
                }
            };

            switch (type) {
                case CEDOLINO: {
                    EssClient.fetchCedoliniFileSetValue(getContext(), filename, callback);
                    break;
                }
                case CUD: {
                    EssClient.fetchCudFileSetValue(getContext(), filename, callback);
                    break;
                }
            }
        }
    }

    @OnClick(R.id.cud_cedolino_data_select)
    public void onDatePickerClick() {

        Calendar cToday = Calendar.getInstance();
        cToday.setTime(new Date());

        NumberPickerBuilder npb = new NumberPickerBuilder()
                .setLabelText(getContext().getResources().getString(R.string.seleziona_anno))
                .setFragmentManager(getChildFragmentManager())
                .setStyleResId(R.style.MyCustomNumberPickerTheme)
                .setMinNumber(MIN_YEAR_ALLOWED)
                .setDecimalVisibility(View.INVISIBLE)
                .setPlusMinusVisibility(View.INVISIBLE)
                .setMaxNumber(new BigDecimal(cToday.get(Calendar.YEAR)));
        npb.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandlerV2() {
            @Override
            public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {
                currentYear = number.intValue();
                setYear(currentYear);
                fetchDataList(getSpinnerSelectedType(), currentYear);
            }
        });
        npb.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.menu_cedolino);
        item.setIcon(R.drawable.ic_cud_cedolino_on);
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
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(false);
        this.fetchDataList(getSpinnerSelectedType(), currentYear);
    }

    @Override
    public void onNetworkDisconnected() {

    }

    @Override
    public void onNetworkAvailable() {

    }

    @Override
    public void onItemSelected(Object obj) {
        mSelectedItem = (CudCedolino) obj;
        downloadPdf(getSpinnerSelectedType(), mSelectedItem.getFilename());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String tag = Navigator.with(getActivity()).utils().getActualTag();
        Log.d(TAG, tag);
        if (!tag.endsWith(CudCedolinoFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_aggiorna:
                    fetchDataList(getSpinnerSelectedType(), currentYear);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            if(mSelectedItem != null){
                downloadPdf(getSpinnerSelectedType(), mSelectedItem.getFilename());
            }
        }
    }
}
