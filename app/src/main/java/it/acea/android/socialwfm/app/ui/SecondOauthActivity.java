package it.acea.android.socialwfm.app.ui;

import android.app.Activity;

/*import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.LogonCoreListener;
import com.sap.maf.tools.logon.logonui.api.LogonListener;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.maf.tools.logon.manager.LogonContext;
import com.sybase.persistence.DataVault;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import it.acea.android.socialwfm.R;*/

/**
 * Created by fabio on 02/11/15.
 */
public class SecondOauthActivity extends Activity {
    /*implements LogonCoreListener {

    private boolean isHttpsEnabled = false;
    private String appConnId = "swfm";
    private LogonCore logonCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initLogonCore();
        registerDevice();
    }

    private void initLogonCore() {
        logonCore = LogonCore.getInstance();
        logonCore.init(this, appConnId); //it.acea.android.socialwfm");
        logonCore.setLogonCoreListener(this);
    }

    private void registerDevice() {
        Log.i(TAG, "appConnId:" + appConnId);
        LogonCoreContext logonCoreContext = logonCore.getLogonContext();
        //logonCoreContext.setAppId(appConnId);
        logonCoreContext.setHost("sykcloud04.replycloud.prv"); //please input the correct IP address here
        int port = 8080;
        logonCoreContext.setPort(port);
        logonCoreContext.setHttps(isHttpsEnabled);
        LogonCore.UserCreationPolicy ucPolicy = LogonCore.UserCreationPolicy.automatic;
        logonCoreContext.setUserCreationPolicy(ucPolicy);
        try {
            logonCoreContext.setBackendUser("pippo"); // please note: here we input SMP cockpit logon username, NOT backend username
            logonCoreContext.setBackendPassword("pluto"); // please note: here we input SMP cockpit logon pwd, NOT backend pwd
        } catch (LogonCoreException e) {
            Log.e(TAG, "error entering user credentials", e);
        }


        logonCore.register(logonCoreContext);
        Log.i(TAG, "after register");
    }

    @Override
    public void applicationSettingsUpdated() {
        Log.e(TAG, "applicationSettingsUpdated");
    }

    @Override
    public void backendPasswordChanged(boolean arg0) {
        Log.e(TAG, "backendPasswordChanged ["+arg0+"]");
    }

    @Override
    public void deregistrationFinished(boolean arg0) {
        Log.e(TAG, "deregistrationFinished ["+arg0+"]");
    }

    @Override
    public void registrationFinished(boolean success, String message, int errorCode, DataVault.DVPasswordPolicy arg3) {
        Log.e(TAG, "registrationFinished, success? " + success); //If you get “success” in console output, go to cockpit/applictions/click on your application id, you will see if a new registration already created!
        Log.e(TAG, "registrationFinished, message= " + message);
        Log.e(TAG, "registrationFinished, errorCode= " + errorCode);

        try {
            String appConnID = LogonCore.getInstance().getLogonContext().getConnId();
            Log.d(TAG, "onLogonFinished: appcid:" + appConnID); Log.d(TAG, "onLogonFinished: endpointurl:" +
                    LogonCore.getInstance().getLogonContext().getAppEndPointUrl());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void traceUploaded() {
    }*/

/*{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        LogonUIFacade mLogonUIFacade = LogonUIFacade.getInstance();
        //Initialize the Logon UI Facade
        mLogonUIFacade.init(new LogonListener() {
            @Override
            public void onLogonFinished(String s, boolean b, LogonContext logonContext) {
                Log.e(TAG, "onLogonFinished [" + s + "] [" + b + "]");

            }

            @Override
            public void onSecureStorePasswordChanged(boolean b, String s) {
                Log.e(TAG, "onSecureStorePasswordChanged [" + s + "] [" + b + "]");

            }

            @Override
            public void onBackendPasswordChanged(boolean b) {
                Log.e(TAG, "onBackendPasswordChanged [" + b + "]");

            }

            @Override
            public void onUserDeleted() {
                Log.e(TAG, "onUserDeleted");

            }

            @Override
            public void onApplicationSettingsUpdated() {
                Log.e(TAG, "onApplicationSettingsUpdated");

            }

            @Override
            public void registrationInfo() {
                Log.e(TAG, "registrationInfo");

            }

            @Override
            public void objectFromSecureStoreForKey() {
                Log.e(TAG, "objectFromSecureStoreForKey");

            }

            @Override
            public void onRefreshCertificate(boolean b, String s) {
                Log.e(TAG, "onSecureStorePasswordChanged [" + s + "] [" + b + "]");

            }
        },SecondOauthActivity.this, "swfm");
        //
        //        CodeUtils.getValueFromManifest(SecondOauthActivity.this, "sap.api.key"));

        setContentView(mLogonUIFacade.logon());
    }*/

    private final static String TAG = SecondOauthActivity.class.getName();
}
