package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by fabio on 22/09/15.
 */
@RealmClass
public class User extends RealmObject {

    @PrimaryKey
    private String id;

    private String firstName;
    private String lastName;
    private String nickname;
    private String title;
    private String email;
    private String fullName;
    private String role;
    private boolean following;

    private String profilePhoto;
    private String feedEntries;

    private String oautToken;
    private String oautTokenSecret;
    private String verificationToken;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFeedEntries() {
        return feedEntries;
    }

    public void setFeedEntries(String feedEntries) {
        this.feedEntries = feedEntries;
    }

    public String getOautToken() {
        return oautToken;
    }

    public void setOautToken(String oautToken) {
        this.oautToken = oautToken;
    }

    public String getOautTokenSecret() {
        return oautTokenSecret;
    }

    public void setOautTokenSecret(String oautTokenSecret) {
        this.oautTokenSecret = oautTokenSecret;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Ignore
    public String nameLogo;

    public String getNameLogo() {

        nameLogo = (((getFirstName() != null) ? getFirstName().substring(0, 1) : "") + ((getLastName() != null) ? getLastName().substring(0, 1) : ""));

        return nameLogo;
    }
};