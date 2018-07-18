package it.acea.android.socialwfm.http.response.group;

import java.io.Serializable;

import it.acea.android.socialwfm.app.model.BeanAutocomplete;

/**
 * Created by a.simeoni on 22/07/2016.
 */
public class GroupSuggestion implements Serializable, BeanAutocomplete{

    private String Id;
    private String Name;

    public void setName(String name) {
        Name = name;
    }

    public String getName(){
        return this.Name;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public String getId() {
        return Id;
    }
    @Override
    public String getFullName() {
        return getName();
    }
}
