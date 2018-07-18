package it.acea.android.socialwfm.app.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.Constant;
import it.acea.android.socialwfm.app.events.CommentListUpdate;
import it.acea.android.socialwfm.app.events.CommentNotifyEvent;
import it.acea.android.socialwfm.app.model.Profile;
import it.acea.android.socialwfm.app.ui.SupportActivity;
import it.acea.android.socialwfm.app.ui.fragment.ProfileFragment;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.feed.Comment;

/**
 * Created by Raphael on 09/11/2015.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MainViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private CallBack callBack;
    private Dialog dialog;

    public CommentAdapter(Context context, Dialog dialog) {
        this.context = context;
        this.dialog = dialog;
        this.commentList = new ArrayList<>();
    }


    private void deleteComment(final Comment comment, final int position) {
        new MaterialDialog.Builder(context)
                .title(R.string.comment_delete_dialog_titolo)
                .content(R.string.comment_delete_dialog_content)
                .negativeText(R.string.comment_delete_dialog_annulla)
                .positiveText(R.string.comment_delete_dialog_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final MaterialDialog progress = new MaterialDialog.Builder(context)
                                .content(context.getString(R.string.comment_delete_progress))
                                .progress(true, 0)
                                .show();
                        HttpClientRequest.executeDeleteComment(context, comment.getId(), new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {

                                int messaggio;
                                try {
                                    CheckErrorInJamResponse.check(e, result);
                                    messaggio = R.string.comment_delete_success;
                                    EventBus.getDefault().post(new CommentNotifyEvent());
                                    EventBus.getDefault().post(new CommentListUpdate());
                                } catch (Exception e1) {
                                    messaggio = R.string.comment_delete_failed;
                                    e1.printStackTrace();
                                }
                                finally {
                                    progress.dismiss();
                                }
                                Toast.makeText(context, messaggio, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .show();
    }

    public void update(List<Comment> commentList) {
        this.commentList.clear();
        this.commentList.addAll(commentList);
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        final Comment feed = commentList.get(position);
        new ImageFactory(context, feed.getCreator().getId(), ImageFactory.TYPE.MEMBER).into(holder.avatar);
        holder.username.setText(feed.getCreator().getFullName());
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
                        .commit();
            }
        });
        String text = feed.getTextWithPlaceholders();
        String expression = "@m\\{(\\d+)\\}";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(feed.getTextWithPlaceholders());
        int i = 0;
        while (matcher.find()) {
            text = text.replace(matcher.group(), feed.getAtMentions().getResults().get(i).getFullName());
            i++;
        }
        holder.content.setText(text);
        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+) ([A-Za-z0-9_-]+)");
        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        String mentionScheme = "acea://profile/";
        String hashtagScheme = "acea://hashtag/";
        Linkify.addLinks(holder.content, mentionPattern, mentionScheme);
        Linkify.addLinks(holder.content, hashtagPattern, hashtagScheme);
        holder.data.setText(Utils.dataRelativa(context, Long.parseLong(feed.getCreatedAt().replace("/Date(", "").replace(")/", "").trim())));
        holder.like.setText(feed.getLikesCount() + " mi piace");
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
                HttpClientRequest.setLikeComment(context, feed, new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        switch (result) {
                            case "error":
                                //TODO error msg
                                break;
                            case "true":
                                feed.setLikesCount(feed.getLikesCount() + 1);
                                feed.setLiked(true);
                                holder.actionLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_social_like_pressed_post));
                                break;
                            case "false":
                                feed.setLikesCount(feed.getLikesCount() - 1);
                                feed.setLiked(false);
                                holder.actionLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_social_like_unpressed_post));
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
                HttpClientRequest.executeRequestGetCommentLikers(context, feed.getId(), new FutureCallback<JsonObject>() {
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
                                if (dialog != null) dialog.dismiss();
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }
        });
        holder.deleteComment.setVisibility(feed.isCanDelete() ? View.VISIBLE : View.INVISIBLE);
        holder.deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(feed, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onClick(Comment comment);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView data;
        private BezelImageView avatar;
        private TextView username;
        private TextView content;
        private TextView like;
        private ImageView actionLike;
        private View deleteComment;

        public MainViewHolder(View itemView) {
            super(itemView);
            data = (TextView) itemView.findViewById(R.id.data);
            avatar = (BezelImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            content = (TextView) itemView.findViewById(R.id.content);
            like = (TextView) itemView.findViewById(R.id.like);
            actionLike = (ImageView) itemView.findViewById(R.id.action_like);
            deleteComment =  itemView.findViewById(R.id.deleteComment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) callBack.onClick(commentList.get(getAdapterPosition() - 1));
        }
    }

}
