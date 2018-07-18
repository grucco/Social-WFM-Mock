package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mikepenz.aboutlibraries.util.Util;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.activity.BaseActivity;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.FollowMember;
import it.acea.android.socialwfm.app.events.MemberSelectedFromSearch;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.adapter.MemberRecycleAdapter;
import it.acea.android.socialwfm.app.ui.dialog.SearchGrouppiContattiDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroContatti;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import tr.xip.errorview.ErrorView;

/**
 * Created by fabio on 03/11/15.
 */
public class FragmentMember extends BaseFragment implements MemberRecycleAdapter.CallBack, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = FragmentMember.class.getName();
    @Bind(R.id.list_members)
    EmptyRecyclerView list_members;
    @Bind(R.id.progress)
    ProgressWheel progress;
    MemberRecycleAdapter adapter;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.error)
    ErrorView errorView;

    private SearchGrouppiContattiDialog searchDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member, null);
        ButterKnife.bind(this, view);
        setSubtitle(R.string.label_menu_contacts);
        Utils.setStatusBarColor(getActivity(),R.color.primary);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshLayout();
        initErrorView();
        initRecyclerView();
        //startIntro();
        updateRecycleViewData();
    }


    private void initErrorView() {
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                updateRecycleViewData();
            }
        });
    }

    private void initRecyclerView() {
        adapter = new MemberRecycleAdapter(getActivity());
        adapter.setCallBack(this);
        list_members.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        list_members.setLayoutManager(manager);
        list_members.setEmptyView(errorView);
    }

    private void loadData() {
        Realm realm = Realm.getDefaultInstance();
        User u = UserHelperFactory.getAuthenticatedUser(getContext());
        List<Profile> lista = realm.where(Profile.class).notEqualTo("Id", u.getId()).findAllSorted("FirstName");
        adapter.update(lista);
    }

    private void initRefreshLayout() {
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.primary),
                ContextCompat.getColor(getContext(), R.color.accent));
        refreshLayout.setOnRefreshListener(this);
    }


    private void startIntro(){
        View searchView   = getActivity().findViewById(R.id.action_search);
        new IntroContatti(getActivity(),list_members,searchView).start();
    }

    @Subscribe
    public void onFollowMember(FollowMember event) {
        updateRecycleViewData();
    }


    private void getContacts() {
        progress.setVisibility(View.VISIBLE);
        dismissError();
        String myId = UserHelperFactory.getAuthenticatedUser(getContext()).getId();
        HttpClientRequest.executeRequestGetContacts(getContext(), myId, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                try {
                    CheckErrorInJamResponse.check(e, result);
                    Gson gson = Utils.getRealmGsonBuilder().create();
                    Type listType = new TypeToken<List<Profile>>() {
                    }.getType();
                    JsonElement o = result.getResult().getAsJsonObject("d").get("results");
                    List<Profile> lista = gson.fromJson(o, listType);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(lista);
                    realm.commitTransaction();
                    ErrorViewManager.setErrorViewNoContacts(errorView);
                    loadData();
                    if (adapter.getItemCount() > 0) onClick(adapter.getItem(0));

                } catch (Exception e1) {
                    e1.printStackTrace();
                    ErrorViewManager.setErrorViewNoConnection(errorView);
                    showError();
                } finally {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showError() {
        errorView.setVisibility(View.VISIBLE);
    }

    private void dismissError() {
        errorView.setVisibility(View.GONE);
    }


    private void updateRecycleViewData() {
        getContacts();
    }


    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Subscribe
    public void onMemberFromSearch(MemberSelectedFromSearch event) {
        selectProfile(event.id);
    }

    @Override
    public void onClick(Profile data) {
        KeyboardUtil.hideKeyboard(getActivity());
        selectProfile(data.getId());
    }

    private void selectProfile(String profileId) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, profileId);
        Navigator.with(getActivity())
                .build()
                .goTo(Fragment.instantiate(getActivity(), ProfileFragment.class.getName()), bundle, R.id.profile_container)
                .replace()
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.contacts_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                searchDialog = new SearchGrouppiContattiDialog(getActivity(),
                        SearchGrouppiContattiDialog.SearchMode.SEARCHMODE_CONTATTI);
                searchDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        updateRecycleViewData();
    }
}
