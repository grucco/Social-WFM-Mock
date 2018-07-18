package it.acea.android.socialwfm.app.bean;

import java.io.Serializable;

/**
 * Created by Raphael on 24/11/2015.
 */
public class DocPath implements Serializable {

    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
