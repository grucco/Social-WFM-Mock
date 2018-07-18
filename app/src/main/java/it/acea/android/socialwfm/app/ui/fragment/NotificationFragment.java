package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.adapter.NotificationAdapter;
import it.acea.android.socialwfm.http.HttpClientRequest;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 29/10/2015.
 */
public class NotificationFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private static final String TAG = NotificationFragment.class.getName();

    private ListView listView;
    //private SwipeRefreshLayout refreshLayout;

    private ToggleButton contactsNotification;
    private ToggleButton groupsNotification;
    private ToggleButton conversationNotification;
    private NotificationAdapter adapter = null;
    private RelativeLayout fragmentContainer;
    private RealmResults<Notification> notificationList;
    private RelativeLayout loader;
    private ErrorView errorView;
    //LocalBroadcastManager broadcaster = null;

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "Instance LocalBroadcastManager ------------------------>");
        broadcaster = LocalBroadcastManager.getInstance(getActivity());
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, null);
        //refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        listView = (ListView) view.findViewById(R.id.update_list);
        contactsNotification = (ToggleButton) view.findViewById(R.id.contacts_notification);
        groupsNotification = (ToggleButton) view.findViewById(R.id.groups_notification);
        conversationNotification = (ToggleButton) view.findViewById(R.id.conversation_notification);
        fragmentContainer = (RelativeLayout) view.findViewById(R.id.fragment_container);
        loader = (RelativeLayout) view.findViewById(R.id.loader);
        errorView = (ErrorView) view.findViewById(R.id.error);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //refreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
        //refreshLayout.setOnRefreshListener(this);
        contactsNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilter(v);
            }
        });
        groupsNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilter(v);
            }
        });
        conversationNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilter(v);
            }
        });
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                getNotification();
            }
        });
    }

    boolean isStart = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!isStart) {
            isStart = true;
            getNotification();
        }
    }

    private void updateFilter(View v) {
        if (!contactsNotification.isChecked() && !groupsNotification.isChecked() && !conversationNotification.isChecked()) {
            ((ToggleButton) v).setChecked(true);
            Snackbar.make(fragmentContainer, R.string.error_message_notification, Snackbar.LENGTH_LONG).show();
            return;
        }
        showNotification(contactsNotification.isChecked(),
                groupsNotification.isChecked(),
                conversationNotification.isChecked());
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    private void getNotification() {
        if (!CodeUtils.checkInternetConnection(getActivity())) {
            errorView.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
            return;
        }
        loader.setVisibility(View.VISIBLE);
        //refreshLayout.setRefreshing(true);
        HttpClientRequest.executeRequestGetNotification(getActivity(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                loader.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                if (e != null) {

                    return;
                }
                Log.d(TAG, result.toString());
                GsonBuilder builder = Utils.getRealmGsonBuilder();
                Gson gson = builder.create();
                Type listType = new TypeToken<List<Notification>>() {
                }.getType();
                List<Notification> notificationBeanList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                notificationBeanList = Utils.getCleanedNotifications(notificationBeanList);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(notificationBeanList);
                realm.commitTransaction();
                //refreshLayout.setRefreshing(false);
                showNotification(contactsNotification.isChecked(),
                        groupsNotification.isChecked(),
                        conversationNotification.isChecked());
            }
        });
    }


    //type all, group, replyinfeed
    private void showNotification(boolean contactsNotification, boolean groupsNotification, boolean conversationNotification) {
        boolean addOr = false;
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Notification> realmQuery = realm.where(Notification.class);
        if (contactsNotification) {
            realmQuery.equalTo("EventType", "MentionInFeed", false);
            realmQuery.or();
            realmQuery.equalTo("EventType", "Like", false);
            realmQuery.or();
            realmQuery.equalTo("EventType", "PostOnWall", false);
            addOr = true;
        }
        if (groupsNotification) {
            if (addOr) realmQuery.or();
            realmQuery.equalTo("EventType", "InviteToGroup", false);
            realmQuery.or();
            realmQuery.equalTo("EventType", "AutoGroupMember", false);
            addOr = true;
        }
        if (conversationNotification) {
            if (addOr) realmQuery.or();
            realmQuery.equalTo("EventType", "ReplyInFeed", false);
            addOr = true;
        }
        Log.d(TAG, realmQuery.toString());
        /*List<Notification> notificationList = realm.where(Notification.class).findAllSorted("CreatedAt", false);*/
        notificationList = realmQuery.findAllSorted("CreatedAt", RealmResults.SORT_ORDER_DESCENDING);
        adapter = new NotificationAdapter(getActivity(), notificationList, true);
        listView.setAdapter(adapter);
        //listView.setOnItemClickListener(this);
        isStart = false;
    }

    @Override
    public void onRefresh() {
        getNotification();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Notification notification = notificationList.get(position);
        if (notification.isRead()) {
            showNotificationDialog(notification);
            return;
        }
        HttpClientRequest.setNotificationRead(getActivity(), notification.getId(), new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e != null) {
                    return;
                }
                if (result.getHeaders().code() == 204) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    notification.setRead(true);
                    realm.commitTransaction();

                    //sendResult(UpdateSchedulerJob.NOTIFICATION_RESULT, "RESULT_OK");
                    showNotification(contactsNotification.isChecked(),
                            groupsNotification.isChecked(),
                            conversationNotification.isChecked());
                    showNotificationDialog(notification);
                }
            }
        });
    }

    private void showNotificationDialog(final Notification notification) {
        String title;
        if (notification.getGroup() != null) {
            title = notification.getGroup().getName();
        } else {
            title = notification.getSender().getFullName();
        }
        new MaterialDialog.Builder(getActivity())
                .positiveText(notification.getAcceptText())
                .negativeText(notification.getRejectText())
                .title(title)
                .content(notification.getDescription())
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        switch (dialogAction) {
                            case POSITIVE:
                                HttpClientRequest.executeRequestNotificationAccept(getActivity(), notification.getId(), new FutureCallback<Response<JsonObject>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<JsonObject> result) {
                                        if (e != null) return;
                                        Log.d(TAG, result.getHeaders().message());
                                        Bundle bundle;
                                        Bundle supportBundle;
                                        switch (notification.getEventType()) {
                                            case "AutoGroupMember":
                                            case "InviteToGroup":
                                                bundle = new Bundle();
                                                bundle.putString("ID_GROUP", notification.getGroup().getId());
                                                supportBundle = new Bundle();
                                                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                                                supportBundle.putString(Constant.SUPPORT_FRAGMENT_TAG, GroupFragment.class.getName());
                                                supportBundle.putString(Constant.SUPPORT_FRAGMENT, GroupFragment.class.getName());
                                                Navigator.with(getActivity())
                                                        .build()
                                                        .goTo(SupportActivity.class, supportBundle)
                                                        .commit();
                                                break;
                                        }
                                    }
                                });
                                break;
                            case NEGATIVE:
                                HttpClientRequest.executeRequestNotificationDismiss(getActivity(), notification.getId(), new FutureCallback<Response<JsonObject>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<JsonObject> result) {
                                        if (e != null) return;
                                        Log.d(TAG, result.getHeaders().message());
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.beginTransaction();
                                        notification.removeFromRealm();
                                        realm.commitTransaction();
                                        notificationList = realm.where(Notification.class).findAllSorted("CreatedAt", RealmResults.SORT_ORDER_DESCENDING);
                                        adapter.updateRealmResults(notificationList);
                                    }
                                });
                                break;
                        }
                    }
                })
                .build()
                .show();
    }

    /*public void sendResult(String result, String message) {
        Intent intent = new Intent(result);
        if (message != null) {
            intent.putExtra(UpdateSchedulerJob.RESPONSE_MESSAGE, message);
        }
        broadcaster.sendBroadcast(intent);
    }*/
};