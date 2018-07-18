package it.acea.android.socialwfm.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import it.acea.android.socialwfm.R;

/**
 * Created by fabio on 28/10/13.
 */
public class SharedPreferenceAdapter {


    // Puntamenti attuali dell'applicazione
    public final static String CURRENT_ENV = R.class.getPackage().getName() +"current.env";
    // SAP JAM
    public final static String OAUTH_JAM_LOGIN_URL = R.class.getPackage().getName() +"oauth.jam.login.url";
    public final static String OAUTH_CONSUMER_KEY = R.class.getPackage().getName() +"oauth.consumer.key";
    public final static String OAUTH_CONSUMER_SECRET = R.class.getPackage().getName() +"oauth.consumer.secret";
    // SMP
    public final static String SMP_ADMIN_BASE_URL = R.class.getPackage().getName() +"smp.admin.base.url";
    private final static String APP_ACCOUNT_PREFS_NAME = R.class.getPackage().getName() + ".prefs.setting";
    private final static String ACTUAL_USER_TOKEN = R.class.getPackage().getName() + ".user_token";
    private final static String ACTUAL_SWFM_USERNAME = R.class.getPackage().getName() + ".swfm.username";

    private final static String APP_CID_REGISTERED = R.class.getPackage().getName() + ".app.cid.registered";
    private final static String APP_CID_REGISTERED_COOKIE = R.class.getPackage().getName() + ".app.cid.registered.cookie";

    private final static String APP_CID_REGISTERED_ESS = R.class.getPackage().getName() + ".app.cid.registered.ess";
    private final static String FIRST_TIME_OAUTH_CHANGE_URL = R.class.getPackage().getName() + ".change.oauth.url";
    private final static String LAST_MEMBER_SELECTED_LIST = R.class.getPackage().getName() + ".last.member.selected.list";
    private final static String LAST_MEMBER_SELECTED_ITEM = R.class.getPackage().getName() + ".last.member.selected.item";

    private final static String BASIC_AUTHENTICATION_TOKEN = R.class.getPackage().getName() + ".basic_auth_token";

    private final static String BASIC_AUTHENTICATION_TOKEN_ESS = R.class.getPackage().getName() + ".basic_auth_token_ess";

    private final static String NUM_NOTIFICATION = R.class.getPackage().getName() + ".num_notification";

    private final static String TAG = SharedPreferenceAdapter.class.getName();

    // Intro views - ci dice se è stato già visualizzato l'help
    public final static String INTRO_HOME   = R.class.getPackage().getName() + "intro.home";
    public final static String INTRO_GRUPPI = R.class.getPackage().getName() + "intro.gruppi";
    public final static String INTRO_FEED   = R.class.getPackage().getName() + "intro.feed";
    public final static String INTRO_CONTATTI       = R.class.getPackage().getName() + "intro.contatti";
    public final static String INTRO_CONTATTI_DETTAGLIO       = R.class.getPackage().getName() + "intro.contatti.dettaglio";
    public final static String INTRO_GRUPPI_DETTAGLIO         = R.class.getPackage().getName() + "intro.gruppi.dettaglio";
    public final static String INTRO_MONTE_ORE      = R.class.getPackage().getName() + "intro.monteore";
    public final static String INTRO_CARTELLINO     = R.class.getPackage().getName() + "intro.cartellino";
    public final static String INTRO_GIUSTIFICATIVI_LISTA = R.class.getPackage().getName() + "intro.giustificativi.lista";
    public final static String INTRO_GIUSTIFICATIVI_CALENDARIO = R.class.getPackage().getName() + "intro.giustificativi.calendario";
    public final static String INTRO_TIMBRATURE     = R.class.getPackage().getName() + "intro.timbrature";
    public final static String INTRO_REPERIBILITA   = R.class.getPackage().getName() + "intro.reperibilita";
    public final static String INTRO_QUADRATURA     = R.class.getPackage().getName() + "intro.quadratura";
    public final static String INTRO_CUDCEDOLINO    = R.class.getPackage().getName() + "intro.cudcedolino";



    public enum Environments{
        PREPRODUZIONE("Preproduzione"),
        PRODUZIONE("Produzione");
        public String environment;
        Environments(String env){
            this.environment = env;
        }
        public static String[] items(){
            return new String[]{"PRODUZIONE","PREPRODUZIONE"
            };
        }
    }


    public static void setValueForKey(Context ctx, String key, String value){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getValueForKey(Context ctx, String key){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(key, null);
    }

    public static void setEnv(Context ctx, Environments env){
        Log.d(TAG,"******************************************** IMPOSTO AMBIENTE: "+env.environment);
        switch (env){
            case PREPRODUZIONE:{
                setValueForKey(ctx,CURRENT_ENV,ctx.getResources().getString(R.string.preproduzione));
                setValueForKey(ctx,OAUTH_JAM_LOGIN_URL,ctx.getResources().getString(R.string.preproduzione_oauth_jam_login_url));
                setValueForKey(ctx,OAUTH_CONSUMER_KEY,ctx.getResources().getString(R.string.preproduzione_oauth_consumer_key));
                setValueForKey(ctx,OAUTH_CONSUMER_SECRET,ctx.getResources().getString(R.string.preproduzione_oauth_consumer_secret));
                setValueForKey(ctx,SMP_ADMIN_BASE_URL,ctx.getResources().getString(R.string.preproduzione_smp_url));
                break;
            }
            case PRODUZIONE:{
                setValueForKey(ctx,CURRENT_ENV,ctx.getResources().getString(R.string.produzione));
                setValueForKey(ctx,OAUTH_JAM_LOGIN_URL,ctx.getResources().getString(R.string.produzione_oauth_jam_login_url));
                setValueForKey(ctx,OAUTH_CONSUMER_KEY,ctx.getResources().getString(R.string.produzione_oauth_consumer_key));
                setValueForKey(ctx,OAUTH_CONSUMER_SECRET,ctx.getResources().getString(R.string.produzione_oauth_consumer_secret));
                setValueForKey(ctx,SMP_ADMIN_BASE_URL,ctx.getResources().getString(R.string.produzione_smp_url));
                break;
            }
        }
    }

    public static boolean isPreproduzione(Context context) {
        return getValueForKey(context, CURRENT_ENV).equals(context.getResources().getString(R.string.preproduzione));
    }

    public static String printEnv(Context ctxt){
        return  "Current env: "+getValueForKey(ctxt,CURRENT_ENV)+"\n"+
                "OAUTH_JAM_LOGIN_URL: "+getValueForKey(ctxt,OAUTH_JAM_LOGIN_URL)+"\n"+
                "OAUTH_CONSUMER_KEY: "+getValueForKey(ctxt,OAUTH_CONSUMER_KEY)+"\n"+
                "OAUTH_CONSUMER_SECRET:"+getValueForKey(ctxt,OAUTH_CONSUMER_SECRET)+"\n"+
                "SMP_ADMIN_BASE_URL:"+getValueForKey(ctxt,SMP_ADMIN_BASE_URL)+"\n";
    }


    /**
     * @param ctx
     */
    public static void setAppCidCookie(Context ctx, String cookie) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(APP_CID_REGISTERED_COOKIE, cookie);
        edit.apply();
    }


    /**
     * @param ctx
     */
    public static void setAppCidRegistered(Context ctx, String cid) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(APP_CID_REGISTERED, cid);
        edit.apply();
    }

    /**
     * @param ctx
     * @return
     */
    public static String getAppCidRegistered(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        Log.d(TAG, "get cid from " + ctx.toString() + " : " + prefs.getString(APP_CID_REGISTERED, null));
        return prefs.getString(APP_CID_REGISTERED, null);
    }


    /**
     * @param ctx
     */
    public static void setFirstTimeOauth(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(FIRST_TIME_OAUTH_CHANGE_URL, false);
        edit.apply();
    }

    /**
     * return last selected position of member-list
     * @param ctx
     */
    public static int getLastVisitMemberList(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getInt(LAST_MEMBER_SELECTED_LIST, 0);
    }


    /**
     * @param ctx
     * @param token
     */
    public static void storeUserOauthToken(Context ctx, String token) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(ACTUAL_USER_TOKEN, token);
        edit.apply();
    }


    public static String getBasicAutheticationToken(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(BASIC_AUTHENTICATION_TOKEN, null);
    }

    public static void removeBasicAuthenticationToken(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(BASIC_AUTHENTICATION_TOKEN);
        edit.apply();
    }

    public static void removeAppcidRegistered(Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(APP_CID_REGISTERED);
        edit.apply();
    }

    public static void storeBasicAutheticationToken(Context ctx, String token) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(BASIC_AUTHENTICATION_TOKEN, token);
        edit.apply();
    }


    public static String getActualSWFMUsername(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(ACTUAL_SWFM_USERNAME, null);
    }

    public static void storeActualSWFMUsername(Context ctx, String username) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(ACTUAL_SWFM_USERNAME, username);
        edit.apply();
    }


    public static synchronized String getUserOauthToken(Context ctx) {

        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(ACTUAL_USER_TOKEN, "");
    }

    public static int getNumNotification(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        return prefs.getInt(NUM_NOTIFICATION, 0);
    }

    public static void setNumNotification(Context ctx, int num) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(NUM_NOTIFICATION, num);
        edit.apply();
    }

    public static void clear(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(APP_ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(ACTUAL_USER_TOKEN);
        edit.remove(BASIC_AUTHENTICATION_TOKEN);
        edit.remove(ACTUAL_SWFM_USERNAME);
        edit.remove(APP_CID_REGISTERED);
        edit.remove(APP_CID_REGISTERED_COOKIE);
        edit.remove(FIRST_TIME_OAUTH_CHANGE_URL);
        edit.remove(LAST_MEMBER_SELECTED_LIST);
        edit.remove(LAST_MEMBER_SELECTED_ITEM);

        edit.apply();
    }

};