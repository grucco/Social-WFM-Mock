package it.acea.android.socialwfm.http.response.feed;

import java.util.List;

import it.acea.android.socialwfm.app.model.Profile;

/**
 * Created by Raphael on 10/12/2015.
 */
public class AtMantions {

    private List<Profile> results;

    public List<Profile> getResults() {
        return results;
    }

    public void setResults(List<Profile> results) {
        this.results = results;
    }
}
