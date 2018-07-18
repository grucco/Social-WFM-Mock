package it.acea.android.socialwfm.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.cookie.CookieMiddleware;

import org.fingerlinks.mobile.android.utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import it.acea.android.socialwfm.BuildConfig;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.factory.OauthFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.response.content.ContentItem;
import it.acea.android.socialwfm.http.response.feed.Comment;
import it.acea.android.socialwfm.http.response.feed.Feed;
import it.acea.android.socialwfm.http.response.user.UserSuggestion;
import it.acea.android.socialwfm.utils.HttpMethod;

/**
 * Created by fabio on 22/10/15.
 */
public class HttpClientRequest {

    private static final String TAG = HttpClientRequest.class.getName();
    private final static String EXPAND_FIELD = "Impianto,Cliente/DispAgg";


    public static void trustAllHttpsClient(Ion ion) throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext _context = SSLContext.getInstance("TLS");

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        TrustManager[] truster = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        _context.init(null, truster, new SecureRandom());

        ion.getHttpClient().getSSLSocketMiddleware().setSpdyEnabled(false);
        ion.getHttpClient().getSSLSocketMiddleware().setHostnameVerifier(hostnameVerifier);
        ion.getHttpClient().getSSLSocketMiddleware().setSSLContext(_context);
        ion.getHttpClient().getSSLSocketMiddleware().setTrustManagers(truster);

    }


    /* Ho duplicato il metodo per non compromettere il codice esistente
    * Ho verificato che decommentando Ion ion = Ion.getInstance(context, tag); nel metodo precedente
    * le registrazioni non vanno a buon fine, forse perchè dovrebbero essere effettuate tutte con la stessa instanza
    * in modo da conversare i cookie
    * Di fatto il metodo precedente non permette la creazione di diverse instanze di ion pronte per SSL
    */
    private static Ion getIonHttpsInstance(Context context, String tag) throws NoSuchAlgorithmException, KeyManagementException, CertificateException, KeyStoreException, IOException {
        Ion ion = Ion.getDefault(context);
        trustAllHttpsClient(ion);
        return ion;
    }

    public static Ion getIonHttpsInstanceByTag(Context context, String tag) throws KeyManagementException, NoSuchAlgorithmException {
        Ion ion = Ion.getInstance(context, tag);
        trustAllHttpsClient(ion);
        return ion;
    }

    private static String getAuthorizationHeader(Context ctx) {
        if (BuildConfig.DEV_MODE) {
            return ctx.getResources().getString(R.string.basic_authorization);
        }
        return SharedPreferenceAdapter.getBasicAutheticationToken(ctx);
    }

    /**
     * @param context
     * @param base_url
     * @param xml
     */
    public static void executeRegisterDevice(final Context context, final String base_url,
                                             String xml, final FutureCallback<String> callbackResponse) {
        try {
            final Ion ion = getIonHttpsInstance(context, "executeRegisterDevice");
            ion.build(context).load(
                    HttpMethod.POST.toString(),
                    base_url + "/odata/applications/latest/swfm/Connections")
                    .setTimeout(15000)
                    .setLogging(TAG, android.util.Log.VERBOSE)
                    .addHeader("Content-type", "application/xml")
                    .addHeader("Authorization", getAuthorizationHeader(context))
                    //.basicAuthentication(username, password)
                    .setStringBody(xml)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

                            if (result == null) {
                                callbackResponse.onCompleted(new Exception("Errore generico"), null);
                                return;
                            }

                            if (result.getException() != null) {
                                callbackResponse.onCompleted(result.getException(), null);
                                return;
                            }

                            List<String> sessionDataCookie = result.getHeaders().getHeaders().getAll("Set-Cookie");
                            buildCidAndCookieString(context, sessionDataCookie);

                            try {
                                URI uri = URI.create(base_url + "/odata/applications/latest/swfm");
                                Multimap _cookies = result.getHeaders().getHeaders().getMultiMap();
                                Map<String, List<String>> cookies = ion.getCookieMiddleware().getCookieManager().get(uri, _cookies);
                                CookieMiddleware.addCookies(cookies, result.getHeaders().getHeaders());

                                callbackResponse.onCompleted(e, result.getResult());
                            } catch (Exception _ex) {
                                callbackResponse.onCompleted(_ex, null);
                            }
                        }
                    });
        } catch (Exception _ex) {
            callbackResponse.onCompleted(_ex, null);
        }
    }

    /**
     * @param context
     * @param base_url
     * @return
     * @throws Exception
     */
    public static String removeRegisterDevice(Context context, String base_url) throws Exception {
        Ion ion = getIonHttpsInstance(context, "removeRegisterDevice");
        Response<String> _responseAuth = ion.build(context).load(
                HttpMethod.DELETE.toString(),
                base_url + "/odata/applications/latest/swfm/Connections('" + SharedPreferenceAdapter.getAppCidRegistered(context) + "')")
                .setLogging(TAG, android.util.Log.VERBOSE)
                .setTimeout(15000)
                .addHeader("Authorization", getAuthorizationHeader(context))
                /*.basicAuthentication(username, password)*/
                .asString()
                .withResponse()
                .get();

        return _responseAuth.getResult();
    }

    private static void buildCidAndCookieString(Context mContext, List<String> sessionDataCookie) {
        String cookies = "";
        for (String key : sessionDataCookie) {
            if (key.contains("X-SMP-APPCID")) {
                String[] values = key.split(";");
                String xSmpCid = values[0];
                String[] valuesCid = xSmpCid.split("=");
                SharedPreferenceAdapter.setAppCidRegistered(mContext, valuesCid[1]);
            }
            cookies += key + ";";
        }
        String _cookies = cookies.substring(0, cookies.length() - 1);
        SharedPreferenceAdapter.setAppCidCookie(mContext, _cookies);
    }

    //private static java.net.CookieManager manager = null;
    //private static URI uri = null;

    public static void initCookieManager(Context ctx, Response<String> mReturnValue) throws Exception {
        java.net.CookieManager manager = middleware.getCookieManager(); //new java.net.CookieManager(null, null);

        String http_protocol = (ctx.getResources().getString(R.string.smpAdmin_base_port).equalsIgnoreCase("8080")) ? "http" : "https";
        String url = http_protocol + "://" +
                SharedPreferenceAdapter.getValueForKey(ctx, SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) +
                ctx.getResources().getString(R.string.smpAdmin_base_port) + "/odata/applications/latest/swfm";
        URI uri = URI.create(url);

        Headers newHeaders = new Headers();
        newHeaders.addAll(mReturnValue.getHeaders().getHeaders().getMultiMap());
        Map<String, List<String>> cookies = manager.get(uri, newHeaders.getMultiMap());
        manager.put(uri, cookies);

        CookieMiddleware.addCookies(cookies, newHeaders);
        SharedPreferenceAdapter.setAppCidCookie(ctx, mReturnValue.getResult());
    }

    private static CookieMiddleware middleware = null;

    /**
     * @param mContext
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static void executeInitSapAuthorization(Context mContext, String username, String password, FutureCallback<Response<String>> callback) {

        String http_protocol = (mContext.getResources().getString(R.string.smpAdmin_base_port).equalsIgnoreCase("8080")) ? "http" : "https";
        String url = http_protocol + "://" +
                SharedPreferenceAdapter.getValueForKey(mContext, SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) +
                mContext.getResources().getString(R.string.smpAdmin_base_port);

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        try {
            Ion ion = getIonHttpsInstance(mContext, "executeInitSapAuthorization");

            SharedPreferences preferences = ion.getContext().getSharedPreferences(ion.getName() + "-cookies", Context.MODE_PRIVATE);
            preferences.edit().clear().apply();

            middleware = ion.getCookieMiddleware();
            middleware.reinit();

            ion.build(mContext)
                    .load(HttpMethod.GET.toString(), url + "/odata/applications/latest/swfm")
                    .setLogging(TAG, android.util.Log.VERBOSE)
                    .basicAuthentication(
                            username,
                            password)
                    .asString()
                    .withResponse()
                    .setCallback(callback);
        } catch (Exception _ex) {
            callback.onCompleted(_ex, null);
        }
    }


    /**
     * Register device, retrieve sap-bae-url and Retrieve ODL data for authenticated user
     * http://help.sap.com/saphelp_smp304sdk/helpdata/en/7c/0a4f017006101484238a3b00c4d5d0/frameset.htm
     *
     * @param context
     * @param callback
     */
    public static void executeRequestGetOdl(Context context, String base_url, String query, FutureCallback<Response<String>> callback) {
        String url = base_url + "/swfm/WorkOrderSet?" + query + "&$format=json&$expand=" +
                EXPAND_FIELD;
        String _url = url.replaceAll(" ", "%20");
        Log.e(TAG, "WorkOrderSet [" + _url + "]");
        Ion ion = null;
        try {
            ion = getIonHttpsInstance(context, "executeRequestGetOdl");
        } catch (Exception _ex) {
            callback.onCompleted(_ex, null);
        }

        try {
            ion.getCookieMiddleware().clear();
            ion.getCookieMiddleware().reinit();
            ion.getCookieMiddleware().getCookieStore().removeAll();
            ion.getCookieMiddleware().getCookieManager().getCookieStore().removeAll();
        } catch (Exception _ex) {
            Log.e(TAG, "Controlled exception [" + _ex.getLocalizedMessage() + "]");
        }

        ion.build(context).load(HttpMethod.GET.toString(), _url)
                .setLogging(TAG, android.util.Log.VERBOSE)
                .followRedirect(true)
                .setTimeout(15000)
                .addHeader("Authorization", getAuthorizationHeader(context))
                .addHeader("X-SMP-APPCID", SharedPreferenceAdapter.getAppCidRegistered(context))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    /**
     * @param context
     * @param url
     * @param callback
     */
    public static void executeRequestGetOdlDeltaToken(Context context, String url, FutureCallback<Response<String>> callback) {
        String _url = url
                .replaceAll(" ", "%20")
                .replaceAll("http", "https");
        Log.e(TAG, "WorkOrderSet [" + _url + "]");

        Ion ion = null;
        try {
            ion = getIonHttpsInstance(context, "executeRequestGetOdl");
        } catch (Exception _ex) {
            callback.onCompleted(_ex, null);
        }

        try {
            ion.getCookieMiddleware().clear();
            ion.getCookieMiddleware().reinit();
            ion.getCookieMiddleware().getCookieStore().removeAll();
            ion.getCookieMiddleware().getCookieManager().getCookieStore().removeAll();
        } catch (Exception _ex) {
            Log.e(TAG, "Controlled exception [" + _ex.getLocalizedMessage() + "]");
        }

        ion.build(context).load(HttpMethod.GET.toString(), _url)
                .setLogging(TAG, android.util.Log.VERBOSE)
                .followRedirect(true)
                .setTimeout(15000)
                .addHeader("Authorization", getAuthorizationHeader(context))
                //.basicAuthentication(username, password)
                .addHeader("X-SMP-APPCID", SharedPreferenceAdapter.getAppCidRegistered(context))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    /**
     * @param context
     * @param futureCallback
     */
    public static void executeRequestGetNotification(Context context, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) +
                        "/api/v1/OData/Notifications?$format=json&$expand=Sender,Group,ObjectReference");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }


    public static void executeRequestGetContacts(Context context, @NonNull String idMember, FutureCallback<Response<JsonObject>> callback) {
        String urlRequest = String.format("/api/v1/OData/Members('%s')/Following?$format=json", idMember);
        String url = context.getString(R.string.oauth_consumer_base_url) + urlRequest;
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

    public static void executeRequestGetProfile(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')?$format=json&$expand=MemberProfile");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetNotificationSender(Context context, FutureCallback<String> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notifications('HYuRxwTr7gDdsJLqMbnXTw')/Sender?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asString()
                .setCallback(futureCallback);
    }

    public static void setFollowingUser(Context context, String idMy, String idMember, FutureCallback<Response<JsonObject>> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + idMy + "')/$links/Following?$format=json");
        JsonObject json = new JsonObject();
        json.addProperty("uri", "Members('" + idMember + "')");
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .setJsonObjectBody(json)
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void setUnFollowingUser(Context context, String idMy, String idMember, FutureCallback<Response<JsonObject>> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + idMy + "')/$links/Following('" + idMember + "')");
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void setNotificationRead(Context context, String id, FutureCallback<Response<String>> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notification_MarkAsRead?Id='" + id + "'");
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asString()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void setNotificationDismiss(Context context, String id, FutureCallback<Response<String>> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notification_Dismiss?Id='" + id + "'");
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asString()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void setLikeComment(Context context, final Comment feed, final FutureCallback<String> futureCallback) {
        //prepare json body with toggle like value
        JsonObject json = new JsonObject();
        json.addProperty("Id", feed.getId());
        json.addProperty("CreatedAt", feed.getCreatedAt());
        json.addProperty("LastModifiedAt", feed.getLastModifiedAt());
        json.addProperty("Text", feed.getText());
        json.addProperty("Liked", !feed.isLiked());
        json.addProperty("LikesCount", feed.getLikesCount());
        json.addProperty("CanDelete", feed.isCanDelete());

        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Comments('" + feed.getId() + "')";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("PATCH", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.PATCH))
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        //set up custom response in callback
                        if (e != null) {
                            //generic error msg "error"
                            futureCallback.onCompleted(null, "error");
                            return;
                        }
                        if (result.getHeaders().code() == 204) {
                            //all ok turn back new like status msg "true|false"
                            futureCallback.onCompleted(null, String.valueOf(!feed.isLiked()));
                        } else {
                            //generic error msg "error"
                            futureCallback.onCompleted(null, "error");
                        }
                    }
                });
    }

    public static void setLikePost(Context context, final Feed feed, final FutureCallback<String> futureCallback) {
        //prepare json body with toggle like value
        JsonObject json = new JsonObject();
        json.addProperty("Id", feed.getId());
        json.addProperty("Title", feed.getTitle());
        json.addProperty("CreatedAt", feed.getCreatedAt());
        json.addProperty("Bookmarked", feed.isBookmarked());
        json.addProperty("Liked", !feed.isLiked());
        json.addProperty("RepliesCount", feed.getRepliesCount());
        json.addProperty("CanDelete", feed.isCanDelete());
        json.addProperty("CanLike", feed.isCanLike());
        json.addProperty("CanReply", feed.isCanReply());
        json.addProperty("ConsolidatedCount", 0);
        json.addProperty("WebURL", feed.getWebURL());
        json.addProperty("Read", feed.isRead());

        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/FeedEntries('" + feed.getId() + "')";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("PATCH", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.PATCH))
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        //set up custom response in callback
                        //set up custom response in callback
                        if (e != null) {
                            //generic error msg "error"
                            futureCallback.onCompleted(null, "error");
                            return;
                        }
                        if (result.getHeaders().code() == 204) {
                            //all ok turn back new like status msg "true|false"
                            futureCallback.onCompleted(null, String.valueOf(!feed.isLiked()));
                        } else {
                            //generic error msg "error"
                            futureCallback.onCompleted(null, "error");
                        }
                    }
                });
    }

    public static void executeRequestGetFeedLikers(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/FeedEntries('" + id + "')/Likers?$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetCommentLikers(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Comments('" + id + "')/Likers?$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestPost(Context context, String url, String text, List<UserSuggestion> userSuggestions, FutureCallback<JsonObject> futureCallback) {
        int insert = 0;
        Pattern pattern;
        Matcher matcher;
        JsonArray mentions = new JsonArray();

        for (UserSuggestion suggestion : userSuggestions) {
            String fullName = suggestion.getFullName();
            pattern = Pattern.compile(fullName, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(text);
            while (matcher.find()) {
                String mentionedName = matcher.group();
                if (mentionedName.equals(fullName)) {
                    text = text.replace(fullName, "@@m{" + insert + "}");
                    JsonObject mentionContainer = new JsonObject();
                    JsonObject mention = new JsonObject();
                    mention.addProperty("uri", "Members('" + suggestion.getId() + "')");
                    mentionContainer.add("__metadata", mention);
                    mentions.add(mentionContainer);
                    insert++;
                    break;
                }
            }
        }

        JsonObject json = new JsonObject();
        json.addProperty("Text", text);
        json.add("AtMentions", mentions);
        Log.d(TAG, json.toString());
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeDeleteFeed(Context context, String id, FutureCallback<Response<String>> callback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/FeedEntries('" + id + "')");
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void executeDeleteComment(Context context, String id, FutureCallback<Response<String>> callback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Comments('" + id + "')");
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void executeDeleteGroup(Context context, String id, FutureCallback<Response<String>> callback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')");
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static void executeRequestGetFollowers(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/Followers?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetFollowing(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/Following?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetManagers(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/Managers?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetAssistants(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/Assistants?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetDirectReports(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/DirectReports?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetMemberProfile(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/MemberProfile?$format=json&$expand=PhoneNumbers");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetGenericFeed(Context context, String url, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetFeedComments(Context context, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/FeedEntries('" + id + "')/Replies?$format=json&$expand=Creator,ThumbnailImage,AtMentions");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetGroups(Context context, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) +
                "/api/v1/OData/Groups");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetMyGroups(Context context, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups?$format=json");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetGroup(Context context, String id, Object tag, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')?$format=json&$expand=Memberships");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .group(tag)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetGroupMembers(Context context, Object group, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')/Memberships?$format=json&$expand='Member'");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .group(group)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetRootContent(Context context, Object group, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')/ContentListItems?$format=json&$expand='ContentItem/Creator,Folder'");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .group(group)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetFolderContent(Context context, Object group, String id, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Folders(Id='" + id + "',FolderType='Folder')/ContentListItems?$format=json&$expand='ContentItem,Folder'");
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .group(group)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestDeleteContentItem(Context context, Object group, ContentItem contentItem, FutureCallback<Response<JsonObject>> futureCallback) {
        String url = String.format("/api/v1/OData/ContentItems(Id='%s', ContentItemType='%s')", contentItem.getId(), contentItem.getContentListItemType());
        OauthFactory factory = new OauthFactory(context,
                context.getResources().getString(R.string.oauth_consumer_base_url) + url);
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .group(group)
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void executeRequestGetContent(Context context, String Id, String ContentItemType, File destination, FutureCallback<File> futureCallback) {
        String file = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/ContentItems(Id='" + Id + "',ContentItemType='" + ContentItemType + "')/$value";
        OauthFactory factory = new OauthFactory(context, file);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .write(destination)
                .setCallback(futureCallback);
    }

    public static void executeRequestJoinGroup(Context context, String id, FutureCallback<Response<JsonObject>> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Group_Join?Id='" + id + "'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void executeUserGroupsMembership(Context context, String groupId, FutureCallback<Response<JsonObject>> futureCallback) {
        String user_id = UserHelperFactory.getAuthenticatedUserId(context);
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/GroupMemberships(GroupId='" + groupId + "',MemberId='" + user_id + "')?$format=json";///Member?$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                //.addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void executeRequestUnJoinGroup(Context context, String id, FutureCallback<Response<JsonObject>> futureCallback) {
        String user_id = UserHelperFactory.getAuthenticatedUserId(context);
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/GroupMemberships(GroupId='" + id + "',MemberId='" + user_id + "')";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("DELETE", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.DELETE))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }


    public static void executeRequestSearch(Context context, String query, FutureCallback<JsonObject> futureCallback) {
        String urlTest = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/SearchSummary?$format=json&Category='member_statuses,comments,people,groups'&Query='" + query + "'";
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Search?$format=json&Category='member_statuses,comments,people,groups'&Query='" + query + "'" + "&$expand='ObjectReference,Group,Creator'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    /**
     * Effettua una ricerca sul social per tutti i feeds taggati con l'id di un impianto
     *
     * @param plantId Identificativo dell'impianto
     */
    public static void executeRequestSearchPlantFeeds(Context context, String plantId, Object ionGroup, FutureCallback<Response<JsonObject>> futureCallback) {
        String tag = plantId.toLowerCase().replace(".", "_");
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Search?$format=json&Category='member_statuses,comments'&Query='" + tag + "'&Tag='" + tag + "'" +
                "&$expand=ObjectReference";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .group(ionGroup)
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void executeRequestNewGroup(Context context, JsonObject body, FutureCallback<JsonObject> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .setJsonObjectBody(body)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void executeRequestNotificationDismiss(Context context, String id, FutureCallback<Response<JsonObject>> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notification_Dismiss?Id='" + id + "'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void executeRequestNotificationAccept(Context context, String id, FutureCallback<Response<JsonObject>> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notification_Accept?Id='" + id + "'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .setLogging(TAG, android.util.Log.VERBOSE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; Charset=UTF-8")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }


    public static void userAutoComplete(Context context, String query, String groupId, FutureCallback<JsonObject> futureCallback) {
        query = query.replace("'", "''");
        query = query.replaceAll(" ", "+");
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members_Autocomplete?$format=json&Query='" + query + "'";
        if (groupId != null) url = url + "&GroupId='" + groupId + "'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void gruppiAutoComplete(Context context, String query, FutureCallback<JsonObject> futureCallback) {
        query = query.replace("'", "''");
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups_Autocomplete?$format=json&Query='" + query + "'";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }


    public static void inviteUserGroup(Context context, String id, List<String> emailList, final FutureCallback<Boolean> futureCallback) {
        String email = "";
        for (int i = 0; i < emailList.size(); i++) {
            if (emailList.size() == i) {
                email = email + emailList.get(i);
            } else {
                email = email + emailList.get(i) + ",";
            }
        }
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Group_Invite?Id='" + id + "'&Email='" + email + "'&$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        boolean success = e == null && (result != null && result.getHeaders().code() == 204);
                        futureCallback.onCompleted(e, success);
                    }
                });
    }

    public static void inviteGroupByMembersId(Context context, String id, List<String> membersId, final FutureCallback<Boolean> futureCallback) {
        String mids = TextUtils.join(",", membersId);
        String baseUrl = context.getResources().getString(R.string.oauth_consumer_base_url);
        String url = String.format("%s/api/v1/OData/Group_InviteByMemberIds?Id='%s'&MemberIds='%s'&$format=json", baseUrl, id, mids);
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        boolean success = e == null && (result != null && result.getHeaders().code() == 204);
                        futureCallback.onCompleted(e, success);
                    }
                });
    }

    public static void loadProfileImage(Context context, File file, String id, final FutureCallback<Boolean> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Members('" + id + "')/ProfilePhoto";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Content-Type", "image/png")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .setFileBody(file)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            futureCallback.onCompleted(e, false);
                            return;
                        }
                        if (result.getHeaders().code() == 201) {
                            futureCallback.onCompleted(e, true);
                        } else {
                            futureCallback.onCompleted(e, false);
                        }

                    }
                });


    }


    public static void loadGroupImage(Context context, File file, String id, final FutureCallback<Boolean> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')/ProfilePhoto";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Content-Type", "image/png")
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .setFileBody(file)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            futureCallback.onCompleted(e, false);
                            return;
                        }
                        if (result.getHeaders().code() == 201) {
                            futureCallback.onCompleted(e, true);
                        } else {
                            futureCallback.onCompleted(e, false);
                        }

                    }
                });


    }

    public static void getNumNotification(Context context, FutureCallback<JsonObject> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Notifications_UnreadCount?$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void genericGet(Context context, String url, FutureCallback<JsonObject> futureCallback) {
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("GET", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.GET))
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void removeJamToken(Context context, final FutureCallback<Boolean> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/oauth/revoke_token";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            futureCallback.onCompleted(e, false);
                            return;
                        }
                        if (result.getHeaders().code() == 200) {
                            futureCallback.onCompleted(e, true);
                        } else {
                            futureCallback.onCompleted(e, false);
                        }

                    }
                });
    }

    public static void createFolderInGroup(Context context, Object group, String groupId, String folderName, final FutureCallback<Boolean> futureCallback) {
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty("Name", folderName);
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + groupId + "')/Folders";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(requestParams)
                .group(group)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        futureCallback.onCompleted(e, result != null && result.getHeaders().code() == 201);
                    }
                });
    }

    public static void createFolderInParentFolder(Context context, Object group, String parentFolderId, String folderName, final FutureCallback<Boolean> futureCallback) {
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty("Name", folderName);
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Folders(Id='" + parentFolderId + "',FolderType='Folder')/Folders";
        OauthFactory factory = new OauthFactory(context, url);
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(requestParams)
                .group(group)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        futureCallback.onCompleted(e, result != null && result.getHeaders().code() == 201);
                    }
                });
    }

    public static void uploadFileToGroup(Context context, Object group, File file, String groupId, final FutureCallback<Boolean> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups(Id='" + groupId + "')/ContentItems?$format=json";
        OauthFactory factory = new OauthFactory(context, url);
        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .addHeader("Content-Type", mimeType)
                .addHeader("Slug", file.getName())
                .setFileBody(file)
                .group(group)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        Log.d(TAG, String.valueOf(result));
                        futureCallback.onCompleted(e, result != null && result.getHeaders().code() == 201);
                    }
                });
    }

    public static void uploadFileToPublicFolder(Context context, Object group, File file, String folderId, final FutureCallback<Boolean> futureCallback) {
        String url = context.getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Folders(Id='" + folderId + "',FolderType='Folder')/ContentItems";
        OauthFactory factory = new OauthFactory(context, url);
        String mimeType = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        Ion.with(context)
                .load("POST", factory.getUrl())
                .addHeader("Authorization", factory.getHeader(HttpMethod.POST))
                .addHeader("Content-Type", mimeType)
                .addHeader("Slug", file.getName())
                .setFileBody(file)
                .group(group)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        Log.d(TAG, String.valueOf(result));
                        futureCallback.onCompleted(e, result != null && result.getHeaders().code() == 201);
                    }
                });
    }

    /**
     * Servizi Gadget
     **/
    public static void getAvailableSurveys(Context context, String email, FutureCallback<Response<JsonObject>> callback) {

        String rootUrl = context.getResources().getString(R.string.gadget_survey_url_root);
        String tailUrl = context.getResources().getString(R.string.gadget_survey_url_service);
        String serviceUrl = String.format("%s/%s", rootUrl, tailUrl);

        Ion.with(context)
                .load("POST", serviceUrl)
                .setBodyParameter("email", email)
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

    public static void getAvailableConcorsi(Context context, FutureCallback<Response<JsonObject>> callback) {

        String rootUrl = context.getResources().getString(R.string.gadget_concorsi_url_root);
        String tailUrl = context.getResources().getString(R.string.gadget_concorsi_url_service);
        String serviceUrl = String.format("%s/%s", rootUrl, tailUrl);

        Ion.with(context)
                .load("GET", serviceUrl)
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

}