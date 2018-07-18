package it.acea.android.socialwfm.app.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.greenfrvr.hashtagview.HashtagView;
import com.koushikdutta.ion.Ion;
import com.mikepenz.materialdrawer.view.BezelImageView;

import org.fingerlinks.mobile.android.utils.widget.EmptyRecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.delegate.SWFMSearchPlantFeedsDelegate;
import it.acea.android.socialwfm.app.events.FeedCreatedEvent;
import it.acea.android.socialwfm.app.events.FeedDeletedEvent;
import it.acea.android.socialwfm.app.events.FeedListUpdate;
import it.acea.android.socialwfm.app.model.User;
import it.acea.android.socialwfm.app.ui.ErrorViewManager;
import it.acea.android.socialwfm.app.ui.dialog.NewPostDialog;
import it.acea.android.socialwfm.app.ui.dialog.NewPostOnPlantDialog;
import it.acea.android.socialwfm.dao.SearchPlantFeedsUtil;
import it.acea.android.socialwfm.factory.FeedDialogFactory;
import it.acea.android.socialwfm.factory.ImageFactory;
import it.acea.android.socialwfm.factory.UserHelperFactory;
import it.acea.android.socialwfm.http.response.search.ObjectReference;
import it.acea.android.socialwfm.http.response.search.SearchResponse;
import tr.xip.errorview.ErrorView;

/**
 * Created by a.simeoni on 19/10/2016.
 */

public class SearchPlantFeedsFragment extends Fragment {

    @Bind(R.id.new_post_header)
    View newPostView;
    @Bind(R.id.plant_feeds)
    EmptyRecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.listContainer)
    public FrameLayout listContainer;
    @Bind(R.id.plant_feeds_header)
    View feedsListHeader;
    @Bind(R.id.coordinatorlayout_feeds_impianto_container)
    CoordinatorLayout coordinatorLayout;

    ErrorView emptyView;

    private PlantFeedAdapter adapter;
    private SearchPlantFeedsUtil searchHelper;
    private String plantId;
    private String activityDesc;
    private Animation slidedown;

    enum EmptyViewStyle{NET_ERR,NO_FEEDS}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds_impianto, container, false);
        ButterKnife.bind(this, view);
        emptyView = ErrorViewManager.getErrorViewNoData(getContext());
        emptyView.setVisibility(View.GONE);
        listContainer.addView(emptyView);
        initListeners();
        initNewPostView();
        initRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initHeaderAnimation();
        initSearchHelper();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Ion.getDefault(getContext()).cancelAll(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void refreshAdapter(List<SearchResponse> data) {
        adapter.updateList(data);
    }

    private void setEmptyView(EmptyViewStyle type){
        switch (type){
            case NET_ERR:{
                ErrorViewManager.setErrorViewNoConnection(emptyView);
                emptyView.showRetryButton(true);
                emptyView.setVisibility(View.VISIBLE);
                break;
            }
            case NO_FEEDS:{
                ErrorViewManager.setErrorViewNoPlantFeeds(emptyView);
                emptyView.showRetryButton(false);
                emptyView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void initHeaderAnimation(){
        slidedown = AnimationUtils.loadAnimation(getContext(),R.anim.bounce_in_from_top);
        slidedown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                feedsListHeader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initSearchHelper() {
        activityDesc = (String) this.getArguments().get("activityDesc");
        String anlage = (String)this.getArguments().get("anlage");
        plantId = (String) this.getArguments().get("plantId");
        //Se ho anche il campo id equipement allora lo aggiungo al tag
        if(anlage!=null && !anlage.trim().equals("")){
            plantId = plantId + "_"+anlage;
        }
        plantId = plantId.toLowerCase();

        searchHelper = new SearchPlantFeedsUtil(getContext(), plantId);
        searchHelper.setDelegate(new SWFMSearchPlantFeedsDelegate() {
            @Override
            public void onSearchCompleted(SearchError error, List<SearchResponse> resultSet) {
                List<SearchResponse> data;
                if (error == null && resultSet != null) {
                    if(resultSet.size()==0){
                        setEmptyView(EmptyViewStyle.NO_FEEDS);
                    }
                    else{
                        emptyView.setVisibility(View.GONE);
                    }
                    data = resultSet;
                } else {
                    setEmptyView(EmptyViewStyle.NET_ERR);
                    data = new ArrayList<>();
                }
                refreshAdapter(data);
                swipeRefreshLayout.setRefreshing(false);
                setHeader(data.size()>0);
            }
        });
    }

    private void setHeader(boolean visible){
        if(visible){
            feedsListHeader.startAnimation(slidedown);
        }
        else{
            feedsListHeader.setVisibility(View.INVISIBLE);
        }
    }

    private void initListeners() {
        emptyView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                loadData();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        newPostView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = UserHelperFactory.getAuthenticatedUser(getContext());
                final NewPostOnPlantDialog newPostDialog = new NewPostOnPlantDialog(getActivity(), NewPostDialog.TYPE.MEMBER, user.getId(),plantId,activityDesc);
                newPostDialog.setCallBack(new NewPostDialog.CallBack() {
                    @Override
                    public void onSucces() {
                        newPostDialog.dismiss();
                        loadData();
                    }
                });
                newPostDialog.show();
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new PlantFeedAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
    }



    private void loadData() {
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        searchHelper.loadPlantFeeds();
    }

    private void initNewPostView(){
        User user = UserHelperFactory.getAuthenticatedUser(getContext());
        BezelImageView avatar = (BezelImageView) newPostView.findViewById(R.id.avatar);
        TextView label = (TextView) newPostView.findViewById(R.id.label);
        ImageFactory imageFactory = new ImageFactory(getContext(),
                user.getId(),
                ImageFactory.TYPE.MEMBER,
                Utils.avatarPlaceholder(getContext(), user.getFirstName(), user.getLastName()));
        imageFactory.into(avatar);
        label.setText(Html.fromHtml("<b>" + user.getFullName() + "</b><br>" + getContext().getString(R.string.scrivi_un_post_impianto)));
    }

    @Subscribe
    public void updateFeedList(FeedListUpdate event) {
        loadData();
    }

    @Subscribe
    public void feedDeleted(FeedDeletedEvent event) {
        showUpdateMessage(R.string.feeds_plant_feed_deleted);
    }

    @Subscribe
    public void feedCreated(FeedCreatedEvent event) {
        showUpdateMessage(R.string.feeds_plant_feed_created);
    }

    private void showUpdateMessage(@StringRes int message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.feeds_plant_update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        }).show();
    }

    class PlantFeedAdapter extends RecyclerView.Adapter<PlantFeedAdapter.PlantFeedViewHolder> {
        private List<SearchResponse> list;
        private Context viewHolderContext;
        private final  String hashtagPattern = "#[^\\s#]*";
        private final String  mentionPattern = "@([A-Za-z0-9_-]+) (([A-Za-z0-9_-]+)??)";

        public PlantFeedAdapter() {
            this.list = new ArrayList<>();
        }


        public void updateList(List<SearchResponse> list) {
            this.list = list;
            notifyDataSetChanged();

        }

        private List<String> findTagMatches(String input){
            List<String> result = Utils.getRegexMatches(hashtagPattern,input);
            Utils.cleanMatchFromResultSet("#"+plantId,result);//Rimuovo il tag relativo all'impianto
            return result;
        }

        private void initHashTagView(PlantFeedViewHolder holder,String feedText){
            List<String> matches = findTagMatches(feedText);
            holder.hashtagView.setData(matches);
        }

        private void initContentView(PlantFeedViewHolder holder,String feedText){
            String cleanedText = feedText.replaceAll(hashtagPattern,"");
            cleanedText = cleanedText.replaceAll(mentionPattern,"");
            cleanedText = cleanedText.replaceAll("sede_tecnica","");
            holder.contentPost.setText(cleanedText);
        }

        @Override
        public PlantFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            viewHolderContext = parent.getContext();
            View v = LayoutInflater.from(viewHolderContext)
                    .inflate(R.layout.row_plant_feed, parent, false);

            return new PlantFeedViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PlantFeedViewHolder holder, int position) {
            SearchResponse response = list.get(position);
            holder.dataPost.setText(response.getLastMotifiedAtRelative(viewHolderContext));
            initContentView(holder,response.getObjectReference().getTitle());
            initHashTagView(holder,response.getObjectReference().getTitle());
            holder.itemView.setTag(response.getObjectReference());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class PlantFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.dataPost)
            TextView dataPost;
            @Bind(R.id.postContent)
            TextView contentPost;
            @Bind(R.id.tag_view)
            HashtagView hashtagView;

            public PlantFeedViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                ObjectReference reference = (ObjectReference) v.getTag();
                switch (reference.getType().toLowerCase()){
                    case "feedentry":
                        new FeedDialogFactory(getContext(), reference.getODataURL()).show();
                        break;
                    case "comment":
                        new FeedDialogFactory(getContext(), reference.getODataURL()).show();
                        break;
                }
            }
        }
    }
}