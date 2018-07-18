package org.fingerlinks.mobile.android.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by nicola on 26/10/16.
 */

public class MyMultiAutoCompleteTextView extends MultiAutoCompleteTextView {
    public MyMultiAutoCompleteTextView(Context context) {
        super(context);
    }

    public MyMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateText(CharSequence text) {
        replaceText(text);
    }
}
