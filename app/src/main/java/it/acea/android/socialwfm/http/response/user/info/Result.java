package it.acea.android.socialwfm.http.response.user.info;

import java.util.List;

/**
 * Created by Raphael on 04/11/2015.
 */
public class Result {

    private List<PhoneNumbers> results;

    public List<PhoneNumbers> getResults() {
        return results;
    }

    public void setResults(List<PhoneNumbers> results) {
        this.results = results;
    }
}
