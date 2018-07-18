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
public class IntroGiustificativiCalendario extends Intro{
    public static final String SELECT_DATA = "GIUSTIFICATIVI_CALENDARIO_SELECT_DATA";
    public static final String NAVIGA = "GIUSTIFICATIVI_CALENDARIO_NEW_GIUSTIFICATIVO";
    public static final String ACTION_VIEW = "GIUSTIFICATIVI_CALENDARIO_ACTION_VIEW";

    private Activity activity;
    private View selectDataView;
    private View navigaMesi;
    private View actionMenu;

    public IntroGiustificativiCalendario(Activity activity, View selectDataView, View navigaMesi, View actionMenu) {
        super(activity);
        this.activity = activity;
        this.selectDataView = selectDataView;
        this.navigaMesi = navigaMesi;
        this.actionMenu = actionMenu;
    }


    @Override
    public void start(){
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_GIUSTIFICATIVI_CALENDARIO)) {
            userLearnt(SharedPreferenceAdapter.INTRO_GIUSTIFICATIVI_CALENDARIO);
            showIntro(selectDataView, SELECT_DATA, activity.getString(R.string.seleziona_data), FocusGravity.RIGHT);
        }*/
    }

    @Override
    public void onUserClicked(String s) {
        if(s.equals(SELECT_DATA)){
            showIntro(this.navigaMesi, NAVIGA, activity.getString(R.string.naviga_mesi), FocusGravity.CENTER, false);
        }
        if(s.equals(NAVIGA)){
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
        }
    }
}