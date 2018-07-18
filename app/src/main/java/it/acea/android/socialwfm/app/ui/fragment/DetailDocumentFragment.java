package it.acea.android.socialwfm.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.async.future.FutureCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.fingerlinks.mobile.android.navigator.Navigator;
import org.fingerlinks.mobile.android.utils.Log;
import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import it.acea.android.socialwfm.app.events.FeedListUpdate;
import it.acea.android.socialwfm.app.ui.dialog.NewPostDialog;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentDocumentImage;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentDocumentPdf;
import it.acea.android.socialwfm.app.ui.fragment.inner.FragmentDocumentVideo;
import it.acea.android.socialwfm.factory.PopolateFeed;
import it.acea.android.socialwfm.http.HttpClientRequest;

/**
 * Created by Raphael on 23/11/2015.
 */
public class DetailDocumentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PopolateFeed.CallBack {

    private static final String TAG = DetailDocumentFragment.class.getName();

    @Bind(R.id.list_post)
    RecyclerView listPost;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.progress)
    ProgressWheel progress;

    private String documentId;
    private String documentType;
    private String documentName;
    private String groupId;

    private String docName;
    private String docExt;

    private PopolateFeed popolateFeed;
    private NewPostDialog newPostDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        documentId = bundle.getString("documentId");
        documentType = bundle.getString("documentType");
        documentName = bundle.getString("documentName");
        groupId = bundle.getString("groupId");
        View view = inflater.inflate(R.layout.fragment_detail_document, null);
        ButterKnife.bind(this, view);
        setSubtitle(documentName);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        refreshLayout.setOnRefreshListener(this);
        String[] parseExr = documentName.split("\\.");
        docName = parseExr[parseExr.length - 2];
        docExt = parseExr[parseExr.length - 1];
        EventBus.getDefault().register(this);
        loadFeed();
        loadDocument();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void loadDocument() {
        try {
            File file = File.createTempFile("tmp"+docName, "." + docExt);
            file.deleteOnExit();
            Log.d(TAG, file.getAbsolutePath());
            HttpClientRequest.executeRequestGetContent(getActivity(), documentId, documentType, file, new FutureCallback<File>() {
                @Override
                public void onCompleted(Exception e, File result) {
                    progress.setVisibility(View.GONE);
                    if (e != null) {
                        errorDocument();
                        return;
                    }
                    Log.d(TAG, result.getAbsolutePath());
                    Bundle bundle = new Bundle();
                    bundle.putString("documentPath", result.getAbsolutePath());
                    switch (docExt.toLowerCase()) {
                        case "pdf":
                            Navigator.with(getActivity())
                                    .build()
                                    .goTo(Fragment.instantiate(getActivity(), FragmentDocumentPdf.class.getName()), bundle, R.id.document_container)
                                    .add()
                                    .commit();
                            break;
                        case "png":
                        case "jpg":
                            Navigator.with(getActivity())
                                    .build()
                                    .goTo(Fragment.instantiate(getActivity(), FragmentDocumentImage.class.getName()), bundle, R.id.document_container)
                                    .add()
                                    .commit();
                            break;
                        case "mp4":
                            Navigator.with(getActivity())
                                    .build()
                                    .goTo(Fragment.instantiate(getActivity(), FragmentDocumentVideo.class.getName()), bundle, R.id.document_container)
                                    .add()
                                    .commit();
                            break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            errorDocument();
        }
    }

    private void errorDocument() {

    }

    private void loadFeed() {
        refreshLayout.setRefreshing(true);
        String url = getActivity().getResources().getString(R.string.oauth_consumer_base_url) + "/api/v1/OData/ContentItems(Id='" + documentId + "',ContentItemType='" + documentType + "')/FeedEntries?$format=json&$expand=PreviewImage,Creator,AtMentions";
        popolateFeed = new PopolateFeed(getActivity(), url);
        popolateFeed.setCustomGroupId(groupId);
        popolateFeed.into(listPost);
        popolateFeed.setCallBack(this);
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
    }

    @Override
    public void onHeaderClick() {
        newPostDialog = new NewPostDialog(getActivity(), NewPostDialog.TYPE.DOCUMENT, documentId);
        newPostDialog.setDocumentType(documentType);
        newPostDialog.setCustomGroupId(groupId);
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

    @Subscribe
    public void updateFeedList(FeedListUpdate event) {
        popolateFeed.update();
    }
}
