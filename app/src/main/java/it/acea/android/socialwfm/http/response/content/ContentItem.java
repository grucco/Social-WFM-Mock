package it.acea.android.socialwfm.http.response.content;

import android.content.Context;

import java.io.Serializable;

import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.http.response.utils.Support;

/**
 * Created by Raphael on 19/11/2015.
 */
public class ContentItem implements Serializable {

    private String Id;
    private String Name;
    private String ContentListItemType;
    private String ContentType;
    private String LastModifiedAt;
    private Support Folder;
    private Support ContentItem;

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

    public String getContentListItemType() {
        return ContentListItemType;
    }

    public void setContentListItemType(String contentListItemType) {
        ContentListItemType = contentListItemType;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getLastModifiedAt(Context context) {
        return Utils.dataRelativa(context, Long.parseLong(LastModifiedAt.replace("/Date(", "").replace(")/", "").trim()));
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        LastModifiedAt = lastModifiedAt;
    }

    public Support getFolder() {
        return Folder;
    }

    public void setFolder(Support folder) {
        Folder = folder;
    }

    public Support getContentItem() {
        return ContentItem;
    }

    public void setContentItem(Support contentItem) {
        ContentItem = contentItem;
    }
}
