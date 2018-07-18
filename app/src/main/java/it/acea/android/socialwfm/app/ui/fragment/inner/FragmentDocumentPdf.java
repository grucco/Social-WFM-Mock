package it.acea.android.socialwfm.app.ui.fragment.inner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import org.fingerlinks.mobile.android.utils.fragment.BaseFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.acea.android.socialwfm.R;

/**
 * Created by Raphael on 23/11/2015.
 */
public class FragmentDocumentPdf extends BaseFragment implements OnLoadCompleteListener, OnPageChangeListener {

    @Bind(R.id.document)
    PDFView pdfView;

    private String documentPath;
    private String subtitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        documentPath = bundle.getString("documentPath");
        subtitle = getToolbar().getSubtitle().toString();
        View view = inflater.inflate(R.layout.fragment_document_pdf, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(documentPath);
        pdfView.fromFile(file)
                .enableSwipe(true)
                .onLoad(this)
                .onPageChange(this)
                .swipeVertical(true)
                .load();
    }

    @Override
    public boolean onSupportBackPressed(Bundle bundle) {
        return false;
    }

    @Override
    protected void setNavigationOnClickListener() {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pdfView = null;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        setSubtitle(subtitle + " (" + page + "/" + pageCount + ")");
    }
}
