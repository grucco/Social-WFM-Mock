package it.acea.android.socialwfm.app.delegate;

/**
 * Created by a.simeoni on 11/03/2016.
 * <p/>
 * Vedi classe NetworkChangeReceiver
 */
public interface SWFMNetworkChangeEventDelegate {
    void onNetworkDisconnected();

    void onNetworkAvailable();
}
