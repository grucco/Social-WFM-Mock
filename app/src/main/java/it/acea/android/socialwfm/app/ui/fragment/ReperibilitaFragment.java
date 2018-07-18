package it.acea.android.socialwfm.app.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.widget.calendar.CalendarView;
import org.fingerlinks.mobile.android.utils.widget.calendar.SupportCalendarItem;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.Reperibilita;
import it.acea.android.socialwfm.app.ui.MainActivity;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.dialog.ReperibilitaDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroReperibilita;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by n.fiorillo on 13/04/2016.
 */
public class ReperibilitaFragment extends EssFragment implements CalendarDatePickerDialogFragment.OnDateSetListener, CalendarView.CalendarListener {

    private static final String TAG = ReperibilitaFragment.class.getSimpleName();
    @Bind(R.id.error_view)
    ErrorView errorView;
    @Bind(R.id.reperibilita_calendario_date_select)
    TextView reperibilitaCalendarioDateSelect;

    Calendar dateRequest = Calendar.getInstance();
    @Bind(R.id.calendar_1)
    CalendarView calendar1;
    @Bind(R.id.calendar_2)
    CalendarView calendar2;
    @Bind(R.id.calendar_3)
    CalendarView calendar3;
    @Bind(R.id.arrow_left)
    ImageView arrowLeft;
    @Bind(R.id.arrow_right)
    ImageView arrowRight;

    List<SupportCalendarItem> calendarItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reperibilita, container, false);
        ButterKnife.bind(this, view);
        Calendar c = Calendar.getInstance();
        setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        initErrorView();
        initCalendars();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
    }

    @Override
    public ErrorView getErrorView() {
        return errorView;
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_reperibilita).setIcon(R.drawable.ic_reperibilita_on);
        menu.findItem(R.id.menu_pdf).setVisible(true);
        menu.findItem(R.id.menu_excel).setVisible(true);
    }

    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.reperibilita);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String tag = Navigator.with(getActivity()).utils().getActualTag();
        Log.d(TAG, tag);
        if (!tag.endsWith(ReperibilitaFragment.class.getName())) {
            return super.onOptionsItemSelected(item);
        }
        else{
            switch (item.getItemId()) {
                case R.id.menu_aggiorna:
                    fetchData();
                    return true;
                case R.id.menu_pdf:
                    exportFile(EssClient.TipoFileMimeType.PDF);
                    return true;
                case R.id.menu_excel:
                    exportFile(EssClient.TipoFileMimeType.EXCEL);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }


    private ArrayList<Reperibilita> getFakeRep() {

        Reperibilita r1 = new Reperibilita();
        Reperibilita r2 = new Reperibilita();

        r1.setDataInizio(new GregorianCalendar(2018,5,22).getTime());
        r1.setOraInizio(new GregorianCalendar(2018,5,22,19,0).getTime());
        r1.setOraFine(new GregorianCalendar(2018,5,22,21,0).getTime());
        r1.setTipo("Extra");

        r2.setDataInizio(new GregorianCalendar(2018,5,18).getTime());
        r2.setOraInizio(new GregorianCalendar(2018,5,18,20,0).getTime());
        r2.setOraFine(new GregorianCalendar(2018,5,18,22,0).getTime());
        r2.setTipo("Extra");

        ArrayList<Reperibilita> rr = new ArrayList<>();
        rr.add(r1);
        rr.add(r2);

        return rr;

    }


    public void fetchData() {
        showProgressDialog();
        EssClient.fetchReperibilita(getContext(), dateRequest.getTime(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                List<Reperibilita> listaReperibilita = null;
                try {
                    listaReperibilita = getFakeRep(); //ResponseManager.getListaReperibilita(e, result);

                } /*catch (ResponseManager.DataErrorExpection e2) {
                    new ErrorDialog(getContext(), e2.getErrorDetails()).show();
                }*/ catch (Exception e1) {
                    showError();
                    e1.printStackTrace();
                } finally {
                    if (listaReperibilita == null) listaReperibilita = new ArrayList<>();
                    buildCalendarItems(listaReperibilita);
                    dismissDialog();
                    showIntro();
                }
            }
        });
    }


    private void initErrorView() {
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                fetchData();
                dismissError();
            }
        });
    }

    private void initCalendars() {
        calendar1.setCalendarListener(this);
        calendar2.setCalendarListener(this);
        calendar3.setCalendarListener(this);
    }

    @OnClick(R.id.reperibilita_calendario_date_select)
    public void showDatePicker() {
        int y = dateRequest.get(Calendar.YEAR);
        int m = dateRequest.get(Calendar.MONTH);
        int d = dateRequest.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setPreselectedDate(y, m, d);
        cdp.setOnDateSetListener(this);
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getFragmentManager(), "");
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
        fetchData();
    }


    @Override
    public void showIntro() {
        View actionView = getActivity().findViewById(R.id.menu_overflow);
        new IntroReperibilita(getActivity(),reperibilitaCalendarioDateSelect,arrowRight,actionView).start();
    }

    private void setDate(int year, int monthOfYear, int dayOfMonth) {
        dateRequest.clear();
        dateRequest.set(year, monthOfYear, dayOfMonth);
        String d = DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY).format(dateRequest.getTime());
        reperibilitaCalendarioDateSelect.setText(d);
        setCalendarsDate();
    }

    private void setCalendarsDate() {
        Calendar c1 = (Calendar) dateRequest.clone();
        Calendar c2 = (Calendar) dateRequest.clone();
        Calendar c3 = (Calendar) dateRequest.clone();
        c2.add(Calendar.MONTH, 1);
        c3.add(Calendar.MONTH, 2);
        calendar1.initializeCalendar(c1);
        calendar2.initializeCalendar(c2);
        calendar3.initializeCalendar(c3);
    }

    @OnClick({R.id.arrow_left, R.id.arrow_right})
    public void dateMove(View view) {
        Calendar c = (Calendar) dateRequest.clone();
        switch (view.getId()) {
            case R.id.arrow_left:
                c.add(Calendar.MONTH, -1);
                break;
            case R.id.arrow_right:
                c.add(Calendar.MONTH, 1);
                break;
        }
        setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        fetchData();
    }

    private void buildCalendarItems(List<Reperibilita> reperibilitaList) {
        calendarItems.clear();
        for (Reperibilita r : reperibilitaList) {
            SupportCalendarItem s = new SupportCalendarItem();
            s.setDate(r.getDataInizio());
            s.setId(String.valueOf(r.hashCode()));
            s.setData(r);
            s.setStyle(R.color.md_yellow_400);
            calendarItems.add(s);
        }
        calendar1.setEventsToCalendar(calendarItems);
        calendar2.setEventsToCalendar(calendarItems);
        calendar3.setEventsToCalendar(calendarItems);
    }

    @Override
    public void onDateSelected(Date date, SupportCalendarItem item) {
        if (item != null) {
            new ReperibilitaDialog(getContext(), (Reperibilita) item.getData()).show();
        }
    }

    private void exportFile(EssClient.TipoFileMimeType tipo) {
        showProgressDialog();
        EssClient.fetchReperibilitaFile(getContext(), dateRequest.getTime(), tipo, new FutureCallback<Response<File>>() {
            @Override
            public void onCompleted(Exception e, Response<File> result) {
                try {
                    File f = ResponseManager.getReperibilitaExportFile(e, result);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.fromFile(f));
                    startActivity(intent);
                }
                catch (ResponseManager.DataErrorExpection ex){
                    ErrorDialog eDialog = new ErrorDialog(getContext(),ex.getErrorDetails());
                    eDialog.show();
                }
                catch (Exception e1) {
                    showFileErrorMessage();
                    e1.printStackTrace();
                } finally {
                    dismissDialog();
                }
            }
        });
    }
}
