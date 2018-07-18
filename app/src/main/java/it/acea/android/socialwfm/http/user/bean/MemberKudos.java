package it.acea.android.socialwfm.http.user.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MemberKudos implements Serializable {

    @SerializedName("__deferred")
    @Expose
    private Deferred Deferred;

    /**
     * @return The Deferred
     */
    public Deferred getDeferred() {
        return Deferred;
    }

    /**
     * @param Deferred The __deferred
     */
    public void setDeferred(Deferred Deferred) {
        this.Deferred = Deferred;
    }


}
