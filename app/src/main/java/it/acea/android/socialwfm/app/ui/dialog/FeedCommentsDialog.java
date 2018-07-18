package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.utils.adapter.HeaderViewRecyclerAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.events.CommentListUpdate;
import it.acea.android.socialwfm.app.events.CommentNotifyEvent;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.adapter.CommentAdapter;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.CheckErrorInJamResponse;
import it.acea.android.socialwfm.http.response.feed.Comment;
import tr.xip.errorview.ErrorView;

/**
 * Created by Raphael on 09/11/2015.
 */
public class FeedCommentsDialog extends Dialog implements FutureCallback<JsonObject>, SwipeRefreshLayout.OnRefreshListener, DialogInterface.OnShowListener {

    private String feedId;
    private SwipeRefreshLayout refreshLayout;
    private CommentAdapter adapter;
    private NewPostDialog newPostDialog;
    private String customGroupId = null;
    private RecyclerView recyclerView;
    private ErrorView errorView;
    private ImageView avatar;
    private User user;
    private View header;


    public FeedCommentsDialog(Context context, String feedId) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_feed_comments);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.feedId = feedId;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = inflater.inflate(R.layout.header_new_post, null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        initRecyclerView();
        setHeaderView(header);
        this.setOnShowListener(this);
    }


    private void initRecyclerView() {
        errorView = (ErrorView) findViewById(R.id.empty_view);
        ErrorViewManager.setErrorViewNoComments(errorView);
        adapter = new CommentAdapter(getContext(), this);
        HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        headerViewRecyclerAdapter.addHeaderView(header);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        recyclerView = (RecyclerView) findViewById(R.id.list_post);
        recyclerView.addItemDecoration(new SpacesItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.feed_list_space)));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(headerViewRecyclerAdapter);
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                errorView.setVisibility(recyclerView.getAdapter().getItemCount() > 1 ? View.GONE: View.VISIBLE);
            }
        });

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
    public void updateList(CommentListUpdate event) {
        load();
    }

    private void setHeaderView(View header) {
        user = UserHelperFactory.getAuthenticatedUser(getContext());
        avatar = (BezelImageView) header.findViewById(R.id.avatar);
        TextView label = (TextView) header.findViewById(R.id.label);
        CardView card = (CardView) header.findViewById(R.id.card);
        label.setText(Html.fromHtml("<b>" + user.getFullName() + "</b>" + "<br>Scrivi un commento"));
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPostDialog = new NewPostDialog(getContext(), NewPostDialog.TYPE.COMMENT, feedId);
                newPostDialog.setCustomGroupId(customGroupId);
                newPostDialog.setCallBack(new NewPostDialog.CallBack() {
                    @Override
                    public void onSucces() {
                        newPostDialog.dismiss();
                        EventBus.getDefault().post(new CommentNotifyEvent());
                        load();
                    }
                });
                newPostDialog.show();
            }
        });
    }

    private void loadImageAvatar(User user, ImageView avatar) {
        ImageFactory imageFactory = new ImageFactory(getContext(),
                user.getId(),
                ImageFactory.TYPE.MEMBER,
                Utils.avatarPlaceholder(getContext(), user.getFirstName(), user.getLastName()));
        imageFactory.into(avatar);

    }

    private void load() {
        refreshLayout.setRefreshing(true);
        HttpClientRequest.executeRequestGetFeedComments(getContext(), feedId, this);
    }

    @Override
    public void onCompleted(Exception e, JsonObject result) {

        try {
            CheckErrorInJamResponse.check(e, result);
            GsonBuilder builder = Utils.getRealmGsonBuilder();
            Gson gson = builder.create();
            Type listType = new TypeToken<List<Comment>>() {
            }.getType();

            List<Comment> commentList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
            adapter.update(commentList);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        load();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        loadImageAvatar(user, avatar);
        load();

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.bottom = space * 4;
            } else {
                outRect.bottom = space;
            }
            outRect.left = space;
            outRect.right = space;
        }
    }

    public void setCustomGroupId(String customGroupId) {
        this.customGroupId = customGroupId;
    }
}
