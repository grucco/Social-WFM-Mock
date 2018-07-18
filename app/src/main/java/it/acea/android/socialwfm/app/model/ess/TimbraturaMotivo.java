package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by n.fiorillo on 20/04/2016.
 */
public class TimbraturaMotivo {
    @SerializedName("AttAbsReason")
    private String id;

    @SerializedName("AttAbsReasonText")
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String toString() {
        return getDescrizione();
    }

    public static TimbraturaMotivo findById(String id, List<TimbraturaMotivo> list) {
        for (TimbraturaMotivo t: list) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }
}
