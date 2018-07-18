package it.acea.android.socialwfm.app.model;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Raphael on 02/11/2015.
 */
@RealmClass
public class Group extends RealmObject {

    @PrimaryKey
    private String Id;
    private String Name;
    private String Description;
    private boolean IsActive;
    private String InvitePolicy;
    private boolean IsAdmin;
    private String GroupType;
    private String CreatedAt;
    private String LastModifiedAt;
    private String LastActivityAt;
    private String UploadPolicy;
    private int MembersCount;
    private boolean AutoGroup;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean isActive) {
        IsActive = isActive;
    }

    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getLastModifiedAt() {
        return LastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        LastModifiedAt = lastModifiedAt;
    }

    public String getLastActivityAt() {
        return LastActivityAt;
    }

    public void setLastActivityAt(String lastActivityAt) {
        LastActivityAt = lastActivityAt;
    }

    public int getMembersCount() {
        return MembersCount;
    }

    public void setMembersCount(int membersCount) {
        MembersCount = membersCount;
    }

    public boolean isAutoGroup() {
        return AutoGroup;
    }

    public void setAutoGroup(boolean autoGroup) {
        AutoGroup = autoGroup;
    }

    public boolean getIsAdmin() {
        return IsAdmin;
    }

    public String getInvitePolicy() {
        return TextUtils.isEmpty(InvitePolicy) ? "" : InvitePolicy;
    }

    public void setInvitePolicy(String invitePolicy) {
        InvitePolicy = invitePolicy;
    }

    public void setIsAdmin(boolean admin) {
        IsAdmin = admin;
    }

    public String getUploadPolicy() {
        return UploadPolicy;
    }

    public void setUploadPolicy(String uploadPolicy) {
        UploadPolicy = uploadPolicy;
    }

    public static boolean authUserCanUploadMedia(Group group, boolean follow) {
        /*
        * non copre il caso di internal_follower */
        return group.getIsAdmin() ||
                group.getUploadPolicy().equals("all") ||
                (group.getUploadPolicy().contains("followers") && follow);
    }

    public static boolean authUserCanInvite(Group group, boolean follow) {
        return group.getIsAdmin() || (!group.isAutoGroup() &&
                (group.getInvitePolicy().equals("all") ||
                (group.getInvitePolicy().equals("followers") && follow)));



    }
}
