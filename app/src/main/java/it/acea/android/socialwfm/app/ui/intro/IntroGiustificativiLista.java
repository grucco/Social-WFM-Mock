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
public class IntroGiustificativiLista extends Intro{
    public static final String SELECT_DATA = "GIUSTIFICATIVI_LISTA_SELECT_DATA";
    public static final String NEW_GIUSTIFICATIVO = "GIUSTIFICATIVI_LISTA_NEW_GIUSTIFICATIVO";
    public static final String ACTION_VIEW = "GIUSTIFICATIVI_LISTA_ACTION_VIEW";

    private Activity activity;
    private View selectDataView;
    private View newGiustificativo;
    private View actionMenu;

    public IntroGiustificativiLista(Activity activity, View actionMenu,  View selectDataView, View newGiustificativo) {
        super(activity);
        this.actionMenu = actionMenu;
        this.activity = activity;
        this.selectDataView = selectDataView;
        this.newGiustificativo = newGiustificativo;
    }


    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_GIUSTIFICATIVI_LISTA)){
            userLearnt(SharedPreferenceAdapter.INTRO_GIUSTIFICATIVI_LISTA);
            showIntro(this.newGiustificativo, NEW_GIUSTIFICATIVO, activity.getString(R.string.crea_giustificativo), FocusGravity.CENTER, false);
        }
    }


    @Override
    public void onUserClicked(String s) {
       /* if (s.equals(SELECT_DATA)) {
            showIntro(this.newGiustificativo, NEW_GIUSTIFICATIVO, activity.getString(R.string.crea_giustificativo), FocusGravity.CENTER, false);
        }
        if (s.equals(NEW_GIUSTIFICATIVO)) {
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
            showIntro(actionMenu, ACTION_VIEW, ssb, FocusGravity.CENTER, false);
        }*/
    }
}
