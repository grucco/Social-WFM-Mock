package it.acea.android.socialwfm.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Raphael on 29/10/2015.
 */
@RealmClass
public class Notification extends RealmObject {

    @PrimaryKey
    private String Id;
    private String EventType;
    private String CreatedAt;
    private String LastModifiedAt;
    private String Message;
    private String Description;
    private boolean Read;
    private String AcceptText;
    private String RejectText;
    private String Category;
    private Profile Sender;
    private Group Group;
    private ObjectReference ObjectReference;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public String getAcceptText() {
        return AcceptText;
    }

    public void setAcceptText(String acceptText) {
        AcceptText = acceptText;
    }

    public String getRejectText() {
        return RejectText;
    }

    public void setRejectText(String rejectText) {
        RejectText = rejectText;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Profile getSender() {
        return Sender;
    }

    public void setSender(Profile sender) {
        Sender = sender;
    }

    public it.acea.android.socialwfm.app.model.Group getGroup() {
        return Group;
    }

    public void setGroup(it.acea.android.socialwfm.app.model.Group group) {
        Group = group;
    }

    public it.acea.android.socialwfm.app.model.ObjectReference getObjectReference() {
        return ObjectReference;
    }

    public void setObjectReference(it.acea.android.socialwfm.app.model.ObjectReference objectReference) {
        ObjectReference = objectReference;
    }
}
