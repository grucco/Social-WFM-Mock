package it.acea.android.socialwfm.http.response.group;

import java.io.Serializable;

/**
 * Created by Raphael on 16/11/2015.
 */
public class ObjectReference implements Serializable {

    private String Id;
    private String Title;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
