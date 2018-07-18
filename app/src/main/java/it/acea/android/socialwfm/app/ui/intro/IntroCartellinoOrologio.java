package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;

import co.mobiwise.materialintro.shape.FocusGravity;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by nicola on 10/08/16.
 */
public class IntroCartellinoOrologio extends Intro {
    private final String PERIODO = "PERIODO_CARTELLINO";
    private final String DATA = "DATA_CARTELLINO";
    private final String ACTION = "ACTION_CARTELLINO";

    private View periodoView;
    private View dataView;
    private View actionView;
    private Activity activity;

    public IntroCartellinoOrologio(Activity activity, View periodoView, View dataView,
                                   View actionView) {
        super(activity);
        this.activity = activity;
        this.periodoView = periodoView;
        this.dataView = dataView;
        this.actionView = actionView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_CARTELLINO)){
            userLearnt(SharedPreferenceAdapter.INTRO_CARTELLINO);
            showIntro(periodoView, PERIODO, "Seleziona periodo", FocusGravity.CENTER);
        }*/
    }

    @Override
    public void onUserClicked(String s) {
        if (s.equals(PERIODO)) showIntro(dataView, DATA, "Seleziona range di date", FocusGravity.CENTER);
        if (s.equals(DATA)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(activity.getString(R.string.action_descrizione));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.aggiorna_intro)+" </b>"));
            ssb.append(activity.getString(R.string.aggiorna_intro_descrizione));
            ssb.append("\n");
            ssb.append("\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ordina_intro)+" </b>"));
            ssb.append(activity.getString(R.string.ordina_intro_descrizione));
            showIntro(actionView, ACTION, ssb, FocusGravity.CENTER, false);
        }
    }
}
