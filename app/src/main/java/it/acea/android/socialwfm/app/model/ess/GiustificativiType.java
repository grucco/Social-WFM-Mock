package it.acea.android.socialwfm.app.model.ess;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by raphaelbussa on 17/03/16.
 */
public class GiustificativiType extends RealmObject {

    @PrimaryKey
    private String Subty;
    private String NoPartialDay;
    private String Subtytext;
    private String OnlySingleDay;

    public String getSubty() {
        return Subty;
    }

    public void setSubty(String subty) {
        Subty = subty;
    }

    public String getNoPartialDay() {
        return NoPartialDay;
    }

    public void setNoPartialDay(String noPartialDay) {
        NoPartialDay = noPartialDay;
    }

    public String getSubtytext() {
        return Subtytext;
    }

    public void setSubtytext(String subtytext) {
        Subtytext = subtytext;
    }

    public String getOnlySingleDay() {
        return OnlySingleDay;
    }

    public void setOnlySingleDay(String onlySingleDay) {
        OnlySingleDay = onlySingleDay;
    }
}
