package it.acea.android.socialwfm.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.koushikdutta.ion.Ion;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import org.fingerlinks.mobile.android.utils.Log;

import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.annotations.RealmModule;
import it.acea.android.socialwfm.BuildConfig;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.DeltaTokenUrl;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.GroupList;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.model.ObjectReference;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.GiustificativiCalendar;
import it.acea.android.socialwfm.app.model.ess.GiustificativiType;
import it.acea.android.socialwfm.app.model.odl.Customer;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.ess.EssClient;
import it.acea.android.socialwfm.service.UpdateSchedulerJob;
import it.acea.android.socialwfm.utils.HttpMethod;
import it.acea.android.socialwfm.utils.OAuthClientHelper;
import it.acea.android.socialwfm.utils.SignatureMethod;

//import android.support.multidex.MultiDex;

/**
 * Created by fabio on 15/10/15.
 */
public class SocialWFMApplication extends MultiDexApplication {

    public final static String TAG = SocialWFMApplication.class.getName();
    public static SocialWFMApplication instance;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fabric.with(this, new Crashlytics());
        instance = this;

        setupEnv();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                try {
                    User user = UserHelperFactory.getAuthenticatedUser(imageView.getContext());
                    if (uri.toString().indexOf("/api/v1/OData/Members(") != -1) {

                        new ImageFactory(imageView.getContext(), user.getId(),
                                ImageFactory.TYPE.MEMBER,
                                Utils.avatarPlaceholder(imageView.getContext(),
                                        user.getFirstName(),
                                        user.getLastName()))
                                .into(imageView);
                    } else {
                        OAuthClientHelper och = new OAuthClientHelper();
                        och.setHttpMethod(HttpMethod.GET);
                        och.setRequestUrl(new URL(uri.toString()));
                        och.setConsumerKey(SharedPreferenceAdapter.getValueForKey(getApplicationContext(),SharedPreferenceAdapter.OAUTH_CONSUMER_KEY));
                        och.setConsumerSecret(SharedPreferenceAdapter.getValueForKey(getApplicationContext(),SharedPreferenceAdapter.OAUTH_CONSUMER_SECRET));

                        och.setToken(user.getOautToken());
                        och.setTokenSecret(user.getOautTokenSecret());
                        och.setSignatureMethod(SignatureMethod.HMAC_SHA1);
                        String authorizationHeader = och.generateAuthorizationHeader();

                        Ion.with(imageView.getContext())
                                .load(uri.toString())
                                .addHeader("Authorization", authorizationHeader)
                                .addHeader("Accept", "application/json")
                                .intoImageView(imageView);
                    }
                } catch (Exception _ex) {
                    Log.e(TAG, _ex.getLocalizedMessage());
                    imageView.setImageResource(R.drawable.img_place_holder_odl);
                }
            }
        });



        JobManager.create(this, new SWFMJobCreator());
        Log.setShowLog(BuildConfig.DEV_MODE);

        // The RealmConfiguration is created using the builder pattern.
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name(getResources().getString(R.string.database_conf_name))
                .schemaVersion(getResources().getInteger(R.integer.database_conf_version))
                .setModules(new SocialWFMModule())
                .deleteRealmIfMigrationNeeded()
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        Log.e(TAG, "Migration avvenuta");
                        return getResources().getInteger(R.integer.database_conf_version);
                    }
                })
                .build();
        //Realm.removeDefaultConfiguration();
        Realm.setDefaultConfiguration(config);
        EssClient.setupEssIonClient(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    public static Context getContext() {
        return mContext;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    /**
     * Gestione puntamenti PREPRODUZIONE,PRODUZIONE
     * Se non è impostato nulla imposta produzione come default
     **/
    private void setupEnv(){
        boolean hasConfig = (SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.CURRENT_ENV)!=null);
        Log.d(TAG,"***************************************************** HA CONFIGURAZIONE: "+hasConfig);
        if(!hasConfig){
            SharedPreferenceAdapter.setEnv(this, SharedPreferenceAdapter.Environments.PRODUZIONE);
        }
    }

    /**
     * Job scheduler executor
     */
    private static class SWFMJobCreator implements JobCreator {

        @Override
        public Job create(String tag) {
            switch (tag) {
                case UpdateSchedulerJob.TAG_FULL_NAME:
                    return new UpdateSchedulerJob();
                default:
                    throw new RuntimeException("Cannot find job for tag " + tag);
            }
        }
    }

    ;

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            JobManager.instance().cancelAll();
        } catch (Exception _ex) {
            android.util.Log.e(TAG, "Impossibile eliminare il JOB");
        }
    }

    @RealmModule(classes = {
            User.class,
            Notification.class,
            Profile.class,
            Group.class,
            Odl.class,
            GroupList.class,
            Customer.class,
            Plant.class,
            DeltaTokenUrl.class,
            ObjectReference.class,
            Giustificativi.class,
            GiustificativiType.class,
            GiustificativiCalendar.class
    })
    class SocialWFMModule {
    }

}
