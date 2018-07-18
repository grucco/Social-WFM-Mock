package it.acea.android.socialwfm.app.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Pair;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.X509TrustManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.http.user.bean.UserBean;
import it.acea.android.socialwfm.utils.HttpMethod;
import it.acea.android.socialwfm.utils.OAuthClientHelper;
import it.acea.android.socialwfm.utils.OAuthUtils;
import it.acea.android.socialwfm.utils.SignatureMethod;

/**
 * Created by fabio on 15/10/15
 * Oauth test:
 * user: a.simeoni@reply.it
 * pwd: a@reply1
 */
public class OauthActivity extends Activity {

    @Bind(R.id.oauth_webview)
    WebView oauth_webview;

    @Bind(R.id.oauth_webview_progress)
    ProgressWheel oauth_webview_progress;

    @Bind(R.id.oauth_rr)
    View oauth_rr;

    String requestToken, requestTokenSecret;
    private String hackUrl;
    private final static String TAG = OauthActivity.class.getName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hackUrl = SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_JAM_LOGIN_URL);
        setContentView(R.layout.activity_oauth);
        ButterKnife.bind(this);


        if (!CodeUtils.checkInternetConnection(this)) {
            new MaterialDialog.Builder(this)
                    .content(R.string.error_message_network)
                    .positiveText(R.string.ok)
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
        }

        CookieManager.getInstance().removeAllCookie();


        realm = getRealmInstance();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private boolean isStart = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            try {
                buildOauthClient();
            } catch (Exception _ex) {
                Log.e(TAG, _ex);
            }
        }
    }

    private void buildOauthClient() throws Exception {

        // ExternalObjectKeys = new ArrayList<String>();
        Log.e(TAG, "***************************************************************");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        Date date = new Date();

        Log.e(TAG, "SAP Jam OAuth1a flows run: " + dateFormat.format(date));
        Pair<String, String> requestTokenPair = postOAuth1aRequestToken();
        requestToken = requestTokenPair.first;
        requestTokenSecret = requestTokenPair.second;

        //authorizeRequestToken(false);
        authorizeRequestToken(true);
    }

    //http://stackoverflow.com/questions/2376471/how-do-i-get-the-web-page-contents-from-a-webview
    private void authorizeRequestToken(boolean forceRedirect) throws Exception {

        String urlString =
                getResources().getString(R.string.oauth_consumer_base_url) +
                        "/oauth/authorize?oauth_token=" + requestToken;

        Log.e(TAG, "***************************************************************");
        Log.e(TAG, "Paste the following link into your browser [" + urlString + "]");
        Log.e(TAG, "Authorize the token, then enter the verification token here");

        oauth_webview.getSettings().setJavaScriptEnabled(true);
        oauth_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        oauth_webview.getSettings().setLoadsImagesAutomatically(true);
        /* Register a new JavaScript interface called HTMLOUT */
        oauth_webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        oauth_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, "OAUTH:onPageStarted [" + url + "]");
                oauth_rr.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG, "OAUTH: onPageFinished [" + url + "]");

                if (url.endsWith("authorize")) {
                    /* This call inject JavaScript into the page which just finished loading. */
                    oauth_webview.
                            loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                } else if (url.contains("/auth/status")) {
                    try {
                        authorizeRequestToken(true);
                    } catch (Exception _ex) {

                        oauth_rr.setVisibility(View.GONE);
                    }
                } else if (url.contains("/login?")) {
                    oauth_rr.setVisibility(View.GONE);

                } else {

                    oauth_rr.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e(TAG, "onReceivedHttpError ---->");
                oauth_rr.setVisibility(View.GONE);
                //TODO handling error
            }
        });
        //TODO Per la gestione in test dopo la prima inizializzazione dell'app cambiare la url di riferimento
        if (!forceRedirect) {
            oauth_webview.loadUrl(hackUrl); //Only first time
        } else
            oauth_webview.loadUrl(urlString);
    }


    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            Log.e(TAG, "INNERHTML [" + html + "]");
            //process the html as needed by the app
            Document doc = Jsoup.parse(html);
            String body = doc.body().html();
            Log.e(TAG, "INNER-body [" + body + "]");
            String verifyString = getResources().getString(R.string.find_verify_code);

            if (body.indexOf(verifyString) == -1) {
                verifyString = "verification code";
            }

            if (body.indexOf(verifyString) != -1) {
                Log.e(TAG, "TROVATO VERIFICATION CODE ------------------------------------------------>");
                Element _element = doc.getElementById("jam-public-content");
                int start = _element.html().indexOf(verifyString) + verifyString.length() + 1;
                //Verification Code
                String verificationCode = _element.html().substring(start).replace(".", "").trim();
                //Call post
                Log.d(TAG, "verificationCode [" + verificationCode + "]");
                try {
                    SharedPreferenceAdapter.setFirstTimeOauth(OauthActivity.this);
                    Pair<String, String> accessTokenPair = postOAuth1aAccessToken(verificationCode);
                    if (accessTokenPair != null) {
                        getCurrentUser(accessTokenPair.first, accessTokenPair.second, verificationCode);
                    } else {
                        /**
                         * Retry to supress error
                         */
                        buildOauthClient();
                    }
                } catch (Exception _ex) {
                    Log.e(TAG, _ex);
                    //TODO Gestire errore
                    denidedAuth();
                }
            } else {
                denidedAuth();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (oauth_webview.canGoBack()) {
            oauth_webview.goBack();
            return;
        }
        super.onBackPressed();
    }


    /**
     * Creates the OAuth1.0 request token
     * POST /oauth/request_token
     */
    private Pair<String, String> postOAuth1aRequestToken() throws Exception {

        Log.d(TAG, "***************************************************************");
        String urlString = getResources().getString(R.string.oauth_consumer_base_url) + "/oauth/request_token";
        Log.d(TAG, "POST " + urlString);

        OAuthClientHelper och = new OAuthClientHelper();
        och.setHttpMethod(HttpMethod.POST);
        och.setRequestUrl(new URL(urlString));
        och.setConsumerKey(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_KEY));
        och.setConsumerSecret(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_SECRET));
        //must set either a callback URL or indicate that the callback is out of band
        och.setOutOfBandCallback();
        och.setSignatureMethod(SignatureMethod.HMAC_SHA1);

        StringBuilder result = new StringBuilder();

        HttpURLConnection connection = OAuthUtils.createConnection(och.getRequestUrl(), new DefaultTrustManager());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String authorizationHeader = och.generateAuthorizationHeader();
        connection.setRequestProperty("Authorization", authorizationHeader);
        Log.d(TAG, "Authorization: " + authorizationHeader);


        int responseCode = connection.getResponseCode();
        Log.d(TAG, "HTTP response code: " + responseCode);
        InputStream is;
        if (connection.getResponseCode() >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();

        String resultString = result.toString();
        Log.d(TAG, "Response body: " + resultString);

        String[] tokenAndSecret = resultString.split("&");
        String requestToken = tokenAndSecret[0].split("=")[1];
        String requestTokenSecret = tokenAndSecret[1].split("=")[1];
        Log.d(TAG, "requestToken: " + requestToken);
        Log.d(TAG, "requestTokenSecret: " + requestTokenSecret);
        return new Pair<String, String>(requestToken, requestTokenSecret);
    }

    /**
     * Creates the OAuth1.0 access token from a request token and verifier
     * POST /oauth/access_token
     */
    private Pair<String, String> postOAuth1aAccessToken(String verifier) throws Exception {

        Log.d(TAG, "\n***************************************************************");
        String urlString = getResources().getString(R.string.oauth_consumer_base_url) + "/oauth/access_token";
        Log.d(TAG, "POST " + urlString);

        OAuthClientHelper och = new OAuthClientHelper();
        och.setHttpMethod(HttpMethod.POST);
        och.setRequestUrl(new URL(urlString));
        och.setConsumerKey(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_KEY));
        och.setConsumerSecret(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_SECRET));
        och.setToken(requestToken);
        och.setTokenSecret(requestTokenSecret);
        och.setVerifier(verifier);
        och.setOutOfBandCallback();
        och.setSignatureMethod(SignatureMethod.HMAC_SHA1);

        StringBuilder result = new StringBuilder();

        HttpURLConnection connection = OAuthUtils.createConnection(och.getRequestUrl(), new DefaultTrustManager());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String authorizationHeader = och.generateAuthorizationHeader();
        connection.setRequestProperty("Authorization", authorizationHeader);
        Log.d(TAG, "Authorization: " + authorizationHeader);


        int responseCode = connection.getResponseCode();
        Log.d(TAG, "HTTP response code: " + responseCode);
        InputStream is;
        if (connection.getResponseCode() >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();

            String resultString = result.toString();
            Log.d(TAG, "Response body: " + resultString);

            String[] tokenAndSecret = resultString.split("&");
            String accessToken = tokenAndSecret[0].split("=")[1];
            String accessTokenSecret = tokenAndSecret[1].split("=")[1];
            Log.d(TAG, "accessToken: " + accessToken);
            Log.d(TAG, "accessTokenSecret: " + accessTokenSecret);

            return new Pair<String, String>(accessToken, accessTokenSecret);
        }
        return null;
    }

    private void getCurrentUser(String... tokens) throws Exception {

        Log.e(TAG, "***************************************************************");
        String urlString = getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Self";

        OAuthClientHelper och = new OAuthClientHelper();
        och.setHttpMethod(HttpMethod.GET);
        och.setRequestUrl(new URL(urlString));
        och.setConsumerKey(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_KEY));
        och.setConsumerSecret(SharedPreferenceAdapter.getValueForKey(this,SharedPreferenceAdapter.OAUTH_CONSUMER_SECRET));
        och.setToken(tokens[0]);
        och.setTokenSecret(tokens[1]);
        och.setSignatureMethod(SignatureMethod.HMAC_SHA1);

        StringBuilder result = new StringBuilder();

        HttpURLConnection connection = OAuthUtils.createConnection(och.getRequestUrl(), new DefaultTrustManager());
        connection.setRequestMethod("GET");

        String authorizationHeader = och.generateAuthorizationHeader();
        connection.setRequestProperty("Authorization", authorizationHeader);
        Log.d(TAG, "Authorization: " + authorizationHeader);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "HTTP response code: " + responseCode);
        InputStream is;
        if (connection.getResponseCode() >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();

        String resultString = result.toString();
        Log.e(TAG, "Response body [" + resultString + "]");
        SharedPreferenceAdapter.storeUserOauthToken(OauthActivity.this, tokens[1]);

        Gson gson = new GsonBuilder().create();
        UserBean userBean = gson.fromJson(resultString, UserBean.class);

        User user = new User();

        //Oauth system
        user.setOautToken(tokens[0]);
        user.setOautTokenSecret(tokens[1]);
        user.setVerificationToken(tokens[2]);

        user.setEmail(userBean.getD().getResults().getEmail());
        user.setFirstName(userBean.getD().getResults().getFirstName());
        user.setLastName(userBean.getD().getResults().getLastName());
        user.setFullName(userBean.getD().getResults().getFullName());
        user.setNickname(userBean.getD().getResults().getNickname());
        user.setId(userBean.getD().getResults().getId());
        user.setTitle(userBean.getD().getResults().getTitle());
        user.setProfilePhoto(userBean.getD().getResults().getProfilePhoto().getDeferred().getUri());
        user.setFollowing(userBean.getD().getResults().isIsFollowing());

        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oauth_rr.setVisibility(View.GONE);

                //TODO redirect to homepage
                Navigator.with(OauthActivity.this)
                        .build()
                        .goTo(MainActivity.class)
                        .animation()
                        .commit();

                finish();
            }
        });
    }

    public static class DefaultTrustManager implements X509TrustManager {

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

    private Realm realm;

    private Realm getRealmInstance() {
        realm = (realm == null) ? Realm.getDefaultInstance() : realm;
        return realm;
    }

    private void denidedAuth() {
        new MaterialDialog.Builder(OauthActivity.this)
                .title(R.string.denied_auth_t)
                .content(R.string.denied_auth_c)
                .positiveText(R.string.ok)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

            if (realm != null)
                realm.close();
        } catch (Exception _ex) {
        }
        realm = null;
    }


};