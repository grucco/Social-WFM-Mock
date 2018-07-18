package it.acea.android.socialwfm.http.response.search;

import java.io.Serializable;

/**
 * Created by raphaelbussa on 11/01/16.
 */
public class ObjectReference implements Serializable {

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
