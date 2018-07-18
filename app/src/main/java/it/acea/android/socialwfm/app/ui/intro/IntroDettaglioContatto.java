package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;

import co.mobiwise.materialintro.shape.FocusGravity;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by a.simeoni on 29/08/2016.
 */
public class IntroDettaglioContatto extends Intro {

    public static final String CONTATTO_FOLLOW  = "CONTATTI_RICERCA";
    public static final String CONTATTO_FEED    = "CONTATTI_FEED";

    private Activity activity;
    private View contattoFollowView;
    private View contattoFeedView;

    public IntroDettaglioContatto(Activity activity, View contattoFollowView, View contattoFeedView) {
        super(activity);
        this.activity = activity;
        this.contattoFollowView = contattoFollowView;
        this.contattoFeedView = contattoFeedView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help della homepage è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_CONTATTI_DETTAGLIO)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.segui_un_contatto)+" </b>"));
            showIntro(this.contattoFollowView, CONTATTO_FOLLOW, ssb, FocusGravity.CENTER,false);
        }
    }

    @Override
    public void onUserClicked(String s) {
        if(s.equals(CONTATTO_FOLLOW)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.crea_un_post)+" </b>"));
            showIntro(this.contattoFeedView, CONTATTO_FEED, ssb, FocusGravity.CENTER,false);
        }
        if(s.equals(CONTATTO_FEED)){
            userLearnt(SharedPreferenceAdapter.INTRO_CONTATTI_DETTAGLIO);
        }
    }
}
