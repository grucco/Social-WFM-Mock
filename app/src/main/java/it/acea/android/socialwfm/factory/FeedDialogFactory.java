package it.acea.android.socialwfm.factory;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.CommentNotifyEvent;
import it.acea.android.socialwfm.app.events.FeedDeletedEvent;
import it.acea.android.socialwfm.app.ui.adapter.CommentAdapter;
import it.acea.android.socialwfm.app.ui.adapter.FeedAdapter;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.feed.Comment;
import it.acea.android.socialwfm.http.response.feed.Feed;

/**
 * Created by raphaelbussa on 08/01/16.
 */
public class FeedDialogFactory {

    private Context context;
    private String url;
    private TYPE type;

    public FeedDialogFactory(Context context, String url) {
        this.context = context;
        this.type = TYPE.NONE;
        if (url.toLowerCase().contains("comments")) {
            this.type = TYPE.COMMENT;
            this.url = url + "?$format=json&$expand=Creator,AtMentions";
        }
        if (url.toLowerCase().contains("feedentries")) {
            this.type = TYPE.FEED;
            this.url = url + "?$format=json&$expand=PreviewImage,Creator,ThumbnailImage,AtMentions";
        }
    }

    public void show() {
        MaterialDialog.Builder b = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_feed_factory, false);
        new MyMaterialDialog(b, url).loadData();
    }

    private class MyMaterialDialog extends MaterialDialog {
        private RecyclerView list;
        private String mUrl;
        MyMaterialDialog(Builder builder, String url) {
            super(builder);
            mUrl = url;
            initView();
        }

        @Override
        protected void onStart() {
            super.onStart();
            EventBus.getDefault().register(this);
        }

        @Override
        protected void onStop() {
            super.onStop();
            EventBus.getDefault().unregister(this);
        }

        @Subscribe
        public void onFeedDeleted(FeedDeletedEvent event) {
            dismiss();
        }

        @Subscribe
        public void onCommentNotify(CommentNotifyEvent event) {
            loadData();
        }

        void loadData() {
            final MaterialDialog progress = new MaterialDialog.Builder(MyMaterialDialog.this.getContext())
                    .content(R.string.feed_dialog_load_content_progress)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
            HttpClientRequest.genericGet(MyMaterialDialog.this.getContext(), mUrl, new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        CheckErrorInJamResponse.check(e, result);
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        switch (type) {
                            case FEED:
                                Feed feed = gson.fromJson(result.getAsJsonObject("d").getAsJsonObject("results"), Feed.class);
                                if (feed == null) return;
                                List<Feed> feedList = new ArrayList<>();
                                feedList.add(feed);
                                FeedAdapter feedAdapter = new FeedAdapter(MyMaterialDialog.this.getContext());
                                list.setAdapter(feedAdapter);
                                feedAdapter.update(feedList);
                                break;
                            case COMMENT:
                                Comment comment = gson.fromJson(result.getAsJsonObject("d").getAsJsonObject("results"), Comment.class);
                                if (comment == null) return;
                                List<Comment> commentList = new ArrayList<>();
                                commentList.add(comment);
                                CommentAdapter commentAdapter = new CommentAdapter(MyMaterialDialog.this.getContext(), null);
                                list.setAdapter(commentAdapter);
                                commentAdapter.update(commentList);
                                break;
                            default:
                                return;
                        }
                        MyMaterialDialog.this.show();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        MyMaterialDialog.this.dismiss();
                        Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        progress.dismiss();
                    }
                }
            });
        }

        private void initView() {
            if (this.getCustomView() != null) {
                list = (RecyclerView) this.getCustomView().findViewById(R.id.support_list);
                GridLayoutManager manager = new GridLayoutManager(context, 1);
                list.setLayoutManager(manager);
            }
        }
    }

    public enum TYPE {
        FEED, COMMENT, NONE
    }

}
