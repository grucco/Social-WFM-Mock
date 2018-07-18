package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Raphael on 30/11/2015.
 */
public class ObjectReference extends RealmObject {

    @PrimaryKey
    private String Id;
    private String Type;
    private String Title;
    private String ODataURL;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getODataURL() {
        return ODataURL;
    }

    public void setODataURL(String ODataURL) {
        this.ODataURL = ODataURL;
    }
}
