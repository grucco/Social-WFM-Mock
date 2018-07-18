package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;

import co.mobiwise.materialintro.shape.FocusGravity;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by a.simeoni on 22/08/2016.
 */
public class IntroReperibilita extends Intro {

    public static final String SELECT_DATA = "REPERIBILITA_SELECT_DATA";
    public static final String NAVIGATE_MONTHS = "REPERIBILITA_NAVIGATE_MONTHS";
    public static final String ACTION_VIEW = "REPERIBILITA_ACTION_VIEW";

    private Activity activity;
    private View selectDataView;
    private View navigateMonthsView;
    private View actionMenu;


    public IntroReperibilita(Activity activity, View selectDataView,View navigateMonthsView, View actionMenu) {
        super(activity);
        this.activity = activity;
        this.selectDataView = selectDataView;
        this.navigateMonthsView = navigateMonthsView;
        this.actionMenu = actionMenu;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_REPERIBILITA)) {
            userLearnt(SharedPreferenceAdapter.INTRO_REPERIBILITA);
            showIntro(selectDataView, SELECT_DATA, activity.getString(R.string.seleziona_data), FocusGravity.CENTER);
        }*/
    }

    @Override
    public void onUserClicked(String s) {
        if (s.equals(SELECT_DATA)) {
            showIntro(this.navigateMonthsView, NAVIGATE_MONTHS, activity.getString(R.string.naviga_mesi), FocusGravity.CENTER, false);
        }
        if (s.equals(NAVIGATE_MONTHS)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(activity.getString(R.string.action_descrizione));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.aggiorna_intro)+" </b>"));
            ssb.append(activity.getString(R.string.aggiorna_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.esporta_pdf_reperibilità_intro)+" </b>"));
            ssb.append(activity.getString(R.string.esporta_pdf__reperibilità_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.esporta_excel_reperibilità_intro)+" </b>"));
            ssb.append(activity.getString(R.string.esporta_excel__reperibilità_intro_descrizione));
            showIntro(actionMenu, ACTION_VIEW, ssb, FocusGravity.CENTER, false);
        }
    }
}
