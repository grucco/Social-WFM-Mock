package it.acea.android.socialwfm.http.response.group;

import java.io.Serializable;

/**
 * Created by Raphael on 16/11/2015.
 */
public class SearchGroup implements Serializable {

    private int FollowersCount;
    private String Description;
    private String CreatedAt;
    private ObjectReference ObjectReference;

    public int getFollowersCount() {
        return FollowersCount;
    }

    public void setFollowersCount(int followersCount) {
        FollowersCount = followersCount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public it.acea.android.socialwfm.http.response.group.ObjectReference getObjectReference() {
        return ObjectReference;
    }

    public void setObjectReference(it.acea.android.socialwfm.http.response.group.ObjectReference objectReference) {
        ObjectReference = objectReference;
    }
}
