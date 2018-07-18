package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;

import it.acea.android.socialwfm.R;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 17/07/2017  *******
 ***************************/

public class SurveyActivityFragment extends Fragment {

    private WebView webView;
    private String url;
    MaterialDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concorsi, container, false);
        webView = (WebView) view.findViewById(R.id.webview_concorsi);
        url = getArguments().getString("surveyUrl");
        progressDialog = buildProgressDialog();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissProgress();
            }
        });
        showProgress();
        webView.loadUrl(url);
    }

    private void showProgress() {
        progressDialog.show();
    }

    private void dismissProgress() {
        progressDialog.dismiss();
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
}
