package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMotivo;
import it.acea.android.socialwfm.app.model.ess.TimbraturaTipo;
import it.acea.android.socialwfm.app.ui.fragment.TimbratureFragment;

/**
 * Created by n.fiorillo on 20/04/2016.
 */
public class TimbraturaAddEditDialog implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener, MaterialDialog.SingleButtonCallback, DialogInterface.OnDismissListener {
    private static final String TAG = TimbraturaAddEditDialog.class.getSimpleName();
    private MaterialDialog dialog;
    private Context context;
    private List<TimbraturaTipo> tipiTimbratura;
    private ArrayAdapter<TimbraturaTipo> tipiAdapter;
    private List<TimbraturaMotivo> motiviTimbratura;
    private ArrayAdapter<TimbraturaMotivo> motiviAdapter;
    private Timbratura.TimbraturaDettaglio timbraturaDettaglio = null;
    private FragmentManager fragmentManager;
    private View rootView;
    private TextView dataTv;
    private TextView oraTv;
    private TextView dataLabel;
    private TextView oraLabel;
    private EditText societaEt;
    private Spinner motivoSp;
    private TextView approvatoreTv;
    private EditText centroDiCostoEt;
    private Spinner tipiSp;
    private EditText noteEt;
    private View maskEdit;

    private Calendar dataRequest;
    private Calendar oraRequest;
    private DialogMode mode;

    public enum DialogMode {
        CREA, MODIFICA, CANCELLA;
    }

    public TimbraturaAddEditDialog(Context context, FragmentManager fragmentManager,
                                   List<TimbraturaTipo> tipiTimbratura, List<TimbraturaMotivo> motiviTimbratura) {
        this.timbraturaDettaglio = new Timbratura.TimbraturaDettaglio();
        init(context, fragmentManager, tipiTimbratura, motiviTimbratura, DialogMode.CREA);
    }

    public TimbraturaAddEditDialog(Context context, FragmentManager fragmentManager, List<TimbraturaTipo> tipiTimbratura,
                                    List<TimbraturaMotivo> motiviTimbratura,
                                   Timbratura.TimbraturaDettaglio timbraturaDettaglio, DialogMode mode) {
        this.timbraturaDettaglio = new Timbratura.TimbraturaDettaglio(timbraturaDettaglio);
        init(context, fragmentManager, tipiTimbratura, motiviTimbratura, mode);
    }

    private void init(Context context, FragmentManager fragmentManager, List<TimbraturaTipo> tipiTimbratura,
                      List<TimbraturaMotivo> motiviTimbratura, DialogMode mode) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.tipiTimbratura = tipiTimbratura;
        this.motiviTimbratura = motiviTimbratura;
        this.mode = mode;
        EventBus.getDefault().register(this);
        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_timbrature_add_edit, false)
                .positiveText(R.string.invia)
                .negativeText(R.string.annulla)
                .onNegative(this)
                .autoDismiss(false)
                .build();
        dialog.setOnDismissListener(this);
        bindUi();
        setTitle();
        setCloseButton();
        setTipoSpinnerAdapter(tipiTimbratura);
        setMotiviSpinnerAdapter(motiviTimbratura);
        setDateAndTimePicker();
        initDate();
        setFormDataToTimbratura();
        bindData();
        buildInvia();
    }

    private void initDate() {
        TimeZone zone = TimeZone.getTimeZone("UTC");
        GregorianCalendar timeLocale = new GregorianCalendar();
        if (timbraturaDettaglio.getGiorno() == null) {
            Calendar c = new GregorianCalendar(zone);
            c.clear();
            c.set(timeLocale.get(Calendar.YEAR), timeLocale.get(Calendar.MONTH), timeLocale.get(Calendar.DAY_OF_MONTH));
            timbraturaDettaglio.setGiorno(c.getTime());
        }
        if (timbraturaDettaglio.getOra() == null) {
            Calendar c = new GregorianCalendar();
            c.clear();
            c.set(Calendar.HOUR_OF_DAY, timeLocale.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, timeLocale.get(Calendar.MINUTE));
            timbraturaDettaglio.setOra(c.getTime());
        }
        dataRequest = new GregorianCalendar(zone);
        dataRequest.clear();
        dataRequest.setTime(timbraturaDettaglio.getGiorno());
        oraRequest = new GregorianCalendar();
        oraRequest.clear();
        oraRequest.setTime(timbraturaDettaglio.getOra());
    }

    private void setTitle() {
        int titleRes = 0;
        switch (mode) {
            case CREA:
                titleRes = R.string.nuova_timbratura;
                break;
            case MODIFICA:
                titleRes = R.string.modifica_timbratura;
                break;
            case CANCELLA:
                titleRes = R.string.cancella_timbratura;
                break;
        }
        ((TextView) rootView.findViewById(R.id.timbrature_dialog_title)).setText(titleRes);
    }

    private void setCloseButton() {
        rootView.findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // Setta i valori dei campi della form a partire dall'oggetto timbratura
    private void bindData() {
        TimbraturaTipo t = TimbraturaTipo.findByTipo(timbraturaDettaglio.getTipo(), tipiTimbratura);
        if (t != null) {
            int i = tipiAdapter.getPosition(t);
            tipiSp.setSelection(i);
        }
        dataRequest.setTime(timbraturaDettaglio.getGiorno());
        oraRequest.setTime(timbraturaDettaglio.getOra());
        dataTv.setText(Utils.dateFormatUTC(timbraturaDettaglio.getGiorno()));
        oraTv.setText(Utils.formatTimeToHHmm(timbraturaDettaglio.getOra()));
        approvatoreTv.setText(timbraturaDettaglio.getApprovatore());
        motivoSp.setEnabled(!timbraturaDettaglio.isMotivoReadOnly());
        TimbraturaMotivo m = TimbraturaMotivo.findById(timbraturaDettaglio.getMotivo(), motiviTimbratura);
        if (m != null) {
            int p = motiviAdapter.getPosition(m);
            motivoSp.setSelection(p);
        }
        tipiSp.setEnabled(!timbraturaDettaglio.isTipoReadOnly());
        dataTv.setEnabled(!timbraturaDettaglio.isGiornoReadOnly());
        oraTv.setEnabled(!timbraturaDettaglio.isOraReadOnly());
        dataLabel.setEnabled(!timbraturaDettaglio.isGiornoReadOnly());
        oraLabel.setEnabled(!timbraturaDettaglio.isOraReadOnly());
        societaEt.setEnabled(!timbraturaDettaglio.isSocietaReadOnly());
        societaEt.setText(timbraturaDettaglio.getIdSocieta());
        centroDiCostoEt.setEnabled(!timbraturaDettaglio.isCentroDiCostoReadOnly());
        centroDiCostoEt.setText(timbraturaDettaglio.getIdCentroDiCosto());
        noteEt.setEnabled(!timbraturaDettaglio.isNotaReadOnly());
        noteEt.setText(timbraturaDettaglio.getNota());
    }

    private void bindUi() {
        rootView = dialog.getCustomView();
        dataTv = (TextView) rootView.findViewById(R.id.timbrature_detail_data_tv);
        oraTv = (TextView) rootView.findViewById(R.id.timbrature_detail_ora_tv);
        dataLabel = (TextView) rootView.findViewById(R.id.timbrature_detail_data_label);
        oraLabel = (TextView) rootView.findViewById(R.id.timbrature_detail_ora_label);
        societaEt = (EditText) rootView.findViewById(R.id.timbrature_detail_societa_et);
        approvatoreTv = (TextView) rootView.findViewById(R.id.timbrature_detail_approvatore_tv);
        centroDiCostoEt = (EditText) rootView.findViewById(R.id.timbrature_detail_cdc_tv);
        motivoSp = (Spinner) rootView.findViewById(R.id.timbrature_detail_motivo_sp);
        tipiSp = (Spinner) rootView.findViewById(R.id.timbratura_tipi_spinner);
        noteEt = (EditText) rootView.findViewById(R.id.timbrature_detail_appunto_tv);
        maskEdit = rootView.findViewById(R.id.mask_edit);
    }

    private void setDateAndTimePicker() {
        dataLabel.setOnClickListener(this);
        oraLabel.setOnClickListener(this);
        dataTv.setOnClickListener(this);
        oraTv.setOnClickListener(this);
    }

    private void setTipoSpinnerAdapter(List<TimbraturaTipo> listaTimbraturaTipo) {
        tipiAdapter = new ArrayAdapter<>(context,
                R.layout.spinner_timbrature_tipo, listaTimbraturaTipo);
        tipiAdapter.setDropDownViewResource(R.layout.spinner_dropdown_timbrature_tipo);
        tipiSp.setAdapter(tipiAdapter);
        //tipiSp.setSelection(Adapter.NO_SELECTION, false);
        tipiSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prepareTimbratura(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setMotiviSpinnerAdapter(List<TimbraturaMotivo> listaTimbraturaMotivi) {
        motiviAdapter = new ArrayAdapter<>(context,
                R.layout.spinner_timbrature_tipo, listaTimbraturaMotivi);
        motiviAdapter.setDropDownViewResource(R.layout.spinner_dropdown_timbrature_tipo);
        motivoSp.setAdapter(motiviAdapter);
        motivoSp.setSelection(Adapter.NO_SELECTION, false);
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timbrature_detail_data_label:
                showDatePicker();
                break;
            case R.id.timbrature_detail_data_tv:
                showDatePicker();
                break;
            case R.id.timbrature_detail_ora_label:
                showTimePicker();
                break;
            case R.id.timbrature_detail_ora_tv:
                showTimePicker();
                break;
        }
    }

    private void showDatePicker() {
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setOnDateSetListener(this);
        cdp.setPreselectedDate(dataRequest.get(Calendar.YEAR), dataRequest.get(Calendar.MONTH), dataRequest.get(Calendar.DAY_OF_MONTH));
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(fragmentManager, "");
    }

    private void showTimePicker() {
        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment();
        rtpd.setOnTimeSetListener(this);
        rtpd.setStartTime(oraRequest.get(Calendar.HOUR_OF_DAY), oraRequest.get(Calendar.MINUTE));
        rtpd.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        rtpd.show(fragmentManager, "");
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        dataRequest.clear();
        dataRequest.set(year, monthOfYear, dayOfMonth);
        dataTv.setText(Utils.dateFormatUTC(dataRequest.getTime()));
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        oraRequest.clear();
        oraRequest.set(Calendar.HOUR_OF_DAY, hourOfDay);
        oraRequest.set(Calendar.MINUTE, minute);
        oraTv.setText(Utils.formatTimeToHHmm(oraRequest.getTime()));
    }

    private void setFormDataToTimbratura() {
        TimbraturaTipo t = (TimbraturaTipo) tipiSp.getSelectedItem();
        timbraturaDettaglio.setTipo(t.getTipo());
        timbraturaDettaglio.setGiorno(dataRequest.getTime());
        timbraturaDettaglio.setOra(oraRequest.getTime());
        if (!timbraturaDettaglio.isMotivoReadOnly()) {
            TimbraturaMotivo m = (TimbraturaMotivo) motivoSp.getSelectedItem();
            timbraturaDettaglio.setMotivo(m.getId());
        }
        if (!timbraturaDettaglio.isSocietaReadOnly()) {
            timbraturaDettaglio.setIdSocieta(societaEt.getText().toString());
        }
        if (!timbraturaDettaglio.isCentroDiCostoReadOnly()) {
            timbraturaDettaglio.setIdCentroDiCosto(centroDiCostoEt.getText().toString());
        }
        if (!timbraturaDettaglio.isNotaReadOnly()) {
            timbraturaDettaglio.setNota(noteEt.getText().toString());
        }
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        switch (which) {
            case NEGATIVE:
                dialog.dismiss();
                break;
        }
    }

    private void prepareTimbratura(boolean modificheCompletate) {
        setFormDataToTimbratura();
        switch (mode) {
            case CREA:
                timbraturaDettaglio.setPrepare(Timbratura.TimbraturaDettaglio.PREPARE_CREATE);
                EventBus.getDefault().post(new TimbratureFragment.MessageCreaTimbratura(timbraturaDettaglio, modificheCompletate));
                break;
            case MODIFICA:
                timbraturaDettaglio.setPrepare(Timbratura.TimbraturaDettaglio.PREPARE_EDIT);
                EventBus.getDefault().post(new TimbratureFragment.MessageRequestModificaTimbratura(timbraturaDettaglio, modificheCompletate));
                break;
            case CANCELLA:
                timbraturaDettaglio.setPrepare(Timbratura.TimbraturaDettaglio.PREPARE_DELETE);
                EventBus.getDefault().post(new TimbratureFragment.MessageRequestModificaTimbratura(timbraturaDettaglio, modificheCompletate));
                break;
        }
    }


    private void salvaTimbratura() {
        setFormDataToTimbratura();
        switch (mode) {
            case CREA:
                timbraturaDettaglio.setPrepare("");
                EventBus.getDefault().post(new TimbratureFragment.MessageCreaTimbratura(timbraturaDettaglio, true));
                break;
            case MODIFICA:
                timbraturaDettaglio.setPrepare(Timbratura.TimbraturaDettaglio.PREPARE_SAVE_UPDATE);
                EventBus.getDefault().post(new TimbratureFragment.MessageRequestModificaTimbratura(timbraturaDettaglio, true));
                break;
            case CANCELLA:
                timbraturaDettaglio.setPrepare(Timbratura.TimbraturaDettaglio.PREPARE_SAVE_DELETE);
                EventBus.getDefault().post(new TimbratureFragment.MessageRequestModificaTimbratura(timbraturaDettaglio, true));
                break;
        }
    }


    @Subscribe
    public void onResponseCreaTimbratura(TimbratureFragment.MessageResponseCreaTimbratura event) {
        responseTimbratura(event.timbraturaDettaglio, event.modificheCompletate);
    }

    @Subscribe
    public void onResponseModificaTimbratura(TimbratureFragment.MessageResponseModificaTimbratura event) {
        responseTimbratura(event.timbraturaDettaglio, event.modificheCompletate);
    }

    private void responseTimbratura(Timbratura.TimbraturaDettaglio timbraturaDettaglio, boolean modificheCompletate) {
        this.timbraturaDettaglio = timbraturaDettaglio;
        bindData();
        if (modificheCompletate) buildSalva();
    }

    @Subscribe
    public void onModificaTimbraturaCompletato(TimbratureFragment.MessageResponseModificaTimbraturaCompletata event) {
        dialog.dismiss();
    }

    @Subscribe
    public void onCreateTimbraturaCompletato(TimbratureFragment.MessageResponseCreateTimbraturaCompletato event) {
        dialog.dismiss();
    }

    @Subscribe
    public void onErroreCreaTimbratura(TimbratureFragment.MessageErroreTimbratura event) {
        buildInvia();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        EventBus.getDefault().unregister(this);
    }

    private void buildInvia() {
        maskEdit.setVisibility(View.GONE);
        int title = 0;
        switch (mode) {
            case CREA:
                title = R.string.crea;
                break;
            case MODIFICA:
                title = R.string.modifica;
                break;
            case CANCELLA:
                title = R.string.cancella;
                break;
        }
        dialog.setActionButton(DialogAction.POSITIVE, title);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareTimbratura(true);
            }
        });
    }

    private void buildSalva() {
        maskEdit.setVisibility(View.VISIBLE);
        maskEdit.requestFocus();
        dialog.setActionButton(DialogAction.POSITIVE, R.string.salva);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvaTimbratura();
            }
        });
    }
}
