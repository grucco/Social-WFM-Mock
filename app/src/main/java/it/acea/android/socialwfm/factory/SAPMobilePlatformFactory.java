package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.events.EventPasswordExpired;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.model.odl.Plant;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.ResponseManager;
import it.acea.android.socialwfm.http.response.RetrieveOdlResponse;

/**
 * Created by fabio on 10/11/15.
 */
public class SAPMobilePlatformFactory {

    private Context mContext;
    private static SAPMobilePlatformFactory instance;
    private boolean mTest = false;
    private boolean mDropbox = false;


    private SAPMobilePlatformFactory(Context ctx) {
        this.mContext = ctx;
    }

    public static SAPMobilePlatformFactory with(Context ctx) {
        instance = new SAPMobilePlatformFactory(ctx);
        return instance;
    }

    public SAPMobilePlatformFactory test(boolean isTest) {
        mTest = isTest;
        return instance;
    }

    public SAPMobilePlatformFactory sourceDropbox(boolean isDropbox) {
        mDropbox = isDropbox;
        return instance;
    }


    private void syncCookieManager() throws Exception {
        if (android.webkit.CookieManager.getInstance().hasCookies()) {
            if (mTest)
                Log.e(TAG, "REMOVE ALL COOKIE --------------------------------------------------------------->");
            android.webkit.CookieManager.getInstance().removeAllCookie();
        }
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
    }

    boolean stop = true;
    private Multimap _cookies = null;

    public synchronized SAPMobilePlatformFactory withAuthorization() {
        if (!mDropbox) {
            try {

                if (!TextUtils.isEmpty(SharedPreferenceAdapter.getAppCidRegistered(mContext))) {
                    android.webkit.CookieManager.getInstance().removeAllCookie();
                    return instance;
                }

            } catch (Exception _ex) {
                Toast.makeText(mContext, _ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return instance;
    }

    public List<Odl> getActualOdlList() {
        Realm realm = Realm.getDefaultInstance();
        List<Odl> odlList = realm.where(Odl.class)
                .findAllSorted("Datetime", true);


//        odlList.get(1).setPlant(odlList.get(0).getPlant());
//        odlList.get(2).setPlant(odlList.get(0).getPlant());
//        odlList.get(4).setPlant(odlList.get(0).getPlant());
//        odlList.get(4).setCustomer(odlList.get(0).getCustomer());



        return odlList;
    }

    public void retrieveOdl(final CallBack callBack) {

        List<Odl> odlList = getActualOdlList();
        if (!CodeUtils.checkInternetConnection(mContext)) {
            callBack.onSuccess(odlList);
            return;
        }


            retrieveOdl(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {

                    ResponseManager.logCrashliticsIfNecessary(mContext, result);
/*
                    if (result == null) {
                        callBack.onFailure("Problemi di connessione dati!");
                        return;
                    }

                    int resultCode = result.getHeaders().code();

                    //Password scaduta
                    if(resultCode == 401){
                            callBack.onFailure("Problemi di connessione dati!");
                            EventBus.getDefault().post(new EventPasswordExpired());
                        return;
                    }

                    if (resultCode == 404) {
                        callBack.onFailure("CID_ERRATO");
                        return;
                    }


                    if (result.getException() != null) {
                        Log.e(TAG, result.getException().getLocalizedMessage());
                        return;
                    }

                    JsonObject o;
                    try {
                        JsonParser parser = new JsonParser();
                        o = parser.parse(result.getResult()).getAsJsonObject();

                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        RetrieveOdlResponse response = gson.fromJson(o.getAsJsonObject("d"), RetrieveOdlResponse.class);

                        if (response.getError() != null) {
                            String errorMessage = Utils.cleanNullableValue(response.getError().code) + "\n" +
                                    Utils.cleanNullableValue(response.getError().message.value);
                            callBack.onFailure(errorMessage);
                            return;
                        }
*/
                        /**
                         * Aggiorno il database. Lo sovrascrivo ogni volta
                         */

                        /*ODLProcessFactory
                                    .with(mContext)
                                    .odlBeans(response.results)
                                    .process();*/

                        List<Odl> odlList = getActualOdlList();

                        callBack.onSuccess(odlList);

                    /*} catch (Exception _ex) {
                        callBack.onFailure();
                    }*/
                }
            });
    }

    /**
     * @return
     */
    public String removeRegisterDevice() {
        String http_protocol = (mContext.getResources().getString(R.string.smpAdmin_base_port).equalsIgnoreCase("8080")) ? "http" : "https";
        String url = http_protocol + "://" +
                SharedPreferenceAdapter.getValueForKey(mContext,SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) +
                mContext.getResources().getString(R.string.smpAdmin_base_port);


        String returnValue = null;
        try {
            returnValue = HttpClientRequest.removeRegisterDevice(mContext, url);
            Log.e(TAG, "returnValue [" + returnValue + "]");
        } catch (Exception _ex) {
            Log.e(TAG, _ex.getLocalizedMessage());
        }
        return returnValue;
    }

    private void retrieveOdl(final FutureCallback<Response<String>> callback) {

        if (TextUtils.isEmpty(SharedPreferenceAdapter.getAppCidRegistered(mContext))) {

            String http_protocol = (mContext.getResources().getString(R.string.smpAdmin_base_port).equalsIgnoreCase("8080")) ? "http" : "https";
            String url = http_protocol + "://" +
                    SharedPreferenceAdapter.getValueForKey(mContext,SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) +
                    mContext.getResources().getString(R.string.smpAdmin_base_port);

            HttpClientRequest.executeRegisterDevice(
                    mContext, url, Constant.XML_BODY_TO_SEND,
                    new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Log.e(TAG, "RESULT REGISTRATION [" + result + "]");
                            executeOdlRequest(callback);
                        }
                    });
        } else {
            executeOdlRequest(callback);
        }
    }

    private void executeOdlRequest(FutureCallback<Response<String>> callback) {

        String http_protocol = (mContext.getResources().getString(R.string.smpAdmin_base_port).equalsIgnoreCase("8080")) ? "http" : "https";
        String url = http_protocol + "://" +
                SharedPreferenceAdapter.getValueForKey(mContext,SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) +
                mContext.getResources().getString(R.string.smpAdmin_base_port);

        String filterQuery = "$filter=User eq '" + getActualUser() + "'";
        HttpClientRequest.executeRequestGetOdl(mContext, url, filterQuery, callback);

    }

    private String getActualUser() {
        if (mTest) {
            return mContext.getResources().getString(R.string.sap_user_authentication);
        }
        return UserHelperFactory.getActualSWFMUsername(mContext);
    }


    public interface CallBack {
        void onSuccess(List<Odl> odlList);
        void onFailure();
        void onFailure(String message);
    }


    private final static String TAG = SAPMobilePlatformFactory.class.getName();
};