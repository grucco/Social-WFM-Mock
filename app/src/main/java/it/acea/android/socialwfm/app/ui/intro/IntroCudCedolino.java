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
public class IntroCudCedolino extends Intro {

    public static final String SELECT_TYPE = "CUDCED_SELECT_TYPE";
    public static final String SELECT_DATE = "CUDCED_SELECT_DATE";
    public static final String ACTION_VIEW = "CUDCED_ACTION_VIEW";

    private Activity activity;
    private View selectTypeView;
    private View selectDateView;
    private View actionMenu;

    public IntroCudCedolino(Activity activity, View selectTypeView, View selectDateView, View actionMenu) {
        super(activity);
        this.activity = activity;
        this.selectTypeView = selectTypeView;
        this.selectDateView = selectDateView;
        this.actionMenu = actionMenu;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help è stato già mostrato, altrimenti salto
        /*if(!alreadyShown(SharedPreferenceAdapter.INTRO_CUDCEDOLINO)) {
            userLearnt(SharedPreferenceAdapter.INTRO_CUDCEDOLINO);
            showIntro(selectTypeView, SELECT_TYPE, activity.getString(R.string.seleziona_tipo_intro), FocusGravity.LEFT);
        }*/
    }
    @Override
    public void onUserClicked(String s) {
        if (s.equals(SELECT_TYPE)) {
            showIntro(this.selectDateView, SELECT_DATE, activity.getString(R.string.seleziona_data), FocusGravity.RIGHT, false);
        }
        if (s.equals(SELECT_DATE)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(activity.getString(R.string.action_descrizione));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.aggiorna_intro)+" </b>"));
            ssb.append(activity.getString(R.string.aggiorna_intro_descrizione));
            showIntro(actionMenu, ACTION_VIEW, ssb, FocusGravity.CENTER, false);
        }
    }
}
