package it.acea.android.socialwfm.app.events;

/**
 * Created by nicola on 08/11/16.
 */
public class FeedDeletedEvent {
    private int mPosition;
    public FeedDeletedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
