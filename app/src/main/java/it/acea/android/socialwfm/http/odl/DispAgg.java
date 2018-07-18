package it.acea.android.socialwfm.http.odl;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fabio on 17/11/15.
 */
public class DispAgg implements Serializable {

    private List<InnerDispAgg> results;

    public List<InnerDispAgg> getResults() {
        return results;
    }

    public void setResults(List<InnerDispAgg> results) {
        this.results = results;
    }
}
