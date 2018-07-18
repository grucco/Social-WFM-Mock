package it.acea.android.socialwfm.app.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.fingerlinks.mobile.android.utils.widget.SlidingTabLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.FeedListUpdate;
import it.acea.android.socialwfm.app.events.FollowGroupEvent;
import it.acea.android.socialwfm.app.events.GroupDetailOpened;
import it.acea.android.socialwfm.app.events.GroupSelectedFromSearch;
import it.acea.android.socialwfm.app.events.GroupUnjoinEvent;
import it.acea.android.socialwfm.app.model.Group;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.adapter.GroupAdapter;
import it.acea.android.socialwfm.app.ui.dialog.FullScreenImageDialog;
import it.acea.android.socialwfm.app.ui.dialog.NewPostDialog;
import it.acea.android.socialwfm.app.ui.intro.IntroDettaglioGruppo;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.PopolateFeed;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 19/11/2015.
 */
public class GroupFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PopolateFeed.CallBack {

    private static final String TAG = GroupFragment.class.getSimpleName();

    @Bind(R.id.progress_layout)
    View progress_layout;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.lock)
    ImageView lock;
    @Bind(R.id.follow)
    ImageView follow;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.list_post)
    RecyclerView listPost;
    @Bind(R.id.tab_layout)
    SlidingTabLayout slidingTabLayout;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.placeholder)
    LinearLayout placeholder;
    @Bind(R.id.error)
    ErrorView errorView;

    private String id;
    private Group group;
    private PopolateFeed popolateFeed;
    private NewPostDialog newPostDialog;

    private boolean isFollowed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, null);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        id = bundle.getString("ID_GROUP");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        refreshLayout.setOnRefreshListener(this);
        getGroup();
        getFeedGroup();
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getGroup();
                getFeedGroup();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        Ion.getDefault(getContext()).cancelAll(this);
    }

    // Aggiorna il menu opzioni gestito dal fragment GroupsFragment
    private void updateOptionsMenu(){
        if(group!=null){//Il gruppo può essere cancellato
            GroupDetailOpened event = new GroupDetailOpened(group.getId(),group.getIsAdmin(),group.isAutoGroup());
            EventBus.getDefault().post(event);
        }
    }

    private void getGroup() {

                if (CodeUtils.checkInternetConnection(getActivity())) {
                    HttpClientRequest.executeRequestGetGroup(getActivity(), id,this, new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                getGroupFromRealm();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateUi();
                                    }
                                });
                                return;
                            }
                            GsonBuilder builder = Utils.getRealmGsonBuilder();
                            Gson gson = builder.create();
                            CheckError error = new Gson().fromJson(result, CheckError.class);
                            if (error.getError() != null) {
                                progress_layout.setVisibility(View.GONE);
                                errorView.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(),
                                        error.getError().getMessage().getValue(),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            User user = UserHelperFactory.getAuthenticatedUser(getContext());
                            isFollowed = result.toString().contains(user.getId());
                            group = gson.fromJson(result.getAsJsonObject("d").get("results"), Group.class);
                            EventBus.getDefault().postSticky(new FollowGroupEvent(group, isFollowed));
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(group);
                            realm.commitTransaction();
                            realm.close();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUi();
                                    updateOptionsMenu();//Caricato il gruppo posso impostare la funzione delete di un gruppo
                                }
                            });
                        }
                    });
                } else {
                    //getGroupFromRealm();
                    //updateUi();
                    progress_layout.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }

    }

    private void updateUi() {
        Realm realm = Realm.getDefaultInstance();
        Group group = realm.where(Group.class).equalTo("Id", id).findFirst();
        if (group == null) return;
        errorView.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
        GroupAdapter adapter = new GroupAdapter(getChildFragmentManager(), group.getId(), group.getDescription(), getActivity());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setTextColor(R.color.white);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(pager);
        new ImageFactory(getActivity(), group.getId(), ImageFactory.TYPE.GROUP, getActivity().getResources().getDrawable(R.drawable.background)).into(image);
        image.setTag(image.getDrawable());
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenImageDialog dialog = new FullScreenImageDialog(getActivity(), (Drawable) image.getTag());
                dialog.show();
            }
        });
        name.setText(group.getName());
        if (group.getGroupType().contains("private")) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.GONE);
        }
        if (isFollowed) {
            follow.setImageResource(R.drawable.ic_preferred_profile);
        } else {
            follow.setImageResource(R.drawable.ic_unpreferred_profile);
        }
        progress_layout.setVisibility(View.GONE);

    }

    private void startIntro(){
        new IntroDettaglioGruppo(getActivity(),follow,listPost.getChildAt(0)).start();
    }

    @OnClick(R.id.follow)
    void followUnfollow() {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            Utils.errorToast((getActivity()));
            return;
        }
        if (isFollowed) {

            new MaterialDialog.Builder(getContext())
                    .title(R.string.title_dettagliogruppo_abbandona)
                    .content(R.string.msg_dettagliogruppo_abbandona)
                    .positiveText(R.string.action_dettagliogruppo_abbandona)
                    .negativeText(R.string.action_dettagliogruppo_resta_nel_gruppo)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            HttpClientRequest.executeRequestUnJoinGroup(getActivity(), id, new FutureCallback<Response<JsonObject>>() {
                                @Override
                                public void onCompleted(Exception e, Response<JsonObject> result) {
                                    if (e != null) return;
                                    if (result.getHeaders().code() == 204) {
                                        isFollowed = false;
                                        EventBus.getDefault().postSticky(new FollowGroupEvent(group, isFollowed));
                                        follow.setImageResource(R.drawable.ic_unpreferred_profile);
                                        EventBus.getDefault().post(new GroupUnjoinEvent());
                                        Toast.makeText(getActivity(), "Hai smesso di seguire questo gruppo", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .show();


        } else {
            HttpClientRequest.executeRequestJoinGroup(getActivity(), id, new FutureCallback<Response<JsonObject>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonObject> result) {
                    if (e != null) return;
                    if (result.getHeaders().code() == 204) {
                        isFollowed = true;
                        EventBus.getDefault().postSticky(new FollowGroupEvent(group, isFollowed));
                        follow.setImageResource(R.drawable.ic_preferred_profile);
                        Toast.makeText(getActivity(), "Stai seguendo questo gruppo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getGroupFromRealm() {
        Realm realm = Realm.getDefaultInstance();
        group = realm.where(Group.class).equalTo("Id", id).findFirst();
        realm.close();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void onFinish() {
        refreshLayout.setRefreshing(false);
        //startIntro();
    }

    @Override
    public void onHeaderClick() {
        newPostDialog = new NewPostDialog(getActivity(), NewPostDialog.TYPE.GROUP, id);
        newPostDialog.setCustomGroupId(id);
        newPostDialog.setCallBack(new NewPostDialog.CallBack() {
            @Override
            public void onSucces() {
                newPostDialog.dismiss();
                popolateFeed.update();
            }
        });
        newPostDialog.show();
    }

    @Override
    public void onRefresh() {
        popolateFeed.update();
    }

    private void getFeedGroup() {
        refreshLayout.setRefreshing(true);
        String url = getActivity().getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/Groups('" + id + "')/FeedEntries?$format=json&$expand=PreviewImage,Creator,AtMentions";
        popolateFeed = new PopolateFeed(getActivity(), url);
        popolateFeed.setCustomGroupId(id);
        popolateFeed.into(listPost);
        popolateFeed.setCallBack(this);
    }

    @Subscribe
    public void updateFeedList(FeedListUpdate event) {
        popolateFeed.update();
    }
}
