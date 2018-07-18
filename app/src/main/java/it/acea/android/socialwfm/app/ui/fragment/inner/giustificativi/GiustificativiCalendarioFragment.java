package it.acea.android.socialwfm.app.ui.fragment.inner.giustificativi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.calendar.CalendarView;
import org.fingerlinks.mobile.android.utils.widget.calendar.SupportCalendarItem;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.ErroreDownloadEvent;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.GiustificativiCalendar;
import it.acea.android.socialwfm.app.ui.dialog.GiustificativoCalendarDetailDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroGiustificativiCalendario;
import it.acea.android.socialwfm.app.ui.intro.IntroGiustificativiLista;
import it.acea.android.socialwfm.http.ess.EssClient;

/**
 * Created by raphaelbussa on 17/03/16.
 */
public class GiustificativiCalendarioFragment extends BaseFragment implements CalendarView.CalendarListener, View.OnClickListener {

    private static final String TAG = GiustificativiCalendarioFragment.class.getName();

    @Bind(R.id.arrow_left)
    ImageView arrow_left;
    @Bind(R.id.calendar_1)
    CalendarView calendar_1;
    @Bind(R.id.calendar_2)
    CalendarView calendar_2;
    @Bind(R.id.calendar_3)
    CalendarView calendar_3;
    @Bind(R.id.arrow_right)
    ImageView arrow_right;
    @Bind(R.id.date_picker)
    TextView date_picker;
    private MaterialDialog progress;
    private BroadcastReceiver receiver;

    private SharedPreferences settings;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_giustificativi_calendario, null);
        ButterKnife.bind(this, view);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData(currentDate, false);
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter(GiustificativiListFragment.UPDATE_CALENDAR));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    public void showIntro(){
        View actionView = getActivity().findViewById(R.id.menu_overflow);
        new IntroGiustificativiCalendario(getActivity(),this.date_picker,this.arrow_right,actionView).start();
    }

    private void getData(Date date) {
        getData(date, true);
    }


    private void getData(Date date, boolean dialog) {

        Date dataMesePrecedente = Utils.getDataMesePrecedente(date);

        if (dialog) {
            progress = new MaterialDialog.Builder(getActivity())
                    .content(R.string.loading)
                    .cancelable(false)
                    .progress(true, 0)
                    .build();
            progress.show();
        }
        EssClient.fetchGiustificativiCalendarSet(getActivity(), dataMesePrecedente , new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (progress != null && progress.isShowing()) progress.dismiss();
                if (e != null || result == null) {
                    loadDataFromRealm();
                    return;
                }
                try {
                    /*Type listType = new TypeToken<List<GiustificativiCalendar>>() {
                    }.getType();
                    GsonBuilder builder = Utils.getRealmGsonBuilder();
                    Gson gson = builder.create();*/
                    List<GiustificativiCalendar> giustificativis = getGiustList(); //gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.clear(GiustificativiCalendar.class);
                    realm.copyToRealmOrUpdate(giustificativis);
                    realm.commitTransaction();
                    loadDataFromRealm();

                    showIntro();

                } catch (Exception exception) {
                    EventBus.getDefault().post(new ErroreDownloadEvent());
                }
            }
        });
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
                moveCalendar(calendar);
                date_picker.setText(Utils.formattaData(currentDate));
            }
        });
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(getChildFragmentManager(), "");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCalendar();
        initArrow();
        currentDate = new Date(System.currentTimeMillis());
        getData(currentDate);
        date_picker.setText(Utils.formattaData(currentDate));
    }

    private Date currentDate;

    public void reloadData() {
        if (currentDate == null) return;
        getData(currentDate);
    }

    private List<SupportCalendarItem> calendarItems;


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


    private ArrayList<GiustificativiCalendar> getGiustList() {

        GiustificativiCalendar g1 = new GiustificativiCalendar();
        g1.setDate(getFormattedDate(new GregorianCalendar(2018,5,18)));
        g1.setYear("2018");
        g1.setMonth("6");
        g1.setMonthtext("Giugno");
        g1.setDay("18");
        g1.setDaytext("Lunedi");
        g1.setActkey("INV");
        g1.setActinfo("abc");
        g1.setActkeytext("Inviato");

        GiustificativiCalendar g2 = new GiustificativiCalendar();
        g2.setDate(getFormattedDate(new GregorianCalendar(2018,5,19)));
        g2.setYear("2018");
        g2.setMonth("6");
        g2.setMonthtext("Giugno");
        g2.setDay("19");
        g2.setDaytext("Martedi");
        g2.setActkey("INV");
        g2.setActinfo("ghi");
        g2.setActkeytext("Inviato");

        ArrayList<GiustificativiCalendar> g = new ArrayList<>();
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

                GiustificativiCalendar gg = new GiustificativiCalendar();

                gg.setDate(begdass[i]);


                Date d = new Date(Long.parseLong(begdass[i].substring(6,begdass[i].length()-2)));
                Date d1 = Utils.getTimePTHMS(begintimess[i]);
                Date d2 = Utils.getTimePTHMS(endtimess[i]);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                gg.setYear(String.valueOf(cal.get(Calendar.YEAR)));
                gg.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));

                DateFormatSymbols dfs = new DateFormatSymbols();
                gg.setMonthtext(dfs.getMonths()[cal.get(Calendar.MONTH)]);

                gg.setDay(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                gg.setDaytext(new SimpleDateFormat("EEEE").format(d));

                gg.setActkey("INV");
                gg.setActinfo(new SimpleDateFormat("dd.MM.yyyy").format(d)+" "+new SimpleDateFormat("HH:mm").format(d1)+
                        " - "+new SimpleDateFormat("HH:mm").format(d2)+" "+subtytypess[i]+" Inviato");
                gg.setActkeytext("Inviato");

                g.add(gg);

            }

        }


        return g;

    }

    String getFormattedDate(GregorianCalendar d) {
        return "/Date("+d.getTimeInMillis()+")/";
    }

    private void loadDataFromRealm() {
        calendarItems = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        List<GiustificativiCalendar> giustificativiList = getGiustList(); //realm.where(GiustificativiCalendar.class).findAll();
        for (GiustificativiCalendar giustificativi : giustificativiList) {
            SupportCalendarItem calendarItem = new SupportCalendarItem();
            calendarItem.setType(giustificativi.getActkey());
            calendarItem.setStyle(itemStyle(giustificativi.getActkey()));
            Log.d(TAG, "key " + giustificativi.getActkey() + " valore " + giustificativi.getActkeytext());
            calendarItem.setId(giustificativi.getDate());
            calendarItem.setDate(Utils.getDateFromString(giustificativi.getDate()));
            calendarItem.setData(giustificativi);
            calendarItems.add(calendarItem);
        }
        setEventsToCalendar(calendarItems);
    }

    private void initArrow() {
        arrow_left.setOnClickListener(this);
        arrow_right.setOnClickListener(this);
    }

    private Calendar value_calendar_1;
    private Calendar value_calendar_2;
    private Calendar value_calendar_3;

    private void moveCalendar(Calendar calendar) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(calendar.getTimeInMillis());
        calendar1.add(Calendar.MONTH, -1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(calendar.getTimeInMillis());

        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTimeInMillis(calendar.getTimeInMillis());
        calendar3.add(Calendar.MONTH, +1);

        value_calendar_1 = calendar1;
        value_calendar_2 = calendar2;
        value_calendar_3 = calendar3;
        calendar_1.initializeCalendar(value_calendar_1);
        calendar_2.initializeCalendar(value_calendar_2);
        calendar_3.initializeCalendar(value_calendar_3);
        if (calendarItems != null && calendarItems.size() != 0) setEventsToCalendar(calendarItems);
    }

    private void initCalendar() {
        calendar_1.setCalendarListener(this);
        calendar_2.setCalendarListener(this);
        calendar_3.setCalendarListener(this);

        value_calendar_1 = Calendar.getInstance(Locale.getDefault());
        value_calendar_1.add(Calendar.MONTH, -1);
        value_calendar_2 = Calendar.getInstance(Locale.getDefault());
        value_calendar_3 = Calendar.getInstance(Locale.getDefault());
        value_calendar_3.add(Calendar.MONTH, +1);

        calendar_1.initializeCalendar(value_calendar_1);
        calendar_2.initializeCalendar(value_calendar_2);
        calendar_3.initializeCalendar(value_calendar_3);
    }

    private void setEventsToCalendar(List<SupportCalendarItem> calendarItems) {
        calendar_1.setEventsToCalendar(calendarItems);
        calendar_2.setEventsToCalendar(calendarItems);
        calendar_3.setEventsToCalendar(calendarItems);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onDateSelected(Date date, SupportCalendarItem item) {
        Log.d(TAG, "Data selezionata");
        //listener called on date click
        if (item != null) {
            new GiustificativoCalendarDetailDialog(getContext(), ((GiustificativiCalendar) item.getData())).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_right:
                value_calendar_1.add(Calendar.MONTH, +1);
                value_calendar_2.add(Calendar.MONTH, +1);
                value_calendar_3.add(Calendar.MONTH, +1);
                calendar_1.initializeCalendar(value_calendar_1);
                calendar_2.initializeCalendar(value_calendar_2);
                calendar_3.initializeCalendar(value_calendar_3);
                break;
            case R.id.arrow_left:
                value_calendar_1.add(Calendar.MONTH, -1);
                value_calendar_2.add(Calendar.MONTH, -1);
                value_calendar_3.add(Calendar.MONTH, -1);
                calendar_1.initializeCalendar(value_calendar_1);
                calendar_2.initializeCalendar(value_calendar_2);
                calendar_3.initializeCalendar(value_calendar_3);
                break;
        }
        currentDate = value_calendar_2.getTime();
        date_picker.setText(Utils.formattaData(currentDate));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentDate.getTime());
        dataRequest = calendar;
        getData(currentDate);
    }

    private int itemStyle(String type) {
        //TODO aggiungere i case "canc richiesto" e "assente" non ho trovato le key
        switch (type) {
            case "NLA":
                return R.color.legenda_giorno_non_lavorativo;
            case "INV":
                return R.color.legenda_inviato;
            case "FST":
                return R.color.legenda_festivo;
            case "INS":
                return R.color.legenda_piu_inserimenti;
            case "CNC":
                return R.color.legenda_canc_richiesto;
            case "ASS":
                return R.color.legenda_assenza;
            default:
                return R.color.primary;
        }
    }

}
