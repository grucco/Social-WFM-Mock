package it.acea.android.socialwfm.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.events.FeedListUpdate;
import it.acea.android.socialwfm.app.events.GroupDetailOpened;
import it.acea.android.socialwfm.app.events.GroupSelectedFromSearch;
import it.acea.android.socialwfm.app.events.GroupUnjoinEvent;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.GroupList;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.AllGroupAdapter;
import it.acea.android.socialwfm.app.ui.dialog.ChangeImageDialog;
import it.acea.android.socialwfm.app.ui.dialog.InviteGroupDialog;
import it.acea.android.socialwfm.app.ui.dialog.NewGroupDialog;
import it.acea.android.socialwfm.app.ui.dialog.SearchGrouppiContattiDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroGroups;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.feed.Feed;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 16/11/2015.
 */
public class GroupsFragment extends BaseFragment implements AllGroupAdapter.CallBack, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = GroupsFragment.class.getName();

    @Bind(R.id.list_members)
    EmptyRecyclerView list_members;

    @Bind(R.id.progress)
    ProgressWheel progress;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private SearchGrouppiContattiDialog searchDialog;
    private AllGroupAdapter allGroupAdapter;

    private String cancellableGroupId;

    @Bind(R.id.error)
    ErrorView errorView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        ButterKnife.bind(this, view);
        setSubtitle(R.string.groups);
        Utils.setStatusBarColor(getActivity(),R.color.primary);
        return view;
    }


    @Override
    public void onStart() {
        //http://greenrobot.org/eventbus/documentation/how-to-get-started/
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe
    public void onGroupMembershipExit(GroupUnjoinEvent event){
        loadGroupsInRealm();
    }

    @Subscribe
    public void onGroupSelectedFromSearch(GroupSelectedFromSearch event) {
        if (searchDialog != null) {
            searchDialog.dismiss();
        }
        String id = event.getId();
        Bundle bundle = new Bundle();
        Bundle supportBundle;
        bundle.putString("ID_GROUP", id);
        supportBundle = new Bundle();
        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
        supportBundle.putString(Constant.SUPPORT_FRAGMENT, GroupFragment.class.getName());
        Navigator.with(getContext())
                .build()
                .goTo(SupportActivity.class, supportBundle)
                .commit();
    }

    @Subscribe
    public void onGroupDisplayed(GroupDetailOpened event) {
        if(event.isAdmin() && !event.isAuto()){
            this.cancellableGroupId = event.getGroupId();
        }
        else{
            this.cancellableGroupId = null;
        }
        //Se non sono amministratore del gruppo questo valore viene passato null
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private ChangeImageDialog changeImageDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                searchDialog = new SearchGrouppiContattiDialog(getActivity(),
                        SearchGrouppiContattiDialog.SearchMode.SEARCHMODE_GRUPPI);
                searchDialog.show();
                break;

            }
            case R.id.add_group:
                if (!CodeUtils.checkInternetConnection(getActivity())) {
                    Utils.errorToast(getActivity());
                    break;
                }
                NewGroupDialog dialog = new NewGroupDialog(getActivity());
                dialog.setCallBack(new NewGroupDialog.CallBack() {
                    @Override
                    public void onSuccess(Group group) {
                        InviteGroupDialog dialog1 = new InviteGroupDialog(getActivity(), group);
                        dialog1.setCallBack(new InviteGroupDialog.CallBack() {
                            @Override
                            public void onSuccess(Group group) {
                                changeImageDialog = new ChangeImageDialog(getActivity(), getActivity(), group.getId(), ChangeImageDialog.TYPE.GROUP);
                                changeImageDialog.setCallBack(new ChangeImageDialog.CallBack() {
                                    @Override
                                    public void onSucces(String id) {
                                        loadGroupsInRealm();
                                        onClick(id);
                                    }
                                });
                                changeImageDialog.show();
                            }
                        });
                        dialog1.show();
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        try {
            menu.clear();
        } catch (Exception ignored) {
        }
        inflater.inflate(R.menu.groups_menu, menu);
        configDeleteGroupMenu(menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initErrorView();
        initSwipeToRefresh();
        allGroupAdapter = new AllGroupAdapter(getActivity());
        allGroupAdapter.setCallBack(this);
        list_members.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        list_members.setAdapter(allGroupAdapter);
        list_members.setEmptyView(errorView);
        loadGroupsInRealm();
    }

    private void configDeleteGroupMenu(Menu menu){

        boolean canDeleteGroup = (cancellableGroupId != null);
        menu.findItem(R.id.menu_overflow).setVisible(canDeleteGroup);
        if(canDeleteGroup){
            MenuItem deleteGroupItem =  menu.findItem(R.id.elimina_gruppo);
            deleteGroupItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    eliminaGruppo(cancellableGroupId);
                    return true;
                }
            });
        }
    }

    private void eliminaGruppo(final String groupId) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.gruppo_delete_dialog_titolo)
                .content(R.string.gruppo_delete_dialog_content)
                .negativeText(R.string.feed_delete_dialog_annulla)
                .positiveText(R.string.feed_delete_dialog_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final MaterialDialog progress = new MaterialDialog.Builder(getContext())
                                .content(getContext().getString(R.string.gruppo_delete_progress))
                                .progress(true, 0)
                                .show();

                        HttpClientRequest.executeDeleteGroup(getContext(), groupId, new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                progress.dismiss();
                                int messaggio;
                                if (result != null && result.getHeaders().code() == 204 ) {
                                    messaggio = R.string.gruppo_delete_success;
                                    loadGroupsInRealm();
                                } else {
                                    messaggio = R.string.gruppo_delete_failed;
                                }
                                Toast.makeText(getContext(), messaggio, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .show();
    }

    private void removeAllGroups(){
        Realm realm = Realm.getDefaultInstance();
        List<GroupList> groupList = realm.where(GroupList.class).findAll();
        List<Group> groups = realm.where(Group.class).findAll();
        realm.beginTransaction();
        groupList.clear();
        groups.clear();
        realm.commitTransaction();
    }

    private void loadGroupsInRealm() {
        progress.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        HttpClientRequest.executeRequestGetMyGroups(getActivity(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                try {
                    CheckErrorInJamResponse.check(e, result);
                    GsonBuilder builder = Utils.getRealmGsonBuilder();
                    Gson gson = builder.create();
                    Type listType = new TypeToken<List<Group>>() {
                    }.getType();

                    removeAllGroups();

                    List<Group> groupList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(groupList);
                    realm.commitTransaction();
                    List<GroupList> searchMyGroups = new ArrayList<>();
                    for (Group group : groupList) {
                        GroupList bean = new GroupList();
                        bean.setCreatedAt(group.getCreatedAt());
                        bean.setDescription(group.getDescription());
                        bean.setFollowersCount(group.getMembersCount());
                        bean.setId(group.getId());
                        bean.setTitle(group.getName());
                        bean.setMine(true);
                        bean.setAutoGroup(group.isAutoGroup());
                        bean.setGroupType(group.getGroupType());
                        bean.setAdmin(group.getIsAdmin());
                        searchMyGroups.add(bean);
                    }
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(searchMyGroups);
                    realm.commitTransaction();
                    loadGroups(true);
                    ErrorViewManager.setErrorViewNoGroups(errorView);
                } catch (Exception e1) {
                    Crashlytics.logException(e1);
                    ErrorViewManager.setErrorViewNoConnection(errorView);
                    errorView.setVisibility(View.VISIBLE);
                } finally {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }


    private void startIntro() {
        View searchView = getActivity().findViewById(R.id.action_search);
        View addGroupView = getActivity().findViewById(R.id.add_group);
        new IntroGroups(getActivity(), list_members, searchView, addGroupView).start();
    }

    private void loadGroups(boolean mine) {
        loadGroups(mine, null);
        //startIntro();
    }

    private void loadGroups(boolean mine, String query) {
        Realm realm = Realm.getDefaultInstance();
        List<GroupList> groupLists;
        if (mine) {
            if (query != null) {
                groupLists = realm.where(GroupList.class).contains("Title", query, false).equalTo("Mine", mine).findAllSorted("Title");
            } else {
                groupLists = realm.where(GroupList.class).equalTo("Mine", mine).findAllSorted("Title");
            }
        } else {
            if (query != null) {
                groupLists = realm.where(GroupList.class).contains("Title", query, false).findAllSorted("Title");
            } else {
                groupLists = realm.where(GroupList.class).findAllSorted("Title");
            }
        }
        allGroupAdapter.update(groupLists);
        if (groupLists.size() > 0) onClick(groupLists.get(0).getId());
    }


    private void initSwipeToRefresh() {
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.primary), ContextCompat.getColor(getContext(), R.color.accent));
        refreshLayout.setOnRefreshListener(this);
    }

    private void initErrorView() {
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                loadGroupsInRealm();
            }
        });
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onClick(String groupId) {
        KeyboardUtil.hideKeyboard(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString("ID_GROUP", groupId);
        Navigator.with(getActivity())
                .build()
                .goTo(Fragment.instantiate(getActivity(), GroupFragment.class.getName()), bundle, R.id.group_container)
                .replace()
                .commit();
    }

    @Override
    public void onRefresh() {
        this.refreshLayout.setRefreshing(false);
        loadGroupsInRealm();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (changeImageDialog != null) {
            changeImageDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
