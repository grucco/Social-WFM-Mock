package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;

import co.mobiwise.materialintro.shape.FocusGravity;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by nicola on 08/08/16.
 */
public class IntroMonteOre extends Intro {
    public static final String SPINNER_TIPI = "SPINNER_TIPI";
    public static final String SELEZIONA_DATA = "SELEZIONA_DATA";
    public static final String ACTION_MONTE_ORE = "ACTION_MONTE_ORE";
    public static final String AGGIORNA = "AGGIORNA_MONTE_ORE";
    public static final String ORDINA = "ORDINA_MONTE_ORE";
    private static final String TAG = IntroMonteOre.class.getSimpleName();

    private Activity activity;
    private View spinnerTipiView;
    private View dataView;
    private View actionMenu;
    private View aggiornaView;
    private View ordinaView;

    public IntroMonteOre(Activity activity, View spinnerTipiView, View dataView, View actionView) {
        super(activity);
        this.activity = activity;
        this.spinnerTipiView = spinnerTipiView;
        this.dataView = dataView;
        this.actionMenu = actionView;
        //this.aggiornaView = aggiornaView;
        //this.ordinaView = ordinaView;

    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_MONTE_ORE)) {
            userLearnt(SharedPreferenceAdapter.INTRO_MONTE_ORE);
            showIntro(spinnerTipiView, SPINNER_TIPI, activity.getString(R.string.intro_ess_filtro_data), FocusGravity.LEFT);
        }
    }


    @Override
    public void onUserClicked(String s) {
        if (s.equals(SPINNER_TIPI)) {
            showIntro(dataView, SELEZIONA_DATA, activity.getString(R.string.intro_ess_filtro_tipo), FocusGravity.RIGHT, false);
        }
        if (s.equals(SELEZIONA_DATA)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(activity.getString(R.string.action_descrizione));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.aggiorna_intro)+" </b>"));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ordina_intro)+" </b>"));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.esporta_dati_visualizzati)+" </b>"));

            showIntro(actionMenu, ACTION_MONTE_ORE, ssb, FocusGravity.CENTER, false);
        }
    }
}
