package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import it.acea.android.socialwfm.Utils;

/**
 * Created by n.fiorillo on 13/04/2016.
 */
public class Reperibilita {
    Date dataInizio;
    Date oraInizio;
    Date oraFine;
    String tipo;
    String descrizioneTipo;

    public Date getDataInizio() {
        return dataInizio;
    }

    public String getDataInizioString() {
        return Utils.dateFormat(dataInizio);
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getOraInizio() {
        return oraInizio;
    }

    public String getOraInizioString() {
        return Utils.formatTimeToHHmm(oraInizio);
    }

    public void setOraInizio(Date oraInizio) {
        this.oraInizio = oraInizio;
    }

    public Date getOraFine() {
        return oraFine;
    }

    public String getOraFineString() {
        return Utils.formatTimeToHHmm(oraFine);
    }

    public void setOraFine(Date oraFine) {
        this.oraFine = oraFine;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescrizioneTipo() {
        return descrizioneTipo;
    }

    public void setDescrizioneTipo(String descrizioneTipo) {
        this.descrizioneTipo = descrizioneTipo;
    }

    @Override
    public int hashCode() {
        int result = dataInizio != null ? dataInizio.hashCode() : 0;
        result = 31 * result + (oraInizio != null ? oraInizio.hashCode() : 0);
        result = 31 * result + (oraFine != null ? oraFine.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (descrizioneTipo != null ? descrizioneTipo.hashCode() : 0);
        return result;
    }

    public static class ReperibilitaDeserializer implements JsonDeserializer<Reperibilita> {

        @Override
        public Reperibilita deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Reperibilita r = new Reperibilita();
            r.setDataInizio(Utils.cleanDateField(json.getAsJsonObject().get("Begda").getAsString()));
            r.setOraInizio(Utils.getTimePTHMS(json.getAsJsonObject().get("Beguz").getAsString()));
            r.setOraFine(Utils.getTimePTHMS(json.getAsJsonObject().get("Enduz").getAsString()));
            r.setTipo(json.getAsJsonObject().get("Stnby").getAsString());
            r.setDescrizioneTipo(json.getAsJsonObject().get("StnbyText").getAsString());
            return r;
        }
    }
}
