package it.acea.android.socialwfm.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.Constant;

/**
 * Created by Raphael on 16/11/2015.
 */
public class SupportActivity extends BaseActivity {

    private static final String TAG = SupportActivity.class.getName();

    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras().getBundle(Constant.BUNDLE());

        if (bundle != null) {
            String fragment = bundle.getString(Constant.SUPPORT_FRAGMENT);
            tag = bundle.getString(Constant.SUPPORT_FRAGMENT_TAG);
            if (tag == null) tag = fragment;
            Bundle fragmentBundle = bundle.getBundle(Constant.SUPPORT_BUNDLE);
            Navigator.with(SupportActivity.this)
                    .build()
                    .goTo(Fragment.instantiate(SupportActivity.this, fragment), fragmentBundle, R.id.container)
                    .tag(tag)
                    .addToBackStack()
                    .add()
                    .commit();
        }
        setNavigationIcon(R.drawable.ic_ab_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void navigateToNextStep(String odl_id, String _fragmentName) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("actual.odl.selected", odl_id);

        Fragment _f = Fragment.instantiate(SupportActivity.this, _fragmentName);
        Navigator.with(SupportActivity.this)
                .build()
                .goTo(_f, bundle, R.id.container)
                .addToBackStack()
                .replace()
                .commit();
    }

    @Override
    protected int getToolbarProjectId() {
        return R.id.toolbar;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (Navigator.with(SupportActivity.this).utils().getActualTag().equals(tag)) {
            finish();
            return;
        }
        if (Navigator.with(SupportActivity.this).utils().canGoBack(getSupportFragmentManager())) {
            Navigator.with(SupportActivity.this).utils().goToPreviousBackStack();
            return;
        }
        super.onBackPressed();
    }
}