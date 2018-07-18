package it.acea.android.socialwfm.app.events;

/**
 * Created by a.simeoni on 28/10/2016.
 */
public class GroupDetailOpened {
    private String groupId;
    private boolean isAdmin;
    private boolean isAuto;

    public GroupDetailOpened(String groupId, boolean isAdmin, boolean isAuto) {
        this.groupId = groupId;
        this.isAdmin = isAdmin;
        this.isAuto = isAuto;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public String getGroupId() {
        return groupId;
    }
    public boolean isAuto() {return isAuto;}
}
