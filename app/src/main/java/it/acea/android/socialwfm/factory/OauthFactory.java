package it.acea.android.socialwfm.factory;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;

import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.utils.HttpMethod;
import it.acea.android.socialwfm.utils.OAuthClientHelper;
import it.acea.android.socialwfm.utils.SignatureMethod;

/**
 * Created by Raphael on 29/10/2015.
 */
public class OauthFactory {

    private static final String TAG = OauthFactory.class.getName();

    private Context context;
    private String url;

    public OauthFactory(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public String getHeader(HttpMethod httpMethod) {
        try {
            User user = UserHelperFactory.getAuthenticatedUser(context);
            OAuthClientHelper och = new OAuthClientHelper();
            och.setHttpMethod(httpMethod);
            och.setRequestUrl(new URL(url));
            och.setConsumerKey(SharedPreferenceAdapter.getValueForKey(context,SharedPreferenceAdapter.OAUTH_CONSUMER_KEY));
            och.setConsumerSecret(SharedPreferenceAdapter.getValueForKey(context,SharedPreferenceAdapter.OAUTH_CONSUMER_SECRET));
            och.setToken(user.getOautToken());
            och.setTokenSecret(user.getOautTokenSecret());
            och.setSignatureMethod(SignatureMethod.HMAC_SHA1);
            return och.generateAuthorizationHeader();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getUrl() {
        return url;
    }
}
