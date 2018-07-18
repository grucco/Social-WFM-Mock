package it.acea.android.socialwfm.app.ui.fragment.inner.giustificativi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.ErroreDownloadEvent;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.GiustificativiType;
import it.acea.android.socialwfm.app.ui.adapter.GiustificativiAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ErrorDialog;
import it.acea.android.socialwfm.app.ui.dialog.GiustificativoEditDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroGiustificativiLista;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.http.response.ResponseManager;
import tr.xip.errorview.ErrorView;

/**
 * Created by raphaelbussa on 21/03/16.
 */
public class GiustificativiListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String UPDATE_CALENDAR = "UPDATE_CALENDAR";
    public static final String UPDATE_CALENDAR_MSG = "UPDATE_CALENDAR_MSG";
    private static final String TAG = GiustificativiListFragment.class.getName();

    private SharedPreferences settings;
    //private Context context;

    @Bind(R.id.list_giustificativi)
    ListView list_giustificativi;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    @Bind(R.id.error_view)
    ErrorView error_view;

    @Bind(R.id.add)
    View newGiustificativo;

    @Bind(R.id.start_date)
    TextView start_date;
    @Bind(R.id.end_date)
    TextView end_date;
    @Bind(R.id.date_picker)
    TextView date_picker;

    private GiustificativiAdapter adapter;
    private MaterialDialog progress;
    private Gson gson;

    private Date currentDate;
    private LocalBroadcastManager broadcaster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_giustificativi_lista, null);
        ButterKnife.bind(this, view);
        return view;
    }


    public String[] sortElements(String[] l) {
        int[] ordering_vars = new int[l.length];
        for(int i=0; i<l.length; i++) {
            ordering_vars[i] = Character.getNumericValue((l[i].charAt(0)));
            l[i] = getProperElement(l[i].substring(1));
        }
        String[] new_l = new String[l.length];
        for(int i=0; i<l.length; i++) {
            new_l[ordering_vars[i]-1] = l[i];
        }
        return new_l;
    }


    //escamotage per prendere l'elemento duplicato all'interno della LinkedHashSet
    public String getProperElement(String e) {
        if(e==null)
            return null;
        if(e.equals(""))
            return "";
        while((e.length() > 0) && (e.charAt(e.length()-1)=='_')) {
            e = e.substring(0, e.length()-1);
        }
        return e;
    }



    private ArrayList<Giustificativi> getGiust() {

        Giustificativi g1 = new Giustificativi();
        g1.setRequestId("1");
        g1.setBegda(getFormattedDate(new GregorianCalendar(2018,5,19)));
        g1.setBeginTime(getFormattedTime(new GregorianCalendar(2018,5,18,10,15).getTime()));
        g1.setEndda(getFormattedDate(new GregorianCalendar(2018,5,19)));
        g1.setEndTime(getFormattedTime(new GregorianCalendar(2018,5,18,11,20).getTime()));
        g1.setStatus("INV");
        g1.setAttabsHours("1.00");
        g1.setStatusText("Inviato");
        g1.setSubty("01");
        g1.setSubtypeDescription("Funzione Religiosa");
        g1.setDocnr("cose");

        Giustificativi g2 = new Giustificativi();
        g2.setRequestId("2");
        g2.setBegda(getFormattedDate(new GregorianCalendar(2018,5,20)));
        g2.setBeginTime(getFormattedTime(new GregorianCalendar(2018,5,19,12,15).getTime()));
        g2.setEndda(getFormattedDate(new GregorianCalendar(2018,5,20)));
        g2.setEndTime(getFormattedTime(new GregorianCalendar(2018,5,19,14,20).getTime()));
        g2.setStatus("INV");
        g2.setAttabsHours("1.00");
        g2.setStatusText("Inviato");
        g2.setSubty("20");
        g2.setSubtypeDescription("Ferie a Giorni");
        g2.setDocnr("cose");

        ArrayList<Giustificativi> g = new ArrayList<>();
        g.add(g1);
        g.add(g2);

        Context context = getContext();
        settings = context.getSharedPreferences("giustificativiSettings",0);


        //PER CANCELLARE CIO' CHE E' MEMORIZZATO NELLE SHAREDPREFERENCES
        /*SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("begdas", null);
        editor.putStringSet("enddas", null);
        editor.putStringSet("subtys", null);
        editor.putStringSet("currnotices", null);
        editor.putStringSet("endtimes", null);
        editor.putStringSet("begintimes", null);
        editor.putStringSet("isprepares", null);
        editor.putStringSet("docnrs", null);
        editor.putStringSet("subtytypes",null);
        editor.putBoolean("giustificativi",false);
        editor.putInt("ordering_var",1);
        editor.apply();*/



        boolean found = settings.getBoolean("giustificativi",false);
        if(found) {

            //NB: alcuni campi (ad es. docnrs) hanno elementi duplicati,
            //pertanto non vengono inseriti più volte nei Set
            LinkedHashSet<String> begdas = new LinkedHashSet<>();
            LinkedHashSet<String> enddas = new LinkedHashSet<>();
            LinkedHashSet<String> subtys = new LinkedHashSet<>();
            LinkedHashSet<String> currnotices = new LinkedHashSet<>();
            LinkedHashSet<String> endtimes = new LinkedHashSet<>();
            LinkedHashSet<String> begintimes = new LinkedHashSet<>();
            LinkedHashSet<String> isprepares = new LinkedHashSet<>();
            LinkedHashSet<String> docnrs = new LinkedHashSet<>();
            LinkedHashSet<String> subtytypes = new LinkedHashSet<>();

            begdas.addAll(settings.getStringSet("begdas",null));
            enddas.addAll(settings.getStringSet("enddas",null));
            subtys.addAll(settings.getStringSet("subtys",null));
            currnotices.addAll(settings.getStringSet("currnotices",null));
            endtimes.addAll(settings.getStringSet("endtimes",null));
            begintimes.addAll(settings.getStringSet("begintimes",null));
            isprepares.addAll(settings.getStringSet("isprepares",null));
            docnrs.addAll(settings.getStringSet("docnrs",null));
            subtytypes.addAll(settings.getStringSet("subtytypes",null));

            String[] begdass = new String[begdas.size()];
            begdas.toArray(begdass);
            begdass = sortElements(begdass);
            String[] enddass = new String[enddas.size()];
            enddas.toArray(enddass);
            enddass = sortElements(enddass);
            String[] subtyss = new String[subtys.size()];
            subtys.toArray(subtyss);
            subtyss = sortElements(subtyss);
            String[] currnoticess = new String[currnotices.size()];
            currnotices.toArray(currnoticess);
            currnoticess = sortElements(currnoticess);
            String[] endtimess = new String[endtimes.size()];
            endtimes.toArray(endtimess);
            endtimess = sortElements(endtimess);
            String[] begintimess = new String[begintimes.size()];
            begintimes.toArray(begintimess);
            begintimess = sortElements(begintimess);
            String[] ispreparess = new String[isprepares.size()];
            isprepares.toArray(ispreparess);
            ispreparess = sortElements(ispreparess);
            String[] docnrss = new String[docnrs.size()];
            docnrs.toArray(docnrss);
            docnrss = sortElements(docnrss);
            String[] subtytypess = new String[subtytypes.size()];
            subtytypes.toArray(subtytypess);
            subtytypess = sortElements(subtytypess);

            for(int i=0; i<begdas.size(); i++) {

                Giustificativi gg = new Giustificativi();
                gg.setRequestId(String.valueOf(i+3));
                gg.setBegda(begdass[i]);
                gg.setBeginTime(begintimess[i]);
                gg.setEndda(enddass[i]);
                gg.setEndTime(endtimess[i]);
                gg.setStatus("INV");
                gg.setAttabsHours("1.00");
                gg.setStatusText("Inviato");
                gg.setSubty(subtyss[i]);
                gg.setSubtypeDescription(subtytypess[i]);
                gg.setDocnr(docnrss[i]);
                gg.setCurrNotice(currnoticess[i]);
                gg.setIsPrepare(ispreparess[i]);


                g.add(gg);

            }

        }

        return g;

    }

    String getFormattedTime(Date d) {
        return "PT"+d.getHours()+"H"+d.getMinutes()+"M"+d.getSeconds()+"S";
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

    String getFormattedDate(GregorianCalendar d) {
        return "/Date("+d.getTimeInMillis()+")/";
    }


    private void getData(final Date date) {

        item = -1;
        cleanCompounds();

        progress = new MaterialDialog.Builder(getActivity())
                .content(R.string.loading)
                .cancelable(false)
                .progress(true, 0)
                .build();
        progress.show();


        EssClient.fetchGiustificativiTypeSet(getActivity(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                /*GsonBuilder builder = Utils.getRealmGsonBuilder();
                gson = builder.create();
                Type listType = new TypeToken<List<GiustificativiType>>() {
                }.getType();*/
                try {
                    //if (e != null) throw e;
                    List<GiustificativiType> typeList = getGiustifType(); //gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(typeList);
                    realm.commitTransaction();
                    EssClient.fetchGiustificativiSet(getActivity(), date, new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {

                            refresh_layout.setRefreshing(false);
                            try {
                                /*new ResponseManager(e, result);
                                Type listType = new TypeToken<List<Giustificativi>>() {
                                }.getType();*/
                                List<Giustificativi> giustificativis = getGiust(); //gson.fromJson(result.getResult().getAsJsonObject("d").get("results"), listType);
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.clear(Giustificativi.class);
                                realm.copyToRealmOrUpdate(giustificativis);
                                realm.commitTransaction();
                                cleanGiustificativiRealm();
                                loadDataFromRealm(item);
                                showIntro();
                            } /*catch (ResponseManager.DataErrorExpection dataErrorExpection) {
                                new ErrorDialog(getContext(), dataErrorExpection.getErrorDetails()).show();
                                dataErrorExpection.printStackTrace();
                            }*/ catch (Exception e2) {
                                EventBus.getDefault().post(new ErroreDownloadEvent());
                            } finally {
                                progress.dismiss();
                            }
                        }
                    });
                } catch (Exception exception) {
                    EventBus.getDefault().post(new ErroreDownloadEvent());
                    progress.dismiss();
                    refresh_layout.setRefreshing(false);
                }
            }
        });
        progress.dismiss();
    }

    private Calendar dataRequest;

    @OnClick(R.id.date_picker)
    void showPicker() {
        if (dataRequest == null) dataRequest = Calendar.getInstance();
        int y = dataRequest.get(Calendar.YEAR);
        int m = dataRequest.get(Calendar.MONTH);
        int d = dataRequest.get(Calendar.DAY_OF_MONTH);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.setPreselectedDate(y, m, d);
        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                dataRequest = calendar;
                currentDate = calendar.getTime();
                getData(currentDate);
                date_picker.setText(Utils.formattaData(currentDate));
            }
        });
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getChildFragmentManager(), "");
    }

    private void loadDataFromRealm(int item) {
        //dataLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
        Realm realm = Realm.getDefaultInstance();
        List<Giustificativi> giustificativiList;

        switch (item) {
            case 0:
                start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
                end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                giustificativiList = realm.where(Giustificativi.class).findAllSorted("Begda", false);
                break;
            case 1:
                start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
                end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                giustificativiList = realm.where(Giustificativi.class).findAllSorted("Begda", true);
                break;
            case 2:
                start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
                giustificativiList = realm.where(Giustificativi.class).findAllSorted("Endda", false);
                break;
            case 3:
                start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_asc, 0);
                giustificativiList = realm.where(Giustificativi.class).findAllSorted("Endda", true);
                break;
            default:
                start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_desc, 0);
                end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                giustificativiList = realm.where(Giustificativi.class).findAllSorted("Begda", true);
                break;
        }

        if (giustificativiList.size() == 0) {
            error_view.setVisibility(View.VISIBLE);
            error_view.setTitle("Nessun giustificativo trovato");
            error_view.setSubtitle("Prova a cambiare data...");
            error_view.showRetryButton(false);
        } else {
            error_view.setVisibility(View.GONE);
        }
        adapter.setGiustificativiList(giustificativiList);
        progress.dismiss();
        refresh_layout.setRefreshing(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh_layout.setOnRefreshListener(this);
        adapter = new GiustificativiAdapter(getActivity(), getChildFragmentManager());
        adapter.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getData(currentDate);
                updateCalendar();
            }
        });
        list_giustificativi.setAdapter(adapter);
        currentDate = new Date(System.currentTimeMillis());
        getData(currentDate);
        date_picker.setText(Utils.formattaData(currentDate));
    }

    @OnClick(R.id.add)
    void onAddClick() {
        GiustificativoEditDialog dialog = new GiustificativoEditDialog(getActivity(), getChildFragmentManager());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getData(currentDate);
                updateCalendar();
            }
        });
        dialog.show();
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
        refresh_layout.setRefreshing(true);
        if (currentDate == null) return;
        getData(currentDate);
    }

    public void showIntro(){
        View actionView = getActivity().findViewById(R.id.menu_overflow);
        new IntroGiustificativiLista(getActivity(),actionView,date_picker,newGiustificativo).start();
    }

    public void reloadData() {
        if (currentDate == null) return;
        getData(currentDate);
    }

    private int item = 0;

    public void filterData() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.ordina)
                .items(R.array.cartellino_orologio_filtra_data_items)
                .itemsCallbackSingleChoice(item, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        item = which;
                        cleanCompounds();
                        loadDataFromRealm(item);
                        return false;
                    }
                })
                .show();

    }

    private void cleanCompounds() {
        start_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        end_date.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void showConnectionError(){
        error_view.setVisibility(View.VISIBLE);
        error_view.setTitle(getContext().getResources().getString(R.string.error_view_oops));
        error_view.setSubtitle(getContext().getResources().getString(R.string.error_message_network));
        error_view.setRetryButtonText(getContext().getResources().getString(R.string.riprova));
        error_view.showRetryButton(true);
    }

    private void updateCalendar() {
        if (broadcaster == null) broadcaster = LocalBroadcastManager.getInstance(getContext());
        Intent intent = new Intent(UPDATE_CALENDAR);
        intent.putExtra(UPDATE_CALENDAR_MSG, "update");
        broadcaster.sendBroadcast(intent);
    }

    /*
    Alcuni giustificativi con un tipo speciale non riesco a gestirli, li rimuovo dal db
    Controllare la documentazione dei servizi per maggiorni informazioni
    * */
    private void cleanGiustificativiRealm() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<Giustificativi> giustificativi = realm.allObjects(Giustificativi.class);
                List<GiustificativiType> types = realm.allObjects(GiustificativiType.class);

                List<String> typesValues = new ArrayList<>();
                for (GiustificativiType type: types) {
                    typesValues.add(type.getSubty());
                }
                for (int i =0; i < giustificativi.size(); i++) {
                    Giustificativi g = giustificativi.get(i);
                    if (!typesValues.contains(g.getSubty()) && g.getDocnr().equals(Giustificativi.EMPTY_DOC_NR) && g.getDateSearch().isEmpty()) {
                        g.removeFromRealm();
                    }
                }
            }
        });
    }
}
