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
public class IntroAnalisiQuadratura extends Intro {
    private final String DATA = "DATA_QUADRATURA";
    private final String ACTION = "ACTION_QUADRATURA";

    private Activity activity;
    private View dataView;
    private View actionView;


    public IntroAnalisiQuadratura(Activity activity, View dataView, View actionView) {
        super(activity);
        this.activity = activity;
        this.dataView = dataView;
        this.actionView = actionView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_QUADRATURA)){
            userLearnt(SharedPreferenceAdapter.INTRO_QUADRATURA);
            showIntro(dataView, DATA,activity.getString(R.string.seleziona_data), FocusGravity.RIGHT);
        }*/
    }

    @Override
    public void onUserClicked(String s) {
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

            showIntro(this.actionView, ACTION, ssb, FocusGravity.CENTER, false);
        }
    }
}
