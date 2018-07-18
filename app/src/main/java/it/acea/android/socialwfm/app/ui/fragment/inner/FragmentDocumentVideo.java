package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;

/**
 * Created by Raphael on 23/11/2015.
 */
public class FragmentDocumentVideo extends BaseFragment {

    @Bind(R.id.document)
    VideoView videoView;

    private String documentPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        documentPath = bundle.getString("documentPath");
        View view = inflater.inflate(R.layout.fragment_document_video, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(documentPath);
        Uri uri = Uri.fromFile(file);
        MediaController mc = new MediaController(getContext());
        videoView.setMediaController(mc);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            videoView.pause();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            videoView = null;
        }
    }

}
