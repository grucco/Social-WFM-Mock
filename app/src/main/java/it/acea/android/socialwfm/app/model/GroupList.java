package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Raphael on 16/11/2015.
 */
public class GroupList extends RealmObject {

    @PrimaryKey
    private String Id;
    private int FollowersCount;
    private String Description;
    private String CreatedAt;
    private String Title;
    private boolean Mine;
    private boolean AutoGroup;
    private String GroupType;
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isAutoGroup() {
        return AutoGroup;
    }

    public void setAutoGroup(boolean autoGroup) {
        AutoGroup = autoGroup;
    }
    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }
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

    public boolean isMine() {
        return Mine;
    }

    public void setMine(boolean mine) {
        Mine = mine;
    }
}
