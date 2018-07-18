package org.fingerlinks.mobile.android.utils.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by Raphael on 07/10/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(getToolbarProjectId());
        if (toolbar != null) {
            setEnvLabel(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNavigationOnClickListener();
                }
            });
            setSupportActionBar(toolbar);
        }
    }

    private void setEnvLabel(Toolbar toolbar) {
        if (SharedPreferenceAdapter.isPreproduzione(this)) {
            View t = LayoutInflater.from(this).inflate(R.layout.label_env, null);
            toolbar.addView(t);
        }
    }

    protected abstract int getToolbarProjectId();

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setIcon(int icon) {
        if (getSupportActionBar() != null) getSupportActionBar().setIcon(icon);
    }

    public void setTitle(String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    public void setTitle(int title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    public void setSubtitle(String subtitle) {
        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(subtitle);
    }

    public void setSubtitle(int subtitle) {
        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(subtitle);
    }

    public void setNavigationIcon(int icon) {
        if (toolbar != null) toolbar.setNavigationIcon(icon);
    }

    protected abstract int getLayoutResource();

    protected abstract void setNavigationOnClickListener();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
            getSupportFragmentManager().findFragmentById(R.id.container).onActivityResult(requestCode, resultCode, data);
        }
    }
};