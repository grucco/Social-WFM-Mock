package it.acea.android.socialwfm.app.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import it.acea.android.socialwfm.R;

/**
 * Created by fabio on 19/06/15.
 */
public class ThirdPartActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LibsFragment fragment = new LibsBuilder()
                .withFields(R.string.class.getFields())
                /*.withLibraries("crouton", "activeandroid", "actionbarsherlock", "showcaseview")*/
                .withVersionShown(true)
                /*.withLicenseShown(false)*/
                .fragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(
                R.id.container,
                fragment).commit();

        setNavigationIcon(R.drawable.ic_ab_back_white);
        setIcon(R.drawable.acea_logo_toolbar);
        setSubtitle(R.string.preference_library);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void setNavigationOnClickListener() {
        finish();
    }

    @Override
    protected int getToolbarProjectId() {
        return R.id.toolbar_above;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_empty_fragment;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
};