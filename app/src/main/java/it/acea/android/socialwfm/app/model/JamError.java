package it.acea.android.socialwfm.app.model;

import it.acea.android.socialwfm.http.response.feed.CheckError;

/**
 * Created by nicola on 19/10/16.
 */

public class JamError extends Exception {
    private CheckError.Error error;
    public JamError(CheckError.Error error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return error.getMessage().getValue();
    }
}
