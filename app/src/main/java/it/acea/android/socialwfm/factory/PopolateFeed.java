package it.acea.android.socialwfm.factory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.utils.CodeUtils;
import org.fingerlinks.mobile.android.utils.adapter.HeaderViewRecyclerAdapter;

import java.lang.reflect.Type;
import java.util.List;

import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.adapter.FeedAdapter;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.feed.CheckError;
import it.acea.android.socialwfm.http.response.feed.Feed;

/**
 * Created by Raphael on 04/11/2015.
 */
public class PopolateFeed {

    private FeedAdapter adapter;
    private Context context;
    private String url;
    private CallBack callBack;
    private View header;
    private LayoutInflater inflater;
    private String customGroupId = null;

    public PopolateFeed(Context context, String url) {
        this.context = context;
        this.url = url;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.header = inflater.inflate(R.layout.header_new_post, null);
        this.adapter = new FeedAdapter(context);
    }

    public void into(RecyclerView recyclerView) {
        adapter.setCustomGroupId(customGroupId);
        HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        headerViewRecyclerAdapter.addHeaderView(header);
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        recyclerView.addItemDecoration(new SpacesItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.feed_list_space)));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(headerViewRecyclerAdapter);
        setHeader();
        getFeed();
    }

    private void setHeader() {
        User user = UserHelperFactory.getAuthenticatedUser(context);
        BezelImageView avatar = (BezelImageView) header.findViewById(R.id.avatar);
        TextView label = (TextView) header.findViewById(R.id.label);
        CardView card = (CardView) header.findViewById(R.id.card);
        ImageFactory imageFactory = new ImageFactory(context,
                user.getId(),
                ImageFactory.TYPE.MEMBER,
                Utils.avatarPlaceholder(context, user.getFirstName(), user.getLastName()));
        imageFactory.into(avatar);
        label.setText(Html.fromHtml("<b>" + user.getFullName() + "</b>" + "<br>Scrivi un post"));
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) callBack.onHeaderClick();
            }
        });
    }

    private void getFeed() {
        if (!CodeUtils.checkInternetConnection(context)) {
            Utils.errorToast((context));
            return;
        }
        new Thread() {
            @Override
            public void run() {
                HttpClientRequest.executeRequestGetGenericFeed(context, url, new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            return;
                        }
                        GsonBuilder builder = Utils.getRealmGsonBuilder();
                        Gson gson = builder.create();
                        CheckError error = new Gson().fromJson(result, CheckError.class);
                        if (error.getError() != null) {
                            Toast.makeText(context,
                                    error.getError().getMessage().getValue(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Type listType = new TypeToken<List<Feed>>() {
                        }.getType();
                        final List<Feed> feedList = gson.fromJson(result.getAsJsonObject("d").get("results"), listType);
                        if (feedList == null) {
                            return;
                        }
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (callBack != null) callBack.onFinish();
                                adapter.update(feedList);
                            }
                        });
                    }
                });
            }
        }.start();

    }

    public void update() {
        getFeed();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onFinish();

        void onHeaderClick();
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
