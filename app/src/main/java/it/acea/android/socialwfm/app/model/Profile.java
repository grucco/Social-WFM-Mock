package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Raphael on 02/11/2015.
 */
@RealmClass
public class Profile extends RealmObject {

    @PrimaryKey
    private String Id;
    private String FirstName;
    private String LastName;
    private String Nickname;
    private String Title;
    private String Email;
    private String FullName;
    private String Role;
    private boolean IsFollowing;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public boolean isIsFollowing() {
        return IsFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        IsFollowing = isFollowing;
    }

}
