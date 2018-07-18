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
public class IntroTimbratureLista extends Intro{

    public static final String SELECT_DATA = "TIMBRATURE_LISTA_SELECT_DATA";
    public static final String ACTION_VIEW = "TIMBRATURE_LISTA_ACTION_VIEW";

    private Activity activity;
    private View selectDataView;
    private View actionMenu;

    public IntroTimbratureLista(Activity activity, View selectDataView, View actionMenu) {
        super(activity);
        this.activity = activity;
        this.selectDataView = selectDataView;
        this.actionMenu = actionMenu;
    }



    @Override
    public void start(){
        super.start();
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_TIMBRATURE)) {
            showIntro(selectDataView, SELECT_DATA, activity.getString(R.string.seleziona_data), FocusGravity.RIGHT);
        }*/
    }

    @Override
    public void onUserClicked(String s) {
        if (s.equals(SELECT_DATA)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(activity.getString(R.string.action_descrizione));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.aggiorna_intro)+" </b>"));
            ssb.append(activity.getString(R.string.aggiorna_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ordina_intro)+" </b>"));
            ssb.append(activity.getString(R.string.ordina_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.legenda_intro)+" </b>"));
            ssb.append(activity.getString(R.string.legenda_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.messaggi_intro)+" </b>"));
            ssb.append(activity.getString(R.string.messaggi_intro_descrizione));
            showIntro(actionMenu, ACTION_VIEW, ssb, FocusGravity.CENTER, false);
        }
    }
}
