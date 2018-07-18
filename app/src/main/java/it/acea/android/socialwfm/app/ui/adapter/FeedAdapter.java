package it.acea.android.socialwfm.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.events.FeedDeletedEvent;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.dialog.FeedCommentsDialog;
import it.acea.android.socialwfm.app.ui.dialog.FullScreenImageDialog;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.feed.Feed;

/**
 * Created by Raphael on 09/11/2015.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MainViewHolder> {

    private List<Feed> feedList;
    private Context context;
    private CallBack callBack;
    private String customGroupId = null;


    public FeedAdapter(Context context) {
        this.context = context;
        this.feedList = new ArrayList<>();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder,  int position) {
        final Feed feed = feedList.get(position);
        if (feed.getPreviewImage() == null) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            new ImageFactory(context, feed.getId(), ImageFactory.TYPE.FEED).into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImageDialog dialog = new FullScreenImageDialog(context, holder.image.getDrawable());
                    dialog.show();
                }
            });
        }
        Drawable drawable = Utils.avatarPlaceholder(context, feed.getCreator().getFirstName(), feed.getCreator().getLastName());
        new ImageFactory(context, feed.getCreator().getId(), ImageFactory.TYPE.MEMBER, drawable).into(holder.avatar);
        holder.avatar.setTag(feed.getCreator().getId());
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, (String) v.getTag());
                Bundle supportBundle = new Bundle();
                supportBundle.putBundle(Constant.SUPPORT_BUNDLE, bundle);
                supportBundle.putString(Constant.SUPPORT_FRAGMENT_TAG, ProfileFragment.class.getName());
                supportBundle.putString(Constant.SUPPORT_FRAGMENT, ProfileFragment.class.getName());
                Navigator.with(context)
                        .build()
                        .goTo(SupportActivity.class, supportBundle)
                        .addRequestCode(1001)
                        .commit();
            }
        });
        holder.username.setText(Html.fromHtml(
                feed.getTitle()
                        .replace(feed.getCreator().getFullName(), "<b>" + feed.getCreator().getFullName() + "</b>")
                        .replace("[", "<i>")
                        .replace("]", "</i>")));
        if (!TextUtils.isEmpty(feed.getText())) {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(Html.fromHtml(feed.getText()));
        } else {
            holder.content.setVisibility(View.GONE);
        }

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+) ([A-Za-z0-9_-]+)");
        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        String mentionScheme = "acea://profile/";
        String hashtagScheme = "acea://hashtag/";
        Linkify.addLinks(holder.content, mentionPattern, mentionScheme);
        Linkify.addLinks(holder.content, hashtagPattern, hashtagScheme);


        holder.content.setLinkTextColor(context.getResources().getColor(R.color.primary));

        holder.data.setText(Utils.dataRelativa(context, Long.parseLong(feed.getCreatedAt().replace("/Date(", "").replace(")/", "").trim())));
        holder.like.setText(feed.getLikesCount() + " mi piace");
        holder.comments.setText(feed.getRepliesCount() + " commenti");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                FeedCommentsDialog dialog = new FeedCommentsDialog(context, feed.getId());
                dialog.setCustomGroupId(customGroupId);
                dialog.show();
            }
        });
        if (feed.isLiked()) {
            holder.actionLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_social_like_pressed_post));
        } else {
            holder.actionLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_social_like_unpressed_post));
        }
        holder.actionLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                HttpClientRequest.setLikePost(context, feed, new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        switch (result) {
                            case "error":
                                //TODO error msg
                                break;
                            case "true":
                                feed.setLikesCount(feed.getLikesCount() + 1);
                                feed.setLiked(true);
                                holder.actionLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_social_like_pressed_post));
                                break;
                            case "false":
                                feed.setLikesCount(feed.getLikesCount() - 1);
                                feed.setLiked(false);
                                holder.actionLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_social_like_unpressed_post));
                                break;
                        }
                        holder.like.setText(feed.getLikesCount() + " mi piace");
                    }
                });
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!CodeUtils.checkInternetConnection(context)) {
                    Utils.errorToast((context));
                    return;
                }
                if (feed.getLikesCount() == 0) {
                    //no likers!!!
                    return;
                }
                HttpClientRequest.executeRequestGetFeedLikers(context, feed.getId(), new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        Type listType = new TypeToken<List<Profile>>() {
                        }.getType();
                        final List<Profile> list = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                        PopupMenu menu = new PopupMenu(context, v);
                        for (int i = 0; i < list.size(); i++) {
                            menu.getMenu().add(0, i, i, list.get(i).getFullName());
                        }
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Bundle bundle = new Bundle();
                                bundle.putString(ProfileFragment.ARGUMENT_PROFILE_ID, list.get(item.getItemId()).getId());
                                Navigator.with(context)
                                        .build()
                                        .goTo(Fragment.instantiate(context, ProfileFragment.class.getName()), bundle, R.id.container)
                                        .addToBackStack()
                                        .add()
                                        .commit();
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }
        });
        holder.delete.setVisibility(isDeleteFeedShown(context, feed) ? View.VISIBLE : View.INVISIBLE);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedAdapter.this.eliminaFeed(feed, holder.getAdapterPosition() - 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void update(List<Feed> feedList) {
        this.feedList.clear();
        this.feedList.addAll(feedList);
        this.notifyDataSetChanged();
    }

    public void setCustomGroupId(String customGroupId) {
        this.customGroupId = customGroupId;
    }


    private void eliminaFeed(final Feed feed, final int position) {
        new MaterialDialog.Builder(context)
                .title(R.string.feed_delete_dialog_titolo)
                .content(R.string.feed_delete_dialog_content)
                .negativeText(R.string.feed_delete_dialog_annulla)
                .positiveText(R.string.feed_delete_dialog_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final MaterialDialog progress = new MaterialDialog.Builder(context)
                                .content(context.getString(R.string.feed_delete_progress))
                                .progress(true, 0)
                                .show();
                        HttpClientRequest.executeDeleteFeed(context, feed.getId(), new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                int messaggio;
                                try {
                                    CheckErrorInJamResponse.check(e, result);
                                    notifyItemRemoved(position);
                                    messaggio = R.string.feed_delete_success;
                                    EventBus.getDefault().post(new FeedDeletedEvent(position));
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    messaggio = R.string.feed_delete_failed;
                                } finally {
                                    progress.dismiss();
                                }
                                Toast.makeText(context, messaggio, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).show();
    }


    private boolean isDeleteFeedShown(Context context, Feed feed) {
        return UserHelperFactory.getAuthenticatedUser(context).getId().equals(feed.getCreator().getId());
    }

    public interface CallBack {
        void onClick(Feed feed);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView data;
        private BezelImageView avatar;
        private TextView username;
        private TextView content;
        private ImageView image;
        private TextView like;
        private TextView comments;
        private ImageView actionLike;
        private ImageView delete;

        public MainViewHolder(View itemView) {
            super(itemView);
            data = (TextView) itemView.findViewById(R.id.data);
            avatar = (BezelImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            content = (TextView) itemView.findViewById(R.id.content);
            image = (ImageView) itemView.findViewById(R.id.image);
            like = (TextView) itemView.findViewById(R.id.like);
            comments = (TextView) itemView.findViewById(R.id.comments);
            actionLike = (ImageView) itemView.findViewById(R.id.action_like);
            delete = (ImageView) itemView.findViewById(R.id.deleteFeed);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) callBack.onClick(feedList.get(getAdapterPosition() - 1));
        }
    }

}