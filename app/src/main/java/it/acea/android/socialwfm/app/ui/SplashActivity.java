package it.acea.android.socialwfm.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import org.fingerlinks.mobile.android.navigator.Navigator;

import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.factory.UserHelperFactory;

/**
 * Created by fabio on 15/10/15.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    private boolean isStart = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            goToNextHomePage();
        }
    }

    private void goToNextHomePage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (UserHelperFactory.isBasicAuthenticationStore(SplashActivity.this)) {
                    /**
                     * Schermata nuova
                     */
                    Navigator.with(SplashActivity.this)
                            .build()
                            .goTo(AuthenticatedWFMUser.class)
                            .animation()
                            .commit();
                    finish();
                } else {
                    User user = UserHelperFactory.getAuthenticatedUser(SplashActivity.this);
                    if (user == null) {
                        Navigator.with(SplashActivity.this)
                                .build()
                                .goTo(OauthActivity.class)
                                .animation()
                                .commit();
                        finish();
                    } else {
                        Navigator.with(SplashActivity.this)
                                .build()
                                .goTo(MainActivity.class)
                                .animation()
                                .commit();
                        finish();
                    }
                }
            }
        }, Constant.DELAY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (realm != null)
            realm.close();
        realm = null;
    }

    private Realm realm;

    private Realm getRealmInstance() {
        realm = (realm == null) ? Realm.getDefaultInstance() : realm;
        return realm;
    }

};