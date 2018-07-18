package it.acea.android.socialwfm.app.ui.fragment;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import javax.net.ssl.SSLException;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.ResponseManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConcorsiActivityFragment extends Fragment {

    private String TAG = ConcorsiActivityFragment.class.getCanonicalName();

    private String CONCORSI_ROOT;
    private final String SAP_JAM_URL = "jam12.sapjam.com";
    private WebView webView;

    MaterialDialog progressDialog;


    public ConcorsiActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concorsi, container, false);
        CONCORSI_ROOT = getResources().getString(R.string.gadget_concorsi_url_root);
        webView = (WebView) view.findViewById(R.id.webview_concorsi);

        this.progressDialog = buildProgressDialog();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d(TAG, "********************************"+error.toString()+"\n"+"CODE: "+error.getPrimaryError());
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.contains(SAP_JAM_URL))
                    closeActivity();
                else super.onLoadResource(view, url);
            }
        });

        CookieManager.getInstance().setAcceptCookie(true);

        fetchConcorsi();
    }

    private void closeActivity() {
        if(getActivity()!=null)
            getActivity().finish();
    }

    private String buildPostData(){
        User user = UserHelperFactory.getAuthenticatedUser(getActivity());
        return  "nome=" + user.getFirstName()+
                "&cognome="+user.getLastName()+
                "&email="+user.getEmail()+
                "&jamid="+user.getId();

    }

    private void fetchConcorsi(){
        this.progressDialog.show();
        HttpClientRequest.getAvailableConcorsi(getContext(), new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                progressDialog.dismiss();
                try {
                    // Genera eccezione se qualcosa non va
                    new ResponseManager(e, result);
                    JsonArray jsonConcorsi = result.getResult().get("results").getAsJsonArray();
                    if (jsonConcorsi.size() > 0) {
                        JsonObject concorso = jsonConcorsi.get(0).getAsJsonObject();
                        String linkConcorso = String.format("%s%s", CONCORSI_ROOT,concorso.get("url").getAsString());
                        String postData = buildPostData();
                        webView.postUrl(linkConcorso, postData.getBytes());

                    } else {
                        showNoConcorsi();
                    }

                }
                catch (SSLException ex){
                    showSSLError();
                }
                catch (Exception ex) {
                    showError();
                }
            }
        });
    }

    private MaterialDialog buildProgressDialog() {
        return new MaterialDialog.Builder(getContext())
                .title(R.string.attendere)
                .content(R.string.download_dati_in_corso)
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .build();
    }


    private MaterialDialog buildMsgDialog(String title, String msg) {
        return new MaterialDialog.Builder(getContext())
                .title(title)
                .content(msg)
                .positiveText(R.string.esci)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        closeActivity();
                    }
                })
                .cancelable(false)
                .show();
    }

    private void showError() {
        buildMsgDialog(getString(R.string.errore),
                getString(R.string.si_e_verificato_un_errore)).show();
    }

    private void showSSLError() {
        buildMsgDialog(getString(R.string.errore),
                getString(R.string.errore_ssl)).show();
    }

    private void showNoConcorsi() {
        buildMsgDialog(getString(R.string.concorsi_disponibili),
                getString(R.string.non_ci_sono_concorsi_Attivi)).show();
    }

}
