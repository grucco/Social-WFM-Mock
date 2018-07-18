package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by n.fiorillo on 21/04/2016.
 */
public class TimbraturaTipo {
    @SerializedName("Teventtype")
    String tipo;

    @SerializedName("TeventtypeText")
    String descrizione;

    public String getTipo() {
        return tipo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setTipo(String t) {
        this.tipo = t;
    }

    public void setDescrizione(String d) {
        this.descrizione = d;
    }

    @Override
    public String toString() {
        return descrizione;
    }

    public static TimbraturaTipo findByTipo(String tipo, List<TimbraturaTipo> list) {
        for (TimbraturaTipo t: list) {
            if (t.getTipo().equals(tipo)) return t;
        }
        return null;
    }
}
