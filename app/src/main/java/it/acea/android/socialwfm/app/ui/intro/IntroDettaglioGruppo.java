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
public class IntroDettaglioGruppo extends Intro{

    public static final String GRUPPO_FOLLOW  = "GRUPPO_FOLLOW";
    public static final String GRUPPO_FEED    = "GRUPPO_FEED";

    private Activity activity;
    private View gruppoFollowView;
    private View gruppoFeedView;

    public IntroDettaglioGruppo(Activity activity, View gruppoFollowView, View gruppoFeedView) {
        super(activity);
        this.activity = activity;
        this.gruppoFollowView = gruppoFollowView;
        this.gruppoFeedView = gruppoFeedView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help della homepage è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_GRUPPI_DETTAGLIO)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.segui_un_gruppo)+" </b>"));
            showIntro(this.gruppoFollowView, GRUPPO_FOLLOW, ssb, FocusGravity.CENTER,false);
        }
    }

    @Override
    public void onUserClicked(String s) {
        if(s.equals(GRUPPO_FOLLOW)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.crea_un_post)+" </b>"));
            showIntro(this.gruppoFeedView, GRUPPO_FEED, ssb, FocusGravity.CENTER,false);
        }
        if(s.equals(GRUPPO_FEED)){
            userLearnt(SharedPreferenceAdapter.INTRO_GRUPPI_DETTAGLIO);
        }
    }
}
