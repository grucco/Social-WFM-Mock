package it.acea.android.socialwfm.app.model.ess;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Date;

import it.acea.android.socialwfm.Utils;

/**
 * Created by n.fiorillo on 02/03/2016.
 */
public class MonteOre {
    private String tipo;
    private String descrizione;
    private Date inizioRiduzione;
    private Date fineRiduzione;
    private String pianificato;
    private String goduto;


    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPianificato() {
        return pianificato;
    }

    public void setPianificato(String pianificato) {
        this.pianificato = pianificato;
    }

    public String getGoduto() {
        return goduto;
    }

    public void setGoduto(String goduto) {
        this.goduto = goduto;
    }

    public Date getInizioRiduzione() {
        return this.inizioRiduzione;
    }

    public String getInizioRiduzioneString() {
        return (this.inizioRiduzione == null) ? "" : Utils.dateFormat(this.inizioRiduzione);
    }


    public void setInizioRiduzione(Date inizioRiduzione) {
        this.inizioRiduzione = inizioRiduzione;
    }

    public Date getFineRiduzione() {
        return this.fineRiduzione;
    }

    public String getFineRiduzioneString() {
        return (this.inizioRiduzione == null) ? "" : Utils.dateFormat(this.fineRiduzione);
    }

    public void setFineRiduzione(Date fineRiduzione) {
        this.fineRiduzione = fineRiduzione;
    }


    public static class MonteOreDeserializer implements JsonDeserializer<MonteOre> {

        @Override
        public MonteOre deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MonteOre m = new MonteOre();
            m.setTipo(json.getAsJsonObject().get("TimeType").getAsString());
            m.setDescrizione(json.getAsJsonObject().get("TimeTypeText").getAsString());
            m.setPianificato(json.getAsJsonObject().get("Entitle").getAsString());
            m.setGoduto(json.getAsJsonObject().get("DeductedReduced").getAsString());
            String d = json.getAsJsonObject().get("DeductBegin").getAsString();
            m.setInizioRiduzione(Utils.cleanDateField(d));
            d = json.getAsJsonObject().get("DeductEnd").getAsString();
            m.setFineRiduzione(Utils.cleanDateField(d));
            return m;
        }
    }

    public static Comparator<MonteOre> INIZIO_RIDUZIONE_ASC = new Comparator<MonteOre>() {
        @Override
        public int compare(MonteOre lhs, MonteOre rhs) {

            return lhs.getInizioRiduzione().compareTo(rhs.getInizioRiduzione());
        }
    };

    public static Comparator<MonteOre> INIZIO_RIDUZIONE_DESC = new Comparator<MonteOre>() {
        @Override
        public int compare(MonteOre lhs, MonteOre rhs) {
            return rhs.getInizioRiduzione().compareTo(lhs.getInizioRiduzione());
        }
    };

    public static Comparator<MonteOre> FINE_RIDUZIONE_ASC = new Comparator<MonteOre>() {
        @Override
        public int compare(MonteOre lhs, MonteOre rhs) {

            return lhs.getFineRiduzione().compareTo(rhs.getFineRiduzione());
        }
    };

    public static Comparator<MonteOre> FINE_RIDUZIONE_DESC = new Comparator<MonteOre>() {
        @Override
        public int compare(MonteOre lhs, MonteOre rhs) {
            return rhs.getFineRiduzione().compareTo(lhs.getFineRiduzione());
        }
    };
}
