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
public class IntroContatti extends Intro {

    public static final String CONTATTI_LISTA   = "CONTATTI_LISTA";
    public static final String CONTATTI_RICERCA = "CONTATTI_RICERCA";

    private Activity activity;
    private View contattiListaView;
    private View contattiRicercaView;

    public IntroContatti(Activity activity, View contattiListaView, View contattiRicercaView) {
        super(activity);
        this.activity = activity;
        this.contattiListaView = contattiListaView;
        this.contattiRicercaView = contattiRicercaView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help della homepage è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_CONTATTI)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.lista_contatti)+" </b>"));
            ssb.append("\n\n");
            ssb.append(activity.getString(R.string.lista_contatti_descrizione));
            showIntro(contattiListaView, CONTATTI_LISTA, ssb, FocusGravity.CENTER,false);
        }
    }

    @Override
    public void onUserClicked(String s) {
        if(s.equals(CONTATTI_LISTA)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ricerca_contatti_auto)+" </b>"));
            showIntro(contattiRicercaView, CONTATTI_RICERCA, ssb, FocusGravity.CENTER, false);
        }
        if(s.equals(CONTATTI_RICERCA)){
            userLearnt(SharedPreferenceAdapter.INTRO_CONTATTI);
        }
    }
}
