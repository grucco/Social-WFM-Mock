package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Raphael on 23/11/2015.
 */
public class FragmentDocumentImage extends BaseFragment {

    @Bind(R.id.document)
    PhotoView photoView;

    private String documentPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        documentPath = bundle.getString("documentPath");
        View view = inflater.inflate(R.layout.fragment_document_image, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap bitmap = BitmapFactory.decodeFile(documentPath);
        photoView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

}
