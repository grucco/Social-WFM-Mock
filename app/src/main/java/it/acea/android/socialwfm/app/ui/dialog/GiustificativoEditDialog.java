package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.GiustificativiType;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;

import static com.afollestad.materialdialogs.DialogAction.NEUTRAL;

/**
 * Created by n.fiorillo on 09/03/2016.
 */

public class GiustificativoEditDialog implements MaterialDialog.SingleButtonCallback {

    private static final String TAG = GiustificativoEditDialog.class.getName();

    private MaterialDialog dialog;
    private MaterialDialog progress;
    private Context context;
    private Giustificativi giustificativo;
    private FragmentManager fragmentManager;
    private MonthAdapter.CalendarDay startDay = null;
    private MonthAdapter.CalendarDay endDay = null;
    private String BeginTime = "PT00H00M00S";
    private String EndTime = "PT00H00M00S";
    private String subType = null;
    private ImageView close_dialog;
    private TextView inizio_date_picker;
    private TextView fine_date_picker;
    private Spinner type;
    private TextView inizio;
    private TextView inizio_date;
    private TextView fine;
    private TextView fine_date;
    private TextView inizioOrario;
    private TextView fineOrario;
    private EditText comment;
    private TextView title;
    private TextView unexpectedType;
    private TextView oreAssenzaPresenza;
    private boolean onlySingleDay = false;
    private List<String> type_list;
    private Giustificativi giustificativiDaConfermare;
    private View formUnClickable;
    private View oreAssenzaPresenzaContainer;

    private MaterialDialog.OnDismissListener onDismissListener;
    private Calendar fineDataRequest;
    private Calendar inizioDataRequest;

    private SharedPreferences settings;
    private String subTypeText = null;
    private static int ordering_var = 1;



    public void setOnDismissListener(MaterialDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public GiustificativoEditDialog(Context context, FragmentManager fragmentManager) {
        this(context, fragmentManager, null);
    }

    public GiustificativoEditDialog(Context context, FragmentManager fragmentManager, Giustificativi giustificativo) {
        this.context = context;
        this.giustificativo = giustificativo;
        this.fragmentManager = fragmentManager;
        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_giustificativo_edit, false)
                .positiveText(R.string.send)
                .autoDismiss(false)
                .onAny(this)
                .build();
        if (onDismissListener != null) dialog.setOnDismissListener(onDismissListener);
        initProgressDialog();
        buildView();
    }

    private void initProgressDialog() {
        progress = new MaterialDialog.Builder(context)
                .content(R.string.loading)
                .cancelable(false)
                .progress(true, 0)
                .build();
    }

    private ArrayList<GiustificativiType> getGiustifType() {

        GiustificativiType g1 = new GiustificativiType();
        g1.setNoPartialDay("true");
        g1.setOnlySingleDay("true");
        g1.setSubty("01");
        g1.setSubtytext("Funzione Religiosa");

        GiustificativiType g2 = new GiustificativiType();
        g2.setNoPartialDay("true");
        g2.setOnlySingleDay("true");
        g2.setSubty("20");
        g2.setSubtytext("Ferie a Giorni");

        GiustificativiType g3 = new GiustificativiType();
        g3.setNoPartialDay("true");
        g3.setOnlySingleDay("true");
        g3.setSubty("02");
        g3.setSubtytext("SFS giornal. mezzi az. Or");

        GiustificativiType g4 = new GiustificativiType();
        g4.setNoPartialDay("true");
        g4.setOnlySingleDay("true");
        g4.setSubty("03");
        g4.setSubtytext("SFS giornal. mezzi az. Sp");

        GiustificativiType g5 = new GiustificativiType();
        g5.setNoPartialDay("true");
        g5.setOnlySingleDay("true");
        g5.setSubty("07");
        g5.setSubtytext("Servizio Presidente CRA");

        GiustificativiType g6 = new GiustificativiType();
        g6.setNoPartialDay("true");
        g6.setOnlySingleDay("true");
        g6.setSubty("13");
        g6.setSubtytext("Sciopero intera giornata");

        GiustificativiType g7 = new GiustificativiType();
        g7.setNoPartialDay("true");
        g7.setOnlySingleDay("true");
        g7.setSubty("19");
        g7.setSubtytext("Rip. fisiologico HH e GG");

        GiustificativiType g8 = new GiustificativiType();
        g8.setNoPartialDay("true");
        g8.setOnlySingleDay("true");
        g8.setSubty("23");
        g8.setSubtytext("Permesso Conto Ore");

        GiustificativiType g9 = new GiustificativiType();
        g9.setNoPartialDay("true");
        g9.setOnlySingleDay("true");
        g9.setSubty("25");
        g9.setSubtytext("HH Permesso da compensare");

        GiustificativiType g10 = new GiustificativiType();
        g10.setNoPartialDay("true");
        g10.setOnlySingleDay("true");
        g10.setSubty("34");
        g10.setSubtytext("Visita medica aziendale");


        ArrayList<GiustificativiType> g = new ArrayList<>();
        g.add(g1);
        g.add(g2);
        g.add(g3);
        g.add(g4);
        g.add(g5);
        g.add(g6);
        g.add(g7);
        g.add(g8);
        g.add(g9);
        g.add(g10);

        return g;

    }

    private void setAction() {
        type_list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        List<GiustificativiType> giustificativiTypeList = getGiustifType(); //realm.where(GiustificativiType.class).findAll();
        for (GiustificativiType giustificativiType : giustificativiTypeList) {
            type_list.add(giustificativiType.getSubtytext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, type_list);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Realm realm1 = Realm.getDefaultInstance();
                GiustificativiType type = realm1.where(GiustificativiType.class).equalTo("Subtytext", type_list.get(position)).findFirst();
                subType = type.getSubty();
                subTypeText = type.getSubtytext();
                setupUI(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        inizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inizioDataRequest == null) inizioDataRequest = Calendar.getInstance();
                int y = inizioDataRequest.get(Calendar.YEAR);
                int m = inizioDataRequest.get(Calendar.MONTH);
                int d = inizioDataRequest.get(Calendar.DAY_OF_MONTH);
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
                cdp.setPreselectedDate(y, m, d);
                cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        inizioDataRequest = calendar;
                        startDay = new MonthAdapter.CalendarDay(calendar);
                        int m = monthOfYear + 1;
                        inizio_date.setText(dayOfMonth + "/" + m + "/" + year);
                    }
                });
                cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
                cdp.show(fragmentManager, "");
            }
        });
        fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDay == null) {
                    return;
                }
                if (fineDataRequest == null) fineDataRequest = Calendar.getInstance();
                int y = fineDataRequest.get(Calendar.YEAR);
                int m = fineDataRequest.get(Calendar.MONTH);
                int d = fineDataRequest.get(Calendar.DAY_OF_MONTH);
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
                cdp.setPreselectedDate(y, m, d);
                cdp.setDateRange(startDay, null);
                cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        fineDataRequest = calendar;
                        endDay = new MonthAdapter.CalendarDay(calendar);
                        int m = monthOfYear + 1;
                        fine_date.setText(dayOfMonth + "/" + m + "/" + year);
                    }
                });
                cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
                cdp.show(fragmentManager, "");
            }
        });
        inizio_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setForced24hFormat()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                BeginTime = "PT" + hourOfDay + "H" + minute + "M00S";
                                inizioOrario.setText(Utils.formatTimePTHMS(BeginTime));
                            }
                        })
                        .setStartTime(10, 10)
                        .setThemeCustom(R.style.MyCustomBetterPickerTheme);
                if (BeginTime != null) {
                    Date date = Utils.getTimePTHMS(BeginTime);
                    rtpd.setStartTime(date.getHours(), date.getMinutes());
                }
                rtpd.show(fragmentManager, "");
            }
        });
        fine_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setForced24hFormat()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                EndTime = "PT" + hourOfDay + "H" + minute + "M00S";
                                fineOrario.setText(Utils.formatTimePTHMS(EndTime));
                            }
                        })
                        .setStartTime(10, 10)
                        .setThemeCustom(R.style.MyCustomBetterPickerTheme);
                if (EndTime != null) {
                    Date date = Utils.getTimePTHMS(EndTime);
                    rtpd.setStartTime(date.getHours(), date.getMinutes());
                }
                rtpd.show(fragmentManager, "");
            }
        });
        if (giustificativo != null) {
            title.setText(R.string.edit_giustificativo);
            bindData(giustificativo);
        } else {
            title.setText(R.string.nuovo_giustificativo);
        }

    }

    private void buildView() {
        View root = dialog.getCustomView();
        if (root != null) {
            close_dialog = (ImageView) root.findViewById(R.id.close_dialog);
            fine_date_picker = (TextView) root.findViewById(R.id.fine_date_picker);
            inizio_date_picker = (TextView) root.findViewById(R.id.inizio_date_picker);
            type = (Spinner) root.findViewById(R.id.type);
            inizio = (TextView) root.findViewById(R.id.inizio);
            inizio_date = (TextView) root.findViewById(R.id.inizio_date);
            fine = (TextView) root.findViewById(R.id.fine);
            fine_date = (TextView) root.findViewById(R.id.fine_date);
            comment = (EditText) root.findViewById(R.id.comment);
            inizioOrario = (TextView) root.findViewById(R.id.inizio_orario_date);
            fineOrario = (TextView) root.findViewById(R.id.fine_orario_date);
            title = (TextView) root.findViewById(R.id.textView2);
            unexpectedType = (TextView) root.findViewById(R.id.giustificativo_edit_unexpected_type);
            oreAssenzaPresenza = (TextView) root.findViewById(R.id.textview_dialog_giustificativo_edit_ore_assenza_presenza);
            formUnClickable = root.findViewById(R.id.view_dialog_giustificativo_edit_form_unclickable);
            oreAssenzaPresenzaContainer = root.findViewById(R.id.linearlayout_dialog_giustificativo_edit_oreassenza_container);
            initViewValues();
            setAction();
        }
    }

    private void initViewValues() {
        inizioOrario.setText(Utils.formatTimePTHMS(BeginTime));
        fineOrario.setText(Utils.formatTimePTHMS(EndTime));
    }

    private void bindData(Giustificativi giustificativo) {
            type.setEnabled(false);
            Realm realm1 = Realm.getDefaultInstance();
            GiustificativiType giustificativiType = realm1.where(GiustificativiType.class).equalTo("Subty", giustificativo.getSubty()).findFirst();
            if (giustificativiType == null) {
                unexpectedType.setVisibility(View.GONE);
                type.setVisibility(View.VISIBLE);
                type.setEnabled(true);
                setAllFieldVisible();
            } else {
                unexpectedType.setVisibility(View.GONE);
                type.setVisibility(View.VISIBLE);
                int pos = type_list.indexOf(giustificativiType.getSubtytext());
                setupUI(giustificativiType);
                type.setSelection(pos);
            }

            long parseBegin = Long.parseLong(giustificativo.getBegda().replaceAll("(/Date\\()|(\\)/)", ""));
            long parseEnd = Long.parseLong(giustificativo.getEndda().replaceAll("(/Date\\()|(\\)/)", ""));
            startDay = new MonthAdapter.CalendarDay(parseBegin);
            endDay = new MonthAdapter.CalendarDay(parseEnd);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(parseBegin);
            fineDataRequest = calendar;
            calendar.setTimeInMillis(parseEnd);
            inizioDataRequest = calendar;
            inizio_date.setText(Utils.dateFormat(new Date(parseBegin)));
            fine_date.setText(Utils.dateFormat(new Date(parseEnd)));

            fineOrario.setText(Utils.formatTimePTHMS(giustificativo.getEndTime()));
            inizioOrario.setText(Utils.formatTimePTHMS(giustificativo.getBeginTime()));
            BeginTime = giustificativo.getBeginTime();
            EndTime = giustificativo.getEndTime();
            oreAssenzaPresenza.setText(giustificativo.getAttabsHours());
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        switch (which) {
            case POSITIVE:
                if (giustificativiDaConfermare != null && giustificativiDaConfermare.getIsPrepare().isEmpty()) {
                    sendData(giustificativiDaConfermare);
                } else {
                    if (giustificativo == null || Giustificativi.isGiustificativoSpeciale(giustificativo)) {
                        addGiustificativo();
                    } else {
                        editGiustificativo();
                    }
                }

                break;
            case NEUTRAL:
                annulla();
                break;
        }
    }

    private void annulla() {
        formUnClickable.setVisibility(View.GONE);
        giustificativiDaConfermare = null;
        dialog.setActionButton(DialogAction.POSITIVE, R.string.giustificativi_dialog_edit_invia);
        dialog.getActionButton(NEUTRAL).setVisibility(View.INVISIBLE);
    }

    private void editGiustificativo() {
        if (onlySingleDay) {
            endDay = startDay;
            fineDataRequest = inizioDataRequest;
        }
        Giustificativi giustificativi = new Giustificativi();
        giustificativi.setRequestId(giustificativo.getRequestId());
        giustificativi.setBegda("/Date(" + inizioDataRequest.getTimeInMillis() + ")/");
        giustificativi.setEndda("/Date(" + fineDataRequest.getTimeInMillis() + ")/");
        giustificativi.setBeginTime(BeginTime);
        giustificativi.setEndTime(EndTime);
        giustificativi.setSubty(subType);
        giustificativi.setSubtypeDescription(subTypeText);
        giustificativi.setDateSearch(giustificativo.getDateSearch());
        giustificativi.setDocnr(giustificativo.getDocnr());
        giustificativi.setCurrNotice(comment.getText().toString());
        giustificativi.setIsPrepare(Giustificativi.IS_PREPARE_EDIT);
        sendData(giustificativi);
    }

    private boolean checkValue() {
        return !(inizioDataRequest == null || fineDataRequest == null || startDay == null || endDay == null || EndTime == null || BeginTime == null || subType == null);
    }

    private void addGiustificativo() {
        if (onlySingleDay) {
            endDay = startDay;
            fineDataRequest = inizioDataRequest;
        }
        if (!checkValue()) {
            Toast.makeText(context, R.string.giustificativi_dialog_campi_non_inseriti, Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }
        Giustificativi giustificativi = new Giustificativi();
        giustificativi.setBegda("/Date(" + inizioDataRequest.getTimeInMillis() + ")/");
        giustificativi.setEndda("/Date(" + fineDataRequest.getTimeInMillis() + ")/");
        giustificativi.setSubty(subType);
        giustificativi.setSubtypeDescription(subTypeText);
        giustificativi.setCurrNotice(comment.getText().toString());
        giustificativi.setEndTime(EndTime);
        giustificativi.setBeginTime(BeginTime);
        giustificativi.setIsPrepare(Giustificativi.IS_PREPARE_CREATE);
        sendData(giustificativi);
    }


    //escamotage per inserire elementi duplicati nella LinkedHashSet
    public void addElement(LinkedHashSet l, String e) {
        while(!l.add(e)) {
            e = e.concat("_");
        }
    }

    private void sendData(final Giustificativi giustificativi) {
        progress.show();

        settings = context.getSharedPreferences("giustificativiSettings",0);
        SharedPreferences.Editor editor = settings.edit();

        boolean found = settings.getBoolean("giustificativi", false);

        LinkedHashSet<String> begdas = new LinkedHashSet<>();
        LinkedHashSet<String> enddas = new LinkedHashSet<>();
        LinkedHashSet<String> subtys = new LinkedHashSet<>();
        LinkedHashSet<String> currnotices = new LinkedHashSet<>();
        LinkedHashSet<String> endtimes = new LinkedHashSet<>();
        LinkedHashSet<String> begintimes = new LinkedHashSet<>();
        LinkedHashSet<String> isprepares = new LinkedHashSet<>();
        LinkedHashSet<String> docnrs = new LinkedHashSet<>();
        LinkedHashSet<String> subtytypes = new LinkedHashSet<>();
        //LinkedHashSet<String> datesearchs = new LinkedHashSet<>();

        if(found) {

            begdas.addAll(settings.getStringSet("begdas",null));
            enddas.addAll(settings.getStringSet("enddas",null));
            subtys.addAll(settings.getStringSet("subtys",null));
            currnotices.addAll(settings.getStringSet("currnotices",null));
            endtimes.addAll(settings.getStringSet("endtimes",null));
            begintimes.addAll(settings.getStringSet("begintimes",null));
            isprepares.addAll(settings.getStringSet("isprepares",null));
            docnrs.addAll(settings.getStringSet("docnrs",null));
            subtytypes.addAll(settings.getStringSet("subtytypes",null));
            //datesearchs.addAll(settings.getStringSet("datesearchs",null));

        } else {
            editor.putBoolean("giustificativi", true);
            editor.putInt("ordering_var", ordering_var);
        }

        //variabile per ordinare gli elementi nella LinkedHashSet
        ordering_var = settings.getInt("ordering_var",1);

        addElement(begdas,String.valueOf(ordering_var).concat(giustificativi.getBegda()));
        addElement(enddas,String.valueOf(ordering_var).concat(giustificativi.getEndda()));
        addElement(subtys,String.valueOf(ordering_var).concat(giustificativi.getSubty()));
        addElement(currnotices,String.valueOf(ordering_var).concat(giustificativi.getCurrNotice()));
        addElement(endtimes,String.valueOf(ordering_var).concat(giustificativi.getEndTime()));
        addElement(begintimes,String.valueOf(ordering_var).concat(giustificativi.getBeginTime()));
        addElement(isprepares,String.valueOf(ordering_var).concat(giustificativi.getIsPrepare()));
        addElement(docnrs,String.valueOf(ordering_var).concat("cose"));
        addElement(subtytypes,String.valueOf(ordering_var).concat(giustificativi.getSubtypeDescription()));
        //datesearchs.add(giustificativi.getDateSearch());

        editor.putInt("ordering_var",++ordering_var);

        editor.putStringSet("begdas", begdas);
        editor.putStringSet("enddas", enddas);
        editor.putStringSet("subtys", subtys);
        editor.putStringSet("currnotices", currnotices);
        editor.putStringSet("endtimes", endtimes);
        editor.putStringSet("begintimes", begintimes);
        editor.putStringSet("isprepares", isprepares);
        editor.putStringSet("docnrs", docnrs);
        editor.putStringSet("subtytypes",subtytypes);
        //editor.putStringSet("datesearchs", datesearchs);

        editor.apply();




        //EssClient.fetchXCSRFToken(context, EssClient.EssEntity.GIUSTIFICATIVI, new FutureCallback<Response<JsonObject>>() {
            //@Override
            //public void onCompleted(Exception e, Response<JsonObject> result) {
                //try {
                    //String token = ResponseManager.getXCSRFToken(e, result);
                    progress.show();
                    //EssClient.createGiustificativo(context, token, giustificativi, new FutureCallback<Response<JsonObject>>() {
                        //@Override
                        //public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                //new ResponseManager(e, result);
                                //if (giustificativi.getIsPrepare().isEmpty()) {
                                    dialog.dismiss();
                                    if (onDismissListener != null) onDismissListener.onDismiss(null);
                                /*} else { // prepare
                                    Type giustificativoType = new TypeToken<Giustificativi>() {
                                    }.getType();
                                    Gson gson = Utils.getRealmGsonBuilder().create();
                                    Giustificativi g = gson.fromJson(result.getResult().getAsJsonObject("d"), giustificativoType);
                                    // Il servizio non ritorna i valori corretti di Docnr e DateSearch
                                    // il prendo dall oggetto precedente
                                    g.setDocnr(giustificativi.getDocnr());
                                    g.setDateSearch(giustificativi.getDateSearch());
                                    onPrepare(g);
                                }*/
                            } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                                new ErrorDialog(context, dataErrorExpection.getErrorDetails()).show();
                                dataErrorExpection.printStackTrace();
                            }*/ catch (Exception e1) {
                                showGenericErrorMessage();
                                e1.printStackTrace();
                            } finally {
                                progress.dismiss();
                            }
                        //}
                    //});
                //} catch (Exception e1) {
                  //  showGenericErrorMessage();
                    //e1.printStackTrace();
                //} finally {
                  //  progress.dismiss();
                //}
            //}
        //});
    }

    private void onPrepare(Giustificativi giustificativo) {
        giustificativo.setIsPrepare("");
        formUnClickable.setVisibility(View.VISIBLE);
        giustificativiDaConfermare = new Giustificativi();
        copyGiustificativo(giustificativo, giustificativiDaConfermare);
        dialog.setActionButton(DialogAction.POSITIVE, R.string.giustificativi_dialog_edit_conferma);
        dialog.setActionButton(NEUTRAL, R.string.giustificativi_dialog_edit_annulla);
        oreAssenzaPresenza.setText(giustificativo.getAttabsHours());
    }

    private void copyGiustificativo(Giustificativi vecchio, Giustificativi nuovo) {
        nuovo.setIsPrepare(vecchio.getIsPrepare());
        nuovo.setDocnr(vecchio.getDocnr());
        nuovo.setDateSearch(vecchio.getDateSearch());
        nuovo.setActionCancelVisibility(vecchio.getActionCancelVisibility());
        nuovo.setActionChangeVisibility(vecchio.getActionChangeVisibility());
        nuovo.setAttabsHours(vecchio.getAttabsHours());
        nuovo.setBegda(vecchio.getBegda());
        nuovo.setBeginTime(vecchio.getBeginTime());
        nuovo.setCurrNotice(vecchio.getCurrNotice());
        nuovo.setDeduction(vecchio.getDeduction());
        nuovo.setDeductionTooltip(vecchio.getDeductionTooltip());
        nuovo.setEndda(vecchio.getEndda());
        nuovo.setEndTime(vecchio.getEndTime());
        nuovo.setNamenxp(vecchio.getNamenxp());
        nuovo.setRequestId(vecchio.getRequestId());
        nuovo.setStatus(vecchio.getStatus());
        nuovo.setStatusText(vecchio.getStatusText());
        nuovo.setSubty(vecchio.getSubty());
        nuovo.setSubtypeDescription(vecchio.getSubtypeDescription());
    }

    private void showGenericErrorMessage() {
        new MaterialDialog.Builder(context)
                .title(R.string.attenzione)
                .content(R.string.si_e_verificato_un_errore)
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .show();
    }

    private void setAllFieldVisible() {
        onlySingleDay = false;
        fine.setVisibility(View.VISIBLE);
        fine_date.setVisibility(View.VISIBLE);
        fine_date_picker.setVisibility(View.VISIBLE);
        inizio_date.setVisibility(View.VISIBLE);
        inizio_date_picker.setVisibility(View.VISIBLE);
        oreAssenzaPresenzaContainer.setVisibility(View.VISIBLE);
    }

    private void setupUI(GiustificativiType type) {
        oreAssenzaPresenzaContainer.setVisibility(type.getNoPartialDay().equals("X")? View.INVISIBLE : View.VISIBLE);
        if (type.getOnlySingleDay().equals("X")) {
            onlySingleDay = true;
            fine.setVisibility(View.INVISIBLE);
            fine_date_picker.setVisibility(View.INVISIBLE);
            fine_date.setVisibility(View.INVISIBLE);
        } else {
            onlySingleDay = false;
            fine.setVisibility(View.VISIBLE);
            fine_date_picker.setVisibility(View.VISIBLE);
            fine_date.setVisibility(View.VISIBLE);
        }
        if (type.getNoPartialDay().equals("X")) {
            EndTime = "PT00H00M00S";
            BeginTime = "PT00H00M00S";
            inizio_date_picker.setVisibility(View.INVISIBLE);
            inizioOrario.setVisibility(View.INVISIBLE);
            fine_date_picker.setVisibility(View.INVISIBLE);
            fineOrario.setVisibility(View.INVISIBLE);
        } else {
            inizio_date_picker.setVisibility(View.VISIBLE);
            inizioOrario.setVisibility(View.VISIBLE);
            fine_date_picker.setVisibility(View.VISIBLE);
            fineOrario.setVisibility(View.VISIBLE);
        }
    }

}
