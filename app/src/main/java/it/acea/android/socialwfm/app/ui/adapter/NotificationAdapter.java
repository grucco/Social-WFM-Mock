package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.Log;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.model.Notification;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.fragment.FragmentMyWorkContainer;
import it.acea.android.socialwfm.app.ui.fragment.GroupFragment;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.factory.FeedDialogFactory;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by Raphael on 29/10/2015.
 */
public class NotificationAdapter extends RealmBaseAdapter<Notification> {

    private static final String TAG = NotificationAdapter.class.getName();

    private LayoutInflater layoutInflater;

    public NotificationAdapter(Context context, RealmResults<Notification> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_notification, null);
            holder = new ViewHolder();
            holder.read = convertView.findViewById(R.id.read);
            holder.data = (TextView) convertView.findViewById(R.id.data);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.image = (BezelImageView) convertView.findViewById(R.id.image);
            holder.accept = (Button) convertView.findViewById(R.id.accept);
            holder.dismiss = (Button) convertView.findViewById(R.id.dismiss);
            holder.actionButton = (LinearLayout) convertView.findViewById(R.id.action_button);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notification notification = getItem(position);
        if (notification.isRead()) {
            holder.read.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.read_notification_false));
        } else {
            holder.read.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.read_notification_true));
        }
        holder.data.setText(Utils.dataRelativa(context, Long.parseLong(notification.getCreatedAt().replace("/Date(", "").replace(")/", "").trim())));
        holder.description.setText(notification.getDescription());
        setIconType(holder.title, notification.getEventType());
        if (notification.getGroup() != null) {
            new ImageFactory(context, notification.getGroup().getId(), ImageFactory.TYPE.GROUP).into(holder.image);
            holder.title.setText(notification.getGroup().getName());
        } else {
            Drawable placeholder = Utils.avatarPlaceholder(context, notification.getSender().getFirstName(), notification.getSender().getLastName());
            new ImageFactory(context, notification.getSender().getId(),
                    ImageFactory.TYPE.MEMBER,
                    placeholder)
                    .into(holder.image);
            holder.title.setText(notification.getSender().getFullName());
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CodeUtils.checkInternetConnection(context)) {
                        Utils.errorToast((context));
                        return;
                    }
                    try {
                        FragmentMyWorkContainer.drawer.closeDrawer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, notification.getSender().getId());
                    Navigator.with(context)
                            .build()
                            .goTo(Fragment.instantiate(context, ProfileFragment.class.getName()), bundle, R.id.container)
                            .addToBackStack()
                            .add()
                            .commit();
                }
            });
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                HttpClientRequest.setNotificationRead(context, notification.getId(), new FutureCallback<Response<String>>() {
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
                            RealmResults<Notification> notificationList = realm.where(Notification.class).findAllSorted("CreatedAt", RealmResults.SORT_ORDER_DESCENDING);
                            updateRealmResults(notificationList);
                        }
                    }
                });
                if (holder.actionButton.getVisibility() == View.GONE) {
                    holder.actionButton.setVisibility(View.VISIBLE);
                } else {
                    holder.actionButton.setVisibility(View.GONE);
                }
            }
        });
        holder.accept.setText(notification.getAcceptText());
        holder.dismiss.setText(notification.getRejectText());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                HttpClientRequest.executeRequestNotificationAccept(context, notification.getId(), new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) return;
                        holder.actionButton.setVisibility(View.GONE);
                        Log.d(TAG, result.getHeaders().message());
                        switch (notification.getEventType()) {
                            case "AutoGroupMember":
                            case "InviteToGroup":
                                if (notification.getGroup() == null) break;
                                Bundle bundle = new Bundle();
                                bundle.putString("ID_GROUP", notification.getGroup().getId());
                                Bundle supportBundle = new Bundle();
                                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                                supportBundle.putString(Constant.SUPPORT_FRAGMENT_TAG, GroupFragment.class.getName());
                                supportBundle.putString(Constant.SUPPORT_FRAGMENT, GroupFragment.class.getName());
                                Navigator.with(context)
                                        .build()
                                        .goTo(SupportActivity.class, supportBundle)
                                        .commit();
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                notification.removeFromRealm();
                                realm.commitTransaction();
                                RealmResults<Notification> notificationList = realm.where(Notification.class).findAllSorted("CreatedAt", RealmResults.SORT_ORDER_DESCENDING);
                                updateRealmResults(notificationList);
                                break;
                            case "Like":
                            case "ReplyInFeed":
                            case "MentionInFeed":
                            case "PostOnWall":
                                if (notification.getObjectReference() != null) {
                                    if (notification.getObjectReference().getType().toLowerCase().equals("group")) {
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putString("ID_GROUP", notification.getObjectReference().getId());
                                        Bundle supportBundle1 = new Bundle();
                                        supportBundle1.putBundle(Constant.SUPPORT_BUNDLE, bundle1);
                                        supportBundle1.putString(Constant.SUPPORT_FRAGMENT_TAG, GroupFragment.class.getName());
                                        supportBundle1.putString(Constant.SUPPORT_FRAGMENT, GroupFragment.class.getName());
                                        Navigator.with(context)
                                                .build()
                                                .goTo(SupportActivity.class, supportBundle1)
                                                .commit();
                                        break;
                                    }
                                    FeedDialogFactory dialogFactory = new FeedDialogFactory(context, notification.getObjectReference().getODataURL());
                                    dialogFactory.show();
                                }
                                break;
                        }
                    }
                });
            }
        });
        holder.dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                HttpClientRequest.executeRequestNotificationDismiss(context, notification.getId(), new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) return;
                        holder.actionButton.setVisibility(View.GONE);
                        Log.d(TAG, result.getHeaders().message());
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        notification.removeFromRealm();
                        realm.commitTransaction();
                        RealmResults<Notification> notificationList = realm.where(Notification.class).findAllSorted("CreatedAt", RealmResults.SORT_ORDER_DESCENDING);
                        updateRealmResults(notificationList);
                    }
                });
            }
        });
        return convertView;
    }

    static class ViewHolder {
        View read;
        TextView data;
        TextView title;
        TextView description;
        BezelImageView image;
        Button accept;
        Button dismiss;
        LinearLayout actionButton;
        LinearLayout item;
    }

    private void setIconType(TextView textView, String type) {
        switch (type) {
            case "MentionInFeed":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contatcs_notification_unpressed, 0, 0, 0);
                break;
            case "Like":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contatcs_notification_unpressed, 0, 0, 0);
                break;
            case "PostOnWall":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contatcs_notification_unpressed, 0, 0, 0);
                break;

            case "InviteToGroup":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_groups_notification_unpressed, 0, 0, 0);
                break;
            case "AutoGroupMember":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_groups_notification_unpressed, 0, 0, 0);
                break;

            case "ReplyInFeed":
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_conversations_notification_unpressed, 0, 0, 0);
                break;
        }
    }

}