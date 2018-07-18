package it.acea.android.socialwfm.app.ui.dialog;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Calendar;
import java.util.Date;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.delegate.SWFMSelectDataInizioFineDialogDelegate;

;

/**
 * Created by a.simeoni on 08/03/2016.
 */

public class SelectDataInizioFineDialog implements CalendarDatePickerDialogFragment.OnDateSetListener, View.OnClickListener {
    private FragmentManager manager;
    private Context context;
    private Date dataInizio;
    private Date dataFine;
    private CalendarDatePickerDialogFragment startDateCalendarFragment, endDateCalendarFragment;
    private static final String CALENDAR_START = "START_DATE";
    private static final String CALENDAR_END = "END_DATE";
    private SWFMSelectDataInizioFineDialogDelegate delegate;


    EditText tvDataInizio;
    EditText tvDataFine;
    Button btDataInizio;
    Button btDataFine;


    private MaterialDialog dialog;

    public SelectDataInizioFineDialog(Context context, FragmentManager manager, Date inizio, Date fine,
                                      SWFMSelectDataInizioFineDialogDelegate del) {
        this.context = context;
        this.delegate = del;
        this.manager = manager;
        this.dataInizio = inizio != null ? inizio : new Date();
        this.dataFine = fine != null ? fine : new Date();

        boolean wrapInScrollView = true;
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.dialog_data_inizio_fine_titolo)
                .customView(R.layout.dialog_seleziona_data_inizio_fine, wrapInScrollView)
                .positiveText(R.string.dialog_data_inizio_fine_download)
                .build();


        overridePositiveActionListener();

        // Date pickers
        this.startDateCalendarFragment = new CalendarDatePickerDialogFragment();
        this.endDateCalendarFragment = new CalendarDatePickerDialogFragment();

        // Buttons
        View custom = dialog.getCustomView();
        this.btDataInizio = (Button) custom.findViewById(R.id.dialog_seleziona_data_inizio_fine_data_inizio_button);
        this.btDataFine = (Button) custom.findViewById(R.id.dialog_seleziona_data_inizio_fine_data_fine_button);
        this.btDataInizio.setOnClickListener(this);
        this.btDataFine.setOnClickListener(this);


        // TextViews
        this.tvDataInizio = (EditText) custom.findViewById(R.id.dialog_seleziona_data_inizio_fine_data_inizio_value);
        this.tvDataFine = (EditText) custom.findViewById(R.id.dialog_seleziona_data_inizio_fine_data_fine_value);
        this.tvDataInizio.setText(Utils.dateFormat(this.dataInizio));
        this.tvDataFine.setText(Utils.dateFormat(this.dataFine));

        dialog.show();
        this.animateViewsIn(tvDataInizio, tvDataFine);
    }

    private int getPositiveActionButtonId() {
        MDButton b = dialog.getActionButton(DialogAction.POSITIVE);
        return b.getId();
    }

    private void overridePositiveActionListener() {
        MDButton b = dialog.getActionButton(DialogAction.POSITIVE);
        b.setOnClickListener(this);
    }

    public void onSelectDataInizio() {
        showCalendar(this.endDateCalendarFragment, this.dataInizio, SelectDataInizioFineDialog.CALENDAR_START);
    }


    public void onSelectDataFine() {
        showCalendar(this.endDateCalendarFragment, this.dataFine, SelectDataInizioFineDialog.CALENDAR_END);
    }

    private void showCalendar(CalendarDatePickerDialogFragment cdp, Date selectedDate, String TAG) {
        Calendar aDay = Calendar.getInstance();
        aDay.setTime(selectedDate);
        cdp.setPreselectedDate(aDay.get(Calendar.YEAR), aDay.get(Calendar.MONTH), aDay.get(Calendar.DAY_OF_MONTH));
        cdp.setOnDateSetListener(this);
        cdp.setThemeCustom(R.style.MyCustomBetterPickerTheme);
        cdp.show(manager, TAG);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String TAG = dialog.getTag();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date d = calendar.getTime();

        if (TAG.equals(CALENDAR_START)) {
            this.dataInizio = d;
            this.tvDataInizio.setText(Utils.dateFormat(d));
            animateSelectedDate(this.tvDataInizio);
        } else if (TAG.equals(CALENDAR_END)) {
            this.dataFine = d;
            this.tvDataFine.setText(Utils.dateFormat(d));
            this.animateSelectedDate(this.tvDataFine);
        }
    }

    private void animateSelectedDate(View view) {
        Techniques technique = Techniques.BounceIn;
        YoYo.with(technique)
                .duration(800)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(view);
    }

    private void animateViewShake(View v) {
        Techniques technique = Techniques.Shake;
        YoYo.with(technique)
                .duration(800)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(v);
    }

    private void animateViewsIn(View v1, View v2) {
        Techniques technique = Techniques.Landing;
        YoYo.with(technique)
                .duration(800)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(v1);
        YoYo.with(technique)
                .duration(800)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(v2);
    }

    private void cleanError() {
        this.tvDataFine.setError(null);
        this.tvDataInizio.setError(null);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dialog_seleziona_data_inizio_fine_data_inizio_button: {
                cleanError();
                this.onSelectDataInizio();
                break;
            }
            case R.id.dialog_seleziona_data_inizio_fine_data_fine_button: {
                cleanError();
                this.onSelectDataFine();
                break;
            }
            default: {
                if (v.getId() == getPositiveActionButtonId()) {
                    if (dataInizio.compareTo(dataFine) > 0) {
                        String error = context.getResources().getString(R.string.dialog_data_inizio_fine_data_fine_maggiore_data_inizio);
                        this.tvDataFine.setError(error);
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        animateViewShake(this.tvDataFine);
                    } else {
                        dialog.dismiss();
                        this.delegate.onDatesChosen(dataInizio, dataFine);
                    }
                }
            }
        }
    }
}
