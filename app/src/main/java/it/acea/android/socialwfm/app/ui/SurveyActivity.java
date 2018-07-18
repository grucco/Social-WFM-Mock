package it.acea.android.socialwfm.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.dialog.GiustificativoDetailDialog;
import it.acea.android.socialwfm.app.ui.fragment.SurveyActivityFragment;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.ResponseManager;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 17/07/2017  *******
 ***************************/

public class SurveyActivity extends AppCompatActivity {

    private static String TAG = SurveyActivity.class.getCanonicalName();

    MaterialDialog progressDialog;
    MaterialDialog surveyItems;
    JsonArray jsonSurveys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = buildProgressDialog();

        fetchSurveys();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private MaterialDialog buildProgressDialog() {
        return new MaterialDialog.Builder(this)
                .title(R.string.attendere)
                .content(R.string.download_dati_in_corso)
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .build();
    }

    private MaterialDialog buildMsgDialog(String title, String msg) {
        return new MaterialDialog.Builder(this)
                .title(title)
                .content(msg)
                .positiveText(R.string.esci)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .cancelable(false)
                .show();
    }


    private MaterialDialog buildSurveyItemsDialog(List<String> items) {
        return new MaterialDialog.Builder(this)
                .title(R.string.survey_disponibili)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                        String url = jsonSurveys.get(which).getAsJsonObject().get("link").getAsString();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        SurveyActivityFragment fragment = new SurveyActivityFragment();
                        Bundle args = new Bundle();
                        args.putString("surveyUrl", url);

                        fragment.setArguments(args);
                        fragmentTransaction.add(R.id.fragment_container, fragment);
                        fragmentTransaction.commit();
                    }
                })
                .show();
    }


    private void fetchSurveys() {
        /**
         *
         * {
         "details": "",
         "status": "ok",
         "surveys": [
         {
         "link": " https://0fd85f79.ngrok.io/limesurvey/index.php?r=survey/index/sid/528524/token/M1nyhTMYrcnMh8C/newtest/Y",
         "name": "Indagine di prova UAT"
         } ...
         ]
         }
         **/
        showProgress();
        User user = UserHelperFactory.getAuthenticatedUser(this);
        String email = user.getEmail();
        HttpClientRequest.getAvailableSurveys(this, email, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                dismissProgress();
                try {
                    // Genera eccezione se qualcosa non va
                    new ResponseManager(e, result);
                    jsonSurveys = result.getResult().get("surveys").getAsJsonArray();
                    if (jsonSurveys.size() > 0) {
                        buildSurveyItemsDialog(filter(jsonSurveys)).show();
                    } else {
                        showNoSurveys();
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


    private List<String> filter(JsonArray items) {
        List<String> result = new ArrayList<>();
        for (JsonElement obj : items) {
            String surveyName = obj.getAsJsonObject().get("name").getAsString();
            result.add(surveyName);
        }
        return result;
    }

    private void showError() {
        buildMsgDialog(getString(R.string.errore),
                getString(R.string.si_e_verificato_un_errore)).show();
    }

    private void showSSLError() {
        buildMsgDialog(getString(R.string.errore),
                getString(R.string.errore_ssl)).show();
    }

    private void showNoSurveys() {
        buildMsgDialog(getString(R.string.survey_disponibili),
                getString(R.string.non_hai_survey_assegnate)).show();
    }

    private void showProgress() {
        progressDialog.show();
    }

    private void dismissProgress() {
        progressDialog.dismiss();
    }
}
