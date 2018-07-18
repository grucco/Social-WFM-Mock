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
public class IntroHome extends Intro{
    public static final String HOME_LISTA_ORDINI     = "HOME_LISTA_ORDINI";
    public static final String HOME_DETTAGLIO_ORDINE = "HOME_DETTAGLIO_ORDINE";
    public static final String HOME_CLOSE_DETAIL     = "HOME_CLOSE_DETAIL";
    public static final String HOME_FULL_SCREEN_MAP  = "HOME_FULL_SCREEN_MAP";
    public static final String HOME_FULL_SCREEN_MAP_CLOSE  = "HOME_FULL_SCREEN_MAP_CLOSE";

    private Activity activity;
    private View homeListaOrdiniView;
    private View homeDettaglioOrdineView;
    private View closeBoxView;
    private View fullScreenMapView;

    public IntroHome(Activity activity, View homeListaOrdiniView,
                     View homeDettaglioOrdineView, View closeBoxView, View fullScreenMapView) {
        super(activity);
        this.activity = activity;
        this.homeListaOrdiniView = homeListaOrdiniView;
        this.homeDettaglioOrdineView = homeDettaglioOrdineView;
        this.closeBoxView = closeBoxView;
        this.fullScreenMapView = fullScreenMapView;
    }

    @Override
    public void start(){
        super.start();
        //Controllo se l'help della homepage è stato già mostrato, altrimenti salto
        if(!alreadyShown(SharedPreferenceAdapter.INTRO_HOME)){
            userLearnt(SharedPreferenceAdapter.INTRO_HOME);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.lista_odl_intro)+" </b>"));
            ssb.append("\n\n");
            ssb.append(Html.fromHtml(activity.getString(R.string.lista_degli_odl)));
            showIntro(homeListaOrdiniView, HOME_LISTA_ORDINI, ssb, FocusGravity.CENTER,false);
         }
    }

    @Override
    public void onUserClicked(String s) {
        if (s.equals(HOME_LISTA_ORDINI)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.schermata_di_dettaglio)+" </b>"));
            showIntro(homeDettaglioOrdineView, HOME_DETTAGLIO_ORDINE, ssb, FocusGravity.CENTER, false);
        }
        if(s.equals(HOME_DETTAGLIO_ORDINE)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.chiude_dettaglio_ordine)+" </b>"));
            showIntro(closeBoxView, HOME_CLOSE_DETAIL, ssb, FocusGravity.CENTER, true);
        }
        if(s.equals(HOME_CLOSE_DETAIL)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.mappa_a_tutto_schermo)+" </b>"));
            showIntro(fullScreenMapView, HOME_FULL_SCREEN_MAP, ssb, FocusGravity.CENTER, true);
        }
        if(s.equals(HOME_FULL_SCREEN_MAP)){
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(Html.fromHtml("<b>"+activity.getString(R.string.ripristina_la_dimensione_della_mappa)+" </b>"));
            showIntro(fullScreenMapView, HOME_FULL_SCREEN_MAP_CLOSE, ssb, FocusGravity.CENTER, true);
        }
    }
}
