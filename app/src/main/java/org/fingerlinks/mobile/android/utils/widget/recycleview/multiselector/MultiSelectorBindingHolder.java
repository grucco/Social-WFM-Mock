package org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector;

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;

/**
 * <p>A {@link android.support.v7.widget.RecyclerView.ViewHolder} that will automatically
 * bind itself to items in a {@link org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector.MultiSelector}.
 * This is like a {@link org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector.SwappingHolder}, but without
 * any background swapping. If you want to implement {@link org.fingerlinks.mobile.android.utils.widget.recycleview.multiselector.SelectableHolder},
 * this is usually the best place to start.</p>
 */
public abstract class MultiSelectorBindingHolder extends RebindReportingHolder implements SelectableHolder {
    private final MultiSelector mMultiSelector;

    public MultiSelectorBindingHolder(View itemView, MultiSelector multiSelector) {
        super(itemView);
        mMultiSelector = multiSelector;
    }

    @Override
    protected void onRebind() {
        mMultiSelector.bindHolder(this, getAdapterPosition(), getItemId());
    }
}
