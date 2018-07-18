package it.acea.android.socialwfm.app;

import it.acea.android.socialwfm.R;

public class Constant {

    public static final Long DELAY = 1500L;
    public final static String HOME_CATEGORY = R.class.getPackage().getName() + "app.ui.HOME_CATEGORY";

    public final static String OAUTH_CONSUMER_BASE_URL = "oauth.consumer.base.url";
    public final static String OAUTH_CONSUMER_SECRET = "oauth.consumer.secret";
    public final static String OAUTH_CONSUMER_KEY = "oauth.consumer.key";

    public final static int WEBVIEW_INTENT_RESULT = 5001;

    /***
     * NAVIGATION TAG
     ***/
    public final static String TAG_HOME = "TAG_HOME";
    public final static String TAG_GROUP = "TAG_GROUP";
    public final static String TAG_MEMBERS = "TAG_MEMBERS";
    public final static String TAG_SETTINGS = "TAG_SETTINGS";
    public final static String TAG_MY_PROFILE = "TAG_MY_PROFILE";
    public final static String TAG_OPEN_WM = "TAG_OPEN_WM";
    public final static String TAG_ESS = "TAG_ESS";

    /***
     * SMP
     ***/
    public final static String SMP_BASE_URL = "smp.base.url";

    public final static String SUPPORT_BUNDLE = "SUPPORT_BUNDLE";
    public final static String SUPPORT_FRAGMENT = "SUPPORT_FRAGMENT";
    public final static String SUPPORT_FRAGMENT_TAG = "SUPPORT_FRAGMENT_TAG";

    public final static int STATIC_MAP_ZOOM_LEVEL = 17;

    /**
     * ESS
     */
    // Monteore
    public final static String ESS_CARTELLINO_INTERVAL_TYPE_ALL = "ALL";
    public final static String ESS_CARTELLINO_INTERVAL_TYPE_3MONTHS = "3MONTHS";
    public final static String ESS_CARTELLINO_INTERVAL_TYPE_6MONTHS = "6MONTHS";
    public final static String ESS_CARTELLINO_INTERVAL_TYPE_12MONTHS = "12MONTHS";


    public static String BUNDLE() {
        return org.fingerlinks.mobile.android.navigator.utils.Constant.BUNDLE;
    }

    public static String XML_BODY_TO_SEND = "<?xml version=\"1.0\" encoding=\"utf-8\"?>    \n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"    \n" +
            "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\">    \n" +
            "<content type=\"application/xml\">    \n" +
            "<m:properties>    \n" +
            "<d:DeviceType>Android</d:DeviceType>    \n" +
            "</m:properties>    \n" +
            "</content> </entry>";
};