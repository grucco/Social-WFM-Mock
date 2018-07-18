package it.acea.android.socialwfm.http.user.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedEntries implements Serializable {

    @SerializedName("__deferred")
    @Expose
    private it.acea.android.socialwfm.http.user.bean.Deferred Deferred;

    /**
     * @return The Deferred
     */
    public it.acea.android.socialwfm.http.user.bean.Deferred getDeferred() {
        return Deferred;
    }

    /**
     * @param Deferred The __deferred
     */
    public void setDeferred(it.acea.android.socialwfm.http.user.bean.Deferred Deferred) {
        this.Deferred = Deferred;
    }

}
