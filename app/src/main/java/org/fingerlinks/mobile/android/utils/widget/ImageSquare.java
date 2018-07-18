package org.fingerlinks.mobile.android.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Raphael on 10/11/2015.
 */
public class ImageSquare extends ImageView {

    public ImageSquare(Context context) {
        super(context);
    }

    public ImageSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSquare(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
