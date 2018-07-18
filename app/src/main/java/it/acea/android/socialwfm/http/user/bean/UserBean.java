package it.acea.android.socialwfm.http.user.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserBean implements Serializable {

    @SerializedName("d")
    @Expose
    private D d;

    /**
     * @return The d
     */
    public D getD() {
        return d;
    }

    /**
     * @param d The d
     */
    public void setD(D d) {
        this.d = d;
    }

};