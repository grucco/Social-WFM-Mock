package it.acea.android.socialwfm.app.events;

import it.acea.android.socialwfm.app.model.Group;

/**
 * Created by nicola on 18/10/16.
 */
public class FollowGroupEvent {
    public Group group;
    public boolean follow;

    public FollowGroupEvent(Group group, boolean follow) {
        this.group = group;
        this.follow = follow;
    }
}