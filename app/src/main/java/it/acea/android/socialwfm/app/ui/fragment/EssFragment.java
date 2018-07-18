package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.ui.MainActivity;
import tr.xip.errorview.ErrorView;

/**
 * Created by n.fiorillo on 07/03/2016.
 */
public abstract class EssFragment extends BaseFragment {

    private static final String TAG = EssFragment.class.getSimpleName();
    private volatile MaterialDialog progressDialog;
    private volatile int dismissStep = 0;


    private void buildProgressDialog() {
        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.attendere)
                .content(R.string.download_dati_in_corso)
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true)
                .build();
    }


    @Subscribe
    public void onBackStackChanged(MainActivity.BackStackChanged event) {
        setSubtitle(R.string.cud_cedolino_subtitle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildProgressDialog();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null) menu.clear();
        inflater.inflate(R.menu.ess_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String fragmentName;
        String actualTag = Navigator.with(getActivity()).utils().getActualTag();
        String tagName = getClass().getName();

        /* Sono costretto a controllare se il tag di navigator e' quello corrente,
           i fragment sono aggiunti (no replace) quindi risponderebbero anche se non visualizzati */

        //if (id == R.id.menu_intro && actualTag.endsWith(tagName)) {
        //    showIntro();
            //return true;
        //}
        switch (id) {
            case R.id.menu_monte_ore:
                fragmentName = MonteOreFragment.class.getName();
                break;
            case R.id.menu_cartellino_orologio:
                fragmentName = CartellinoOrologioFragment.class.getName();
                break;
            case R.id.menu_timbrature:
                fragmentName = TimbratureFragment.class.getName();
                break;
            case R.id.menu_giustificativi:
                fragmentName = GiustificativiFragment.class.getName();
                break;
            case R.id.menu_quadratura:
                fragmentName = QuadraturaFragment.class.getName();
                break;
            case R.id.menu_reperibilita:
                fragmentName = ReperibilitaFragment.class.getName();
                break;
            case R.id.menu_cedolino:
                fragmentName = CudCedolinoFragment.class.getName();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        String tag = Constant.TAG_ESS + fragmentName;

        if(actualTag.equals(tag)){
            return super.onOptionsItemSelected(item);
        }
        if (Navigator.with(getActivity()).utils().canGoBackToSpecificPoint(tag, R.id.container, getFragmentManager())) {

            Navigator.with(getActivity()).utils().goBackToSpecificPoint(tag);

        } else {
            Navigator.with(getContext())
                    .build()
                    .goTo(Fragment.instantiate(getContext(), fragmentName), R.id.container)
                    .tag(tag)
                    .addToBackStack()
                    .add()
                    .commit();
        }
        return true;

    }

    protected void showIntro() {}

    public synchronized void showProgressDialog() {
        this.dismissStep++;
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    public void setProgressDialogTitle(@NonNull String title) {
        this.progressDialog.setTitle(title);
    }

    public void setProgressDialogTitle(@StringRes int titleRes) {
        this.progressDialog.setTitle(titleRes);
    }

    public void setProgressDialogContent(@NonNull String content) {
        this.progressDialog.setContent(content);
    }

    public void setProgressDialogContent(@StringRes int contentRes) {
        this.progressDialog.setContent(contentRes);
    }

    public synchronized void showProgressDialog(int numStep) {
        this.dismissStep += numStep;
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    public synchronized void dismissDialog() {
        if (--dismissStep <= 0 && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setSubtitle("");
    }

    public abstract ErrorView getErrorView();

    public void showError() {
        getErrorView().setVisibility(View.VISIBLE);
    }

    public void dismissError() {
        getErrorView().setVisibility(View.GONE);
    }

    public void showFileErrorMessage() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.attenzione)
                .content(R.string.export_file_fallito)
                .positiveText(R.string.chiudi)
                .autoDismiss(true)
                .show();
    }
}
