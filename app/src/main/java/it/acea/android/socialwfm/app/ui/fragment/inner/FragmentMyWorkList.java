package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;
import io.realm.RealmResults;
import it.acea.android.socialwfm.BuildConfig;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.events.CanReloadList;
//import it.acea.android.socialwfm.app.model.odl.MockOdl;
import it.acea.android.socialwfm.app.model.odl.Odl;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.adapter.MyWorkRecycleAdapter;
import it.acea.android.socialwfm.factory.SAPMobilePlatformFactory;
import it.acea.android.socialwfm.service.UpdateSchedulerJob;
import tr.xip.errorview.ErrorView;

/**
 * Created by fabio on 22/10/15.
 */
public class FragmentMyWorkList extends BaseFragment implements MyWorkRecycleAdapter.CallBack {

    private MyWorkRecycleAdapter adapter;

    @Bind(R.id.list_mywork)
    RecyclerView list_mywork;

    @Bind(R.id.progress)
    ProgressWheel progress;

    @Bind(R.id.error)
    ErrorView empty_view_layout;

    public OnOdlItemClickListener mListener;
    private OnitemsLoadingFinish mListenerLoading;
    private BroadcastReceiver receiver;

    public interface OnitemsLoadingFinish {
         void items(List<Odl> odlBeanList);
         //void itemss(List<MockOdl> odlBeanList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(UpdateSchedulerJob.RESPONSE_MESSAGE);
                Log.e(TAG, "UPDATE RESULT DATA FROM JOB [" + s + "]");
                if (s.equalsIgnoreCase("RESULT_OK")) {
                    //update all list data
                    reloadListFromDb();
                }
            }
        };

    }

    private void reloadListFromDb(){
        List<Odl> odlList = SAPMobilePlatformFactory.with(getContext())
                .getActualOdlList();

        /*List<MockOdl> o = new ArrayList<>(odlList.size());
        for(int i=0; i<odlList.size(); i++) {

            MockOdl oo = new MockOdl(odlList.get(i));
            if(i==1 || i==2) {
                oo.setPlant(odlList.get(0).getPlant());
            } else if(i==4) {
                oo.setPlant(odlList.get(0).getPlant());
                oo.setCustomer(odlList.get(0).getCustomer());
            }
            o.add(oo);

        }*/

        //adapter.updater(o);
        adapter.update(odlList);
    }

    /**
     * Imposta immagine e messaggio della error view
     * @param emptyList Se true viene visualizzato un messaggio di lista vuota, se false
     *                  viene visualizzato errore di rete
     * */
    private void showErrorView(boolean emptyList){
        if(emptyList)
            ErrorViewManager.setErrorViewNoOdl(empty_view_layout);

        else
            ErrorViewManager.setErrorViewNoConnection(empty_view_layout);

        empty_view_layout.setVisibility(View.VISIBLE);
    }

    private void hideErrorView(){
        empty_view_layout.setVisibility(View.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mywork_list, null);
        ButterKnife.bind(this, view);

        mListenerLoading = CodeUtils.ensure_cast(OnitemsLoadingFinish.class, getParentFragment());
        mListener = CodeUtils.ensure_cast(OnOdlItemClickListener.class, getParentFragment());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MyWorkRecycleAdapter(getActivity());
        adapter.setCallBack(this);
        list_mywork.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.list_space);
        list_mywork.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        list_mywork.setLayoutManager(manager);

        empty_view_layout.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                reloadList();
            }
        });

    }

    boolean isStart = false;
    boolean isCallingEnabled = false;

    private void reloadList(){
        list_mywork.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        hideErrorView();
        SAPMobilePlatformFactory
                .with(getActivity())
                .test(BuildConfig.DEV_MODE)
                .sourceDropbox(BuildConfig.DROPBOX_SOURCE) /** Only For Development test out of ACEA **/
                .withAuthorization()
                .retrieveOdl(new SAPMobilePlatformFactory.CallBack() {

                    @Override
                    public void onFailure(String message) {

                        if (message.equalsIgnoreCase("CID_ERRATO")) {
                            isCallingEnabled = false;
                            SharedPreferenceAdapter.setAppCidRegistered(getActivity(), null);
                            loadDataRequest();
                            return;
                        }

                        progress.setVisibility(View.GONE);
                        list_mywork.setVisibility(View.GONE);
                        showErrorView(false);
                        onClick(null);

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.preference_logout_popup_title)
                                .content(message)
                                .iconRes(android.R.drawable.ic_dialog_alert)
                                .build()
                                .show();
                    }

                    @Override
                    public void onSuccess(List<Odl> odlList) {
                        progress.setVisibility(View.GONE);
                        isCallingEnabled = true;

                        if (odlList != null && !odlList.isEmpty()) {
                            /*List<MockOdl> o = new ArrayList<>(odlList.size());
                            for(int i=0; i<odlList.size(); i++) {

                                MockOdl oo = new MockOdl(odlList.get(i));
                                if(i==1 || i==2) {
                                    oo.setPlant(odlList.get(0).getPlant());
                                } else if(i==4) {
                                    oo.setPlant(odlList.get(0).getPlant());
                                    oo.setCustomer(odlList.get(0).getCustomer());
                                }
                                o.add(oo);

                            }*/
                            //adapter.updater(o);
                            //mListenerLoading.itemss(o);
                            adapter.update(odlList);
                            mListenerLoading.items(odlList);

                            list_mywork.setVisibility(View.VISIBLE);
                            onClick(null);
                        } else {
                            //empty view
                            list_mywork.setVisibility(View.GONE);
                            showErrorView(true);
                            onClick(null);
                        }
                    }

                    @Override
                    public void onFailure() {
                        progress.setVisibility(View.GONE);
                        list_mywork.setVisibility(View.GONE);
                        showErrorView(false);
                        onClick(null);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            loadDataRequest();
        }
    }

    private void loadDataRequest() {

        if (!isCallingEnabled) {
            Log.e(TAG, "CHIAMATA ODL-WORK --------------------------------------------------------------->");
            isCallingEnabled = true;

            list_mywork.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            hideErrorView();
            SAPMobilePlatformFactory
                    .with(getActivity())
                    .test(BuildConfig.DEV_MODE)
                    .sourceDropbox(BuildConfig.DROPBOX_SOURCE) /** Only For Development test out of ACEA **/
                    .withAuthorization()
                    .retrieveOdl(new SAPMobilePlatformFactory.CallBack() {

                        @Override
                        public void onFailure(String message) {

                            if (message.equalsIgnoreCase("CID_ERRATO")) {
                                isCallingEnabled = false;
                                SharedPreferenceAdapter.setAppCidRegistered(getActivity(), null);
                                loadDataRequest();
                                return;
                            }

                            progress.setVisibility(View.GONE);
                            list_mywork.setVisibility(View.GONE);
                            showErrorView(false);
                            onClick(null);

                            new MaterialDialog.Builder(getActivity())
                                    .title(R.string.preference_logout_popup_title)
                                    .content(message)
                                    .iconRes(android.R.drawable.ic_dialog_alert)
                                    .build()
                                    .show();
                        }

                        @Override
                        public void onSuccess(List<Odl> odlList) {
                            progress.setVisibility(View.GONE);
                            isCallingEnabled = true;

                            if (odlList != null && !odlList.isEmpty()) {
                                /*List<MockOdl> o = new ArrayList<>(odlList.size());
                                for(int i=0; i<odlList.size(); i++) {

                                    MockOdl oo = new MockOdl(odlList.get(i));
                                    if(i==1 || i==2) {
                                        oo.setPlant(odlList.get(0).getPlant());
                                    } else if(i==4) {
                                        oo.setPlant(odlList.get(0).getPlant());
                                        oo.setCustomer(odlList.get(0).getCustomer());
                                    }
                                    o.add(oo);

                                }*/



                        //        odlList.get(1).setPlant(odlList.get(0).getPlant());
                        //        odlList.get(2).setPlant(odlList.get(0).getPlant());
                        //        odlList.get(4).setPlant(odlList.get(0).getPlant());
                        //        odlList.get(4).setCustomer(odlList.get(0).getCustomer());

                                //adapter.updater(o);
                                //mListenerLoading.itemss(o);
                                adapter.update(odlList);
                                mListenerLoading.items(odlList);
                                list_mywork.setVisibility(View.VISIBLE);
                            } else {
                                //empty view
                                list_mywork.setVisibility(View.GONE);
                                showErrorView(true);
                                onClick(null);
                            }
                        }

                        @Override
                        public void onFailure() {
                            progress.setVisibility(View.GONE);
                            list_mywork.setVisibility(View.GONE);
                            showErrorView(false);
                            onClick(null);
                        }
                    });
        }
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    public void onClick(String id_data) {
        //Visualizza dettaglio
        if (mListener != null) {
            mListener.onOdlItemSelected(id_data);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) outRect.top = space;
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }

    public interface OnOdlItemClickListener {
        void onOdlItemSelected(String id);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver((receiver),
                        new IntentFilter(UpdateSchedulerJob.ODL_RESULT)
                );
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).
                unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    //L'evento è generato da MainActivity quando la home torna in foreground
    @Subscribe
    public void canReloadList(CanReloadList event){
        if(isStart){
            reloadListFromDb();
        }
    }

    @Override
    protected void setNavigationOnClickListener() {
    }

    public final static String TAG = FragmentMyWorkList.class.getName();
};