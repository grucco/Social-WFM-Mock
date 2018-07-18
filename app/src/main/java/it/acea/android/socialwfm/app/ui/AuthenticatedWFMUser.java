package it.acea.android.socialwfm.app.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by fabio on 19/01/16.
 */
public class AuthenticatedWFMUser extends Activity {

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.password)
    EditText password;

    @Bind(R.id.label_error_message)
    TextView label_error_message;

    @Bind(R.id.button_login)
    Button button_login;

    @Bind(R.id.view)
    CardView cardView;

    @Bind(R.id.ambienteTest)
    TextView ambienteTest;

    List<View> checkList;
    MaterialDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        ButterKnife.bind(this);

        checkList = new ArrayList<>();
        checkList.add(email);
        checkList.add(password);
        email.addTextChangedListener(new CustomTextWatcher(button_login, checkList));
        password.addTextChangedListener(new CustomTextWatcher(button_login, checkList));
        setLabelEnv();
        setDoneBehaviour();
        buildDialog();
    }


    private void setDoneBehaviour() {
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    if(email.getText().toString().equals("")){
                        return false;
                    }
                    else{
                        doLogin();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private class CustomTextWatcher implements TextWatcher {

        private View positiveAction;
        private List<View> checkList;

        public CustomTextWatcher(View positiveAction, List<View> checkList) {
            this.positiveAction = positiveAction;
            this.checkList = checkList;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            positiveAction.setEnabled(checkEmptyViews(checkList));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void setLabelEnv() {
        ambienteTest.setVisibility(SharedPreferenceAdapter.isPreproduzione(this)? View.VISIBLE : View.GONE);
    }

    private boolean checkEmptyViews(List<View> viewList) {
        for (View view : viewList) {
            EditText row = (EditText) view;
            if (row.length() == 0 || row.getText() == null) return false;
        }
        return true;
    }

    private int getSelectedEnvironmentChoice(){
        return SharedPreferenceAdapter.isPreproduzione(this)? 1:0;
    }

    @OnClick(R.id.button_settings)
    public void onSettingsClicked() {
            final Context context = this;
            new MaterialDialog.Builder(this)
                    .title(R.string.cambia_impostazioni)
                    .items(R.array.single_choice_switch_env)
                    .itemsCallbackSingleChoice(getSelectedEnvironmentChoice(), new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which){
                                case 0:{
                                    SharedPreferenceAdapter.setEnv(context, SharedPreferenceAdapter.Environments.PRODUZIONE);
                                    break;
                                }
                                case 1:{
                                    SharedPreferenceAdapter.setEnv(context, SharedPreferenceAdapter.Environments.PREPRODUZIONE);
                                    break;
                                }
                            }
                            setLabelEnv();
                            return true;
                        }
                    })
                    .cancelable(true)
                    .show();
    }

    private void buildDialog(){
        progressDialog = new MaterialDialog.Builder(this)
                .title("Attendere")
                .content("Verifica credenziali in corso...")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true).build();
    }

    private void showProgress(){
        progressDialog.show();
    }

    private void dismissProgress(){
        progressDialog.dismiss();
    }

    private void doLogin(){
        label_error_message.setVisibility(View.GONE);
        showProgress();
        String username = email.getText().toString();
        authorizedUserOnPlatform(username, password);
    }

    @OnClick(R.id.button_login)
    public void OnLoginButtonClick() {
        doLogin();
    }

    private boolean isUserAuthenticated(Exception e, Response<String> result){
        if (e != null || result == null || (result != null && result.getException() != null)) {
            //Errore generico
            label_error_message.setText(R.string.error_message_network);
            label_error_message.setVisibility(View.VISIBLE);
            return false;
        }

        int code = result.getHeaders().code();
        if (code == 401) {
            //Autenticazione errata
            label_error_message.setText(R.string.error_message_login);
            label_error_message.setVisibility(View.VISIBLE);
            email.setError(getString(R.string.error_username));
            password.setError(getString(R.string.error_pwd));
            return false;
        }
        return true;
    }

    /**
     * Autorizza l'utente sulla piattaforma JAM
     *
     * @param username
     * @param password
     */
    private void authorizedUserOnPlatform(final String username, final EditText password) {
        HttpClientRequest.executeInitSapAuthorization(AuthenticatedWFMUser.this,
                username,
                password.getText().toString(), new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {

                        dismissProgress();

                        if(!isUserAuthenticated(e,result)){
                            return;
                        }

                        try {
                            String oauth = result.getRequest().getHeaders().get("Authorization");
                            Log.e(TAG, "BASIC_AUTH_VALUE [" + oauth + "] USERNAME [" + username + "]");
                            Crashlytics.setUserIdentifier(username);
                            SharedPreferenceAdapter.storeBasicAutheticationToken(AuthenticatedWFMUser.this, oauth);
                            SharedPreferenceAdapter.storeActualSWFMUsername(AuthenticatedWFMUser.this, username);

                            HttpClientRequest.initCookieManager(AuthenticatedWFMUser.this, result);

                            //Go to next activity
                            User user = UserHelperFactory.getAuthenticatedUser(AuthenticatedWFMUser.this);
                            if (user == null) {
                                Navigator.with(AuthenticatedWFMUser.this)
                                        .build()
                                        .goTo(OauthActivity.class)
                                        .animation()
                                        .commit();
                                finish();
                            } else {
                                Navigator.with(AuthenticatedWFMUser.this)
                                        .build()
                                        .goTo(MainActivity.class)
                                        .animation()
                                        .commit();
                                finish();
                            }

                        } catch (Exception _ex) {
                            //Autenticazione errata
                            label_error_message.setVisibility(View.VISIBLE);
                            email.setError("username errato");
                            password.setError("password errata");
                            return;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private final static String TAG = AuthenticatedWFMUser.class.getName();
};