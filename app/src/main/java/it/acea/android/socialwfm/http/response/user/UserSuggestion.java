package it.acea.android.socialwfm.http.response.user;

import java.io.Serializable;

import it.acea.android.socialwfm.app.model.BeanAutocomplete;

/**
 * Created by Raphael on 10/12/2015.
 */
public class UserSuggestion implements Serializable, BeanAutocomplete{

    private String Id;
    private String FullName;
    private String Email;

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getFullName() {
        return FullName;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
