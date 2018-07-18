package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.ui.adapter.TimbratureCalendarioEventsAdapter;
import it.acea.android.socialwfm.app.ui.dialog.TimbratureDettaglioItemDialog;
import it.acea.android.socialwfm.app.ui.fragment.TimbratureFragment;
import it.acea.android.socialwfm.app.ui.intro.IntroTimbratureCalendario;
import it.acea.android.socialwfm.app.ui.intro.IntroTimbratureLista;

/**
 * Created by a.simeoni on 16/03/2016.
 */
public class FragmentTimbratureCalendario extends Fragment implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener {

    private static final String TAG = FragmentTimbratureCalendario.class.getSimpleName();
    @Bind(R.id.weekView)
    WeekView weekView;

    @Bind(R.id.timbrature_calendario_date_select)
    TextView dateSelect;

    @Bind(R.id.timbrature_calendario_previous_date)
    ImageView previousWeekSelect;

    @Bind(R.id.timbrature_calendario_next_date)
    ImageView nextWeekSelect;

    Timbratura timbratura;
    ArrayList<Timbratura.TimbraturaOrarioWeekEvent> weekEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timbrature_calendario, container, false);
        ButterKnife.bind(this, v);
        weekView.setOnEventClickListener(this);
        weekView.setMonthChangeListener(this);
        weekView.setEventLongPressListener(this);
        weekView.setFirstDayOfWeek(Calendar.MONDAY);
        weekView.setXScrollingSpeed(0);
        setupDateTimeInterpreter();
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


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        List<Timbratura.TimbraturaOrarioWeekEvent> events = getEventWithStartTime(event, weekEvents);
        if (events.size() > 1) {
            showMultipleEventsDialog(events);
        } else {
            Timbratura.TimbraturaOrario orario = ((Timbratura.TimbraturaOrarioWeekEvent) event).getTimbraturaOrario();
            if (orario != null && orario.getDettaglio() != null) {
                new TimbratureDettaglioItemDialog(getContext(), orario.getDettaglio()).show();
            }
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Return only the events that matches newYear and newMonth.
        List<WeekViewEvent> matchedEvents = new ArrayList<>();
        for (WeekViewEvent event : weekEvents) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        /*
        * La weekview non si posiziona nel giorno scelto col metodo goToDate() prima di essere visibile
        * probabilmente a causa di un bug https://github.com/alamkanak/Android-Week-View/issues/127
        * richiamo il metodo per settare il giorno
        * */
        TimbratureFragment.MessageDateSet m = EventBus.getDefault().getStickyEvent(TimbratureFragment.MessageDateSet.class);
        dateSet(m);
        return matchedEvents;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    @Subscribe(sticky = true)
    public void onDataFetched(TimbratureFragment.MessageTimbraburaEvent event) {
        this.timbratura = event.timbratura;
        buildWeekEvents(timbratura);
    }

    @Subscribe(sticky = true)
    public void dateSet(TimbratureFragment.MessageDateSet event) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(event.year, event.monthOfYear, event.dayOfMonth);
        String d = DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY).format(c.getTime());
        dateSelect.setText(d);
        // Scelgo di andare al lunedì della settimana che contiene la data
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekView.goToDate(c);
    }

    @OnClick(R.id.timbrature_calendario_date_select)
    public void showDatePicker() {
        EventBus.getDefault().post(new TimbratureFragment.MessageShowDatePicker());
    }

    @OnClick(R.id.timbrature_calendario_next_date)
    public void nextWeekSelect() {
        EventBus.getDefault().post(new TimbratureFragment.MessageRequestNextWeekData());
    }

    @OnClick(R.id.timbrature_calendario_previous_date)
    public void previousWeekSelect() {
        EventBus.getDefault().post(new TimbratureFragment.MessageRequestPreviousWeekData());
    }

    private void buildWeekEvents(Timbratura timbratura) {
        weekEvents.clear();
        if (timbratura == null) return;
        for (Timbratura.TimbraturaOrario to : timbratura.getOrari()) {
            weekEvents.add(to.toTimbraturaOrarioWeekEvent(getContext()));
        }
    }

    public void showIntro(){
        // TODO: Capire perchè il getActivity() restituisce null
        // dobbiamo invocarlo per poter recuperare un riferimento al menu contestuale
    }

    /**
     * Checks if an event falls into a specific year and month.
     *
     * @param event The event to check for.
     * @param year  The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    private List<Timbratura.TimbraturaOrarioWeekEvent> getEventWithStartTime(WeekViewEvent thisEvent, List<Timbratura.TimbraturaOrarioWeekEvent> events) {
        ArrayList<Timbratura.TimbraturaOrarioWeekEvent> sameTimeEvents = new ArrayList<>();
        for (Timbratura.TimbraturaOrarioWeekEvent event : events) {
            if (isSameStartTime(thisEvent, event)) {
                sameTimeEvents.add(event);
            }
        }
        return sameTimeEvents;
    }

    private boolean isSameStartTime(WeekViewEvent event1, WeekViewEvent event2) {
        Calendar c1 = event1.getStartTime();
        Calendar c2 = event2.getStartTime();
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) &&
                c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY) &&
                c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
    }

    public void showMultipleEventsDialog(List<Timbratura.TimbraturaOrarioWeekEvent> events) {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_timbrature_calendar_events, false)
                .positiveText(R.string.chiudi)
                .build();
        TimbratureCalendarioEventsAdapter adapter = new TimbratureCalendarioEventsAdapter(getContext(), events);
        View view = dialog.getCustomView();
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.timbrature_calendario_events_rv);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.LTGRAY)
                        .sizeResId(R.dimen.divider)
                        //.marginResId(R.dimen.list_margin_left, R.dimen.list_margin_right)
                        .build());

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        dialog.show();

    }


    private void setupDateTimeInterpreter() {
        weekView.setDateTimeInterpreter(
                new DateTimeInterpreter() {
                    @Override
                    public String interpretDate(Calendar date) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/M/yy", Locale.getDefault());
                            return sdf.format(date.getTime()).toUpperCase();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }
                    }

                    @Override
                    public String interpretTime(int hour) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, 0);

                        try {
                            SimpleDateFormat sdf = android.text.format.DateFormat.is24HourFormat(getContext()) ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("hh a", Locale.getDefault());
                            return sdf.format(calendar.getTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }
                    }
                });
    }
}
