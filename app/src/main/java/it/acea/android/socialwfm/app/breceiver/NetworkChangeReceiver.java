package it.acea.android.socialwfm.app.breceiver;

/**
 * Created by a.simeoni on 11/03/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.delegate.SWFMNetworkChangeEventDelegate;


public class NetworkChangeReceiver extends BroadcastReceiver {

    public SWFMNetworkChangeEventDelegate delegate;

    public NetworkChangeReceiver(SWFMNetworkChangeEventDelegate delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!Utils.checkInternetConnection(context)) {
            delegate.onNetworkDisconnected();
        } else {
            delegate.onNetworkAvailable();
        }
    }
}