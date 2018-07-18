package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.WindowManager;

import it.acea.android.socialwfm.R;
import uk.co.senab.photoview.PhotoView;


/**
 * Created by Raphael on 09/11/2015.
 */
public class FullScreenImageDialog extends Dialog {

    public FullScreenImageDialog(Context context, Drawable drawable) {
        super(context, R.style.Dialog_Fullscreen);
        PhotoView photoView = new PhotoView(context);
        photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setContentView(photoView);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        photoView.setImageDrawable(drawable);
    }
}
