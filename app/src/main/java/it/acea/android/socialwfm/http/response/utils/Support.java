package it.acea.android.socialwfm.http.response.utils;

import java.io.Serializable;

import it.acea.android.socialwfm.app.model.Creator;

/**
 * Created by Raphael on 04/11/2015.
 */
public class Support implements Serializable {

    private String Id;
    private String Title;
    private Creator Creator;

    public it.acea.android.socialwfm.app.model.Creator getCreator() {
        return Creator;
    }

    public void setCreator(it.acea.android.socialwfm.app.model.Creator creator) {
        Creator = creator;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
