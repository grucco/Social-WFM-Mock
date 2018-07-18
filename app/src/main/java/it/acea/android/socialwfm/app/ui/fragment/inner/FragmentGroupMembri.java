package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.NoScrollListView;
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
import it.acea.android.socialwfm.app.events.FollowGroupEvent;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.ListMembersAdapter;
import it.acea.android.socialwfm.app.ui.adapter.MemberRecycleAdapter;
import it.acea.android.socialwfm.app.ui.dialog.InviteGroupDialog;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.group.members.Members;

/**
 * Created by Raphael on 04/11/2015.
 */
public class FragmentGroupMembri extends BaseFragment implements MemberRecycleAdapter.CallBack {

    private static final String TAG = FragmentGroupMembri.class.getName();
    @Bind(R.id.num_members)
    TextView num_members;
    @Bind(R.id.list_members)
    NoScrollListView list_members;
    @Bind(R.id.add_member)
    ImageView add_member;
    @Bind(R.id.progress)
    ProgressWheel progressWheel;
    @Bind(R.id.coordinatorlayout_gruppo_membri)
    CoordinatorLayout coordinatorLayout;

    private String groupId;
    private ListMembersAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupId = getArguments().getString("GROUP_ID");
        View view = inflater.inflate(R.layout.fragment_group_membri, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
    }

    private void showUpdateMessage(@StringRes final int message) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        }, 1000);
    }

    private void fetchData() {
        progressWheel.setVisibility(View.VISIBLE);
        HttpClientRequest.executeRequestGetGroupMembers(getActivity(), this, groupId, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {

                if (e != null || result == null) {
                    progressWheel.setVisibility(View.GONE);
                    return;
                }
                GsonBuilder builder = Utils.getRealmGsonBuilder();
                Gson gson = builder.create();
                Type listType = new TypeToken<List<Members>>() {
                }.getType();
                try {
                    List<Members> membersList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                    loadData(membersList);
                } catch (Exception e1) {
                    Log.e(TAG, "fetchData", e1);
                } finally {
                    progressWheel.setVisibility(View.GONE);
                }

            }
        });

    }

    private void loadData(List<Members> membersList) {
        List<Members> filterMembersList = new ArrayList<>();
        for (Members members : membersList) {
            if (!members.getMemberType().toLowerCase().equals("pending")) {
                filterMembersList.add(members);
            }
        }

        num_members.setText(filterMembersList.size() + " membri");
        adapter = new ListMembersAdapter(getActivity(), filterMembersList);
        adapter.setCallback(this);
        list_members.setAdapter(adapter);
        Realm realm = Realm.getDefaultInstance();
        Group group = realm.where(Group.class).equalTo("Id", groupId).findFirst();
        add_member.setTag(group);
        add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(getActivity())) {
                    Utils.errorToast((getActivity()));
                    return;
                }
                InviteGroupDialog dialog = new InviteGroupDialog(getActivity(), (Group) v.getTag());
                dialog.setCallBack(new InviteGroupDialog.CallBack() {
                    @Override
                    public void onSuccess(Group group) {
                        showUpdateMessage(R.string.title_membri_gruppo_sono_stati_invitati);
                    }
                });
                dialog.show();
            }
        });
    }

    @Subscribe(sticky = true)
    public void followEvent(FollowGroupEvent event) {
        add_member.setVisibility(Group.authUserCanInvite(event.group, event.follow) ? View.VISIBLE: View.INVISIBLE );
        fetchData();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Ion.getDefault(getActivity()).cancelAll(this);
    }

    @Override
    public void onClick(Profile data) {
        selectProfile(data.getId());
    }

    private void selectProfile(String profileId) {
        Bundle bundle = new Bundle();
        Bundle supportBundle;
        bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, profileId);

        supportBundle = new Bundle();
        supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
        supportBundle.putString(Constant.SUPPORT_FRAGMENT, ProfileFragment.class.getName());
        Navigator.with(getContext())
                .build()
                .goTo(SupportActivity.class, supportBundle)
                .commit();
    }
}
