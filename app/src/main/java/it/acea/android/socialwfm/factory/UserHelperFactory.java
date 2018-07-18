package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.FutureCallback;

import org.fingerlinks.mobile.android.utils.Log;

import io.realm.Realm;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.DeltaTokenUrl;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.GroupList;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.model.ObjectReference;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.model.odl.Customer;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by fabio on 13/05/14.
 */
public class UserHelperFactory {

    public static boolean isBasicAuthenticationStore(Context context) {
        return TextUtils.isEmpty(SharedPreferenceAdapter.getBasicAutheticationToken(context));
    }

    /**
     * Check if user needed to obtain the access_token
     *
     * @param ctx
     * @return
     */
    public static boolean userIsLogged(Context ctx) {
        return (!TextUtils.isEmpty(SharedPreferenceAdapter.getUserOauthToken(ctx)));
    }

    /**
     * @param ctx
     */
    public static synchronized void logoutUserFromSystem(final Context ctx, final LogoutCallBack callBack) {

        //CALL ONLY FROM MAIN ACTIVITY
        new MaterialDialog.Builder(ctx)
                .title("Effettuare il logout?")
                .content("Continuando cancellerai tutti i dati dell'applicazione\nContinuare?")
                .positiveText("OK")
                .negativeText("Annulla")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(final MaterialDialog dialog, DialogAction which) {
                        if (which == DialogAction.POSITIVE) {

                            HttpClientRequest.removeJamToken(ctx, new FutureCallback<Boolean>() {
                                @Override
                                public void onCompleted(Exception e, Boolean result) {
                                    SharedPreferenceAdapter.clear(ctx);
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.clear(User.class);
                                    realm.clear(Notification.class);
                                    realm.clear(Profile.class);
                                    realm.clear(Group.class);
                                    realm.clear(Odl.class);
                                    realm.clear(GroupList.class);
                                    realm.clear(Customer.class);
                                    realm.clear(Plant.class);
                                    realm.clear(DeltaTokenUrl.class);
                                    realm.clear(ObjectReference.class);
                                    realm.commitTransaction();
                                    String returnValue = SAPMobilePlatformFactory.with(ctx).removeRegisterDevice();
                                    Log.e(TAG, "RETURN_VALUE [" + returnValue + "]");
                                    dialog.dismiss();
                                    callBack.onFinish();
                                }
                            });
                        }

                    }
                })
                .show();
    }

    public static void brasaOdl(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.clear(Odl.class);
        realm.clear(Customer.class);
        realm.clear(Plant.class);
        realm.commitTransaction();
    }

    /**
     * Return actual authenticated user
     *
     * @param ctx
     * @return
     */
    public static synchronized User getAuthenticatedUser(Context ctx) {
        Realm realm = Realm.getDefaultInstance();
        User user = realm.
                where(User.class).equalTo("oautTokenSecret", SharedPreferenceAdapter.
                getUserOauthToken(ctx)).findFirst();
        return user;
    }

    public static synchronized String getActualSWFMUsername(Context ctx) {
        return SharedPreferenceAdapter.getActualSWFMUsername(ctx).toUpperCase();
    }

    public static synchronized String getAuthenticatedUserId(Context ctx) {
        return getAuthenticatedUser(ctx).getId();
    }

    public interface LogoutCallBack {
        void onFinish();
    }

    private final static String TAG = UserHelperFactory.class.getName();
};