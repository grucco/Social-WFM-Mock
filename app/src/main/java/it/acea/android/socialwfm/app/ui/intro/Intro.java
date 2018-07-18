package it.acea.android.socialwfm.app.ui.intro;

import android.app.Activity;
import android.view.View;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by nicola on 08/08/16.
 */
public abstract class Intro implements MaterialIntroListener {
    private Activity activity;

    public Intro(Activity activity) {
        this.activity = activity;
    }


    protected void showIntro(View view, String id, String text, FocusGravity focusGravity){
        showIntro(view, id, text, focusGravity, false);
    }

    protected void showIntro(View view, String id, CharSequence text, FocusGravity focusGravity, boolean performClick) {
        if(view == null) return;
        new MaterialIntroView.Builder(activity)
                .enableDotAnimation(false)
                .setFocusGravity(focusGravity)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(1)
                .enableFadeAnimation(false)
                .performClick(performClick)
                .setInfoText(text)
                .setInfoTextSize(20)
                .enableIcon(false)
                .setTarget(view)
                .setListener(this)
                .setUsageId(id); //THIS SHOULD BE UNIQUE ID
                //* Disabilito la visualizzazione delle intro*/
                //.show();
    }

    protected boolean alreadyShown(String introKey){
        String showIntro = SharedPreferenceAdapter.getValueForKey(activity, introKey);
        return Boolean.parseBoolean(showIntro);
    }

    /**
     * Scrive nelle shared preferences che l'utente ha appreso l'help fornito
     * */
    protected void userLearnt(String introKey){
        SharedPreferenceAdapter.setValueForKey(activity,introKey,"true");
    }

    @Override
    public void onUserClicked(String s) {
        return;
    }

    public void start() {
        return;
        //new PreferencesManager(activity.getApplicationContext()).resetAll();
    }
}
