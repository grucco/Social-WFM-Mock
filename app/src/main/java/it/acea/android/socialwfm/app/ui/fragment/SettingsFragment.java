package it.acea.android.socialwfm.app.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;
import org.fingerlinks.mobile.android.utils.fragment.PreferenceFragment;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.ui.WebViewActivity;
import it.acea.android.socialwfm.factory.UserHelperFactory;

/**
 * @author Andrea Cappelli
 *         Date: 25/09/15
 *         Time: 15:34
 */
public class SettingsFragment extends PreferenceFragment {

    //private GoogleCloudMessaging gcm = null;
    private Toolbar toolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        toolbar = ((BaseActivity) activity).getToolbar();
    }

    //@Override
    //public void onResume() {
    //super.onResume();
    //toolbar.setTitle(R.string.action_settings);
    //toolbar.setBackgroundResource(R.color.primary);
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.preferences);
        try {
            PackageInfo mInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            findPreference("version").setSummary(mInfo.versionName + " (" + mInfo.versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().setBackgroundResource(R.drawable.background_white);
        getView().setClickable(true);

        findPreference("privacy_policy").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Bundle b = new Bundle();
                        b.putString("url_to_load",
                                "http://www.acea.it/ViewCategory.aspx?lang=it&catid=75a0f95272e142f2be0d26644a69da54"
                                /*getActivity().getResources().getString(R.string.privacy_url)*/);
                        Navigator.with(getActivity())
                                .build()
                                .goTo(WebViewActivity.class, b)
                                .animation()
                                .addRequestCode(Constant.WEBVIEW_INTENT_RESULT)
                                .commit();

                        return false;
                    }
                });

        findPreference("user_logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UserHelperFactory.logoutUserFromSystem(getActivity(), new UserHelperFactory.LogoutCallBack() {
                    @Override
                    public void onFinish() {
                        Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
                return false;
            }
        });

    }

    private final static String TAG = SettingsFragment.class.getName();
}