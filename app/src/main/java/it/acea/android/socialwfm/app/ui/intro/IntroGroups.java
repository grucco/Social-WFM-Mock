package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;

import co.mobiwise.materialintro.shape.FocusGravity;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by a.simeoni on 26/08/2016.
 */
public class IntroGroups extends Intro {

    public static final String GRUPPI_LISTA    = "GRUPPI_LISTA";
    public static final String GRUPPI_RICERCA  = "GRUPPI_RICERCA";
    public static final String GRUPPI_CREA     = "GRUPPI_CREA";

    private Activity activity;
    private View gruppiListaView;
    private View gruppiRicercaView;
    private View gruppiNuovoView;

    public IntroGroups(Activity activity, View gruppiListaView, View gruppiRicercaView, View gruppiNuovoView) {
        super(activity);
        this.activity = activity;
        this.gruppiListaView = gruppiListaView;
        this.gruppiRicercaView = gruppiRicercaView;
        this.gruppiNuovoView = gruppiNuovoView;
    }

    @Override
    public void start() {
        super.start();
        //Controllo se l'help della homepage è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_GRUPPI)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.lista_gruppi)+" </b>"));
            showIntro(gruppiListaView, GRUPPI_LISTA, ssb, FocusGravity.CENTER,false);
         }
    }

    @Override
    public void onUserClicked(String s) {
        if (s.equals(GRUPPI_LISTA)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ricerca_gruppi_auto)+" </b>"));
            showIntro(gruppiRicercaView, GRUPPI_RICERCA, ssb, FocusGravity.CENTER, false);
        }
        if (s.equals(GRUPPI_RICERCA)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.crea_un_gruppo)+" </b>"));
            showIntro(gruppiNuovoView, GRUPPI_CREA, ssb, FocusGravity.CENTER, false);
        }
        if (s.equals(GRUPPI_CREA)) {
           userLearnt(SharedPreferenceAdapter.INTRO_GRUPPI);
        }
    }
}
