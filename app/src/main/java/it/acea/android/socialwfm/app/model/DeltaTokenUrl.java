package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by fabio on 18/11/15.
 */
@RealmClass
public class DeltaTokenUrl extends RealmObject {

    @PrimaryKey
    private long id;

    private String deltaUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeltaUrl() {
        return deltaUrl;
    }

    public void setDeltaUrl(String deltaUrl) {
        this.deltaUrl = deltaUrl;
    }
};