package it.acea.android.socialwfm.http.response.search;

import android.content.Context;

import java.io.Serializable;
import java.util.Date;

import it.acea.android.socialwfm.Utils;

/**
 * Created by raphaelbussa on 11/01/16.
 */
public class SearchResponse implements Serializable {

    private ObjectReference ObjectReference;
    private int LikesCount = 0;
    private String ViewsCount;

    private String LastModifiedAt;

    public String getViewsCount() {
        return ViewsCount;
    }

    public void setViewsCount(String viewsCount) {
        ViewsCount = viewsCount;
    }

    public String getLastMotifiedAtRelative(Context context) {
        Date d = Utils.cleanDateField(getLastModifiedAt());
        return Utils.dataRelativa(context, d.getTime());
    }


    public it.acea.android.socialwfm.http.response.search.ObjectReference getObjectReference() {
        return ObjectReference;
    }

    public void setObjectReference(it.acea.android.socialwfm.http.response.search.ObjectReference objectReference) {
        ObjectReference = objectReference;
    }

    public void setLikesCount(int c){
        this.LikesCount = c;
    }

    public int getLikesCount(){
        return LikesCount;
    }

    public String getLastModifiedAt() {
        return LastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.LastModifiedAt = lastModifiedAt;
    }
}
