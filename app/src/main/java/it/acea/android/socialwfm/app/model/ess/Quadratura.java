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
 * Created by a.simeoni on 29/03/2016.
 */
public class Quadratura {

    private String Error;
    private String Etext;
    private String Pernr;
    private String Ename;
    private String Kurzt;
    private Date Ldate;

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    public String getEtext() {
        return Etext;
    }

    public void setEtext(String etext) {
        Etext = etext;
    }

    public String getPernr() {
        return Pernr;
    }

    public void setPernr(String pernr) {
        Pernr = pernr;
    }

    public String getEname() {
        return Ename;
    }

    public void setEname(String ename) {
        Ename = ename;
    }

    public String getKurzt() {
        return Kurzt;
    }

    public void setKurzt(String kurzt) {
        Kurzt = kurzt;
    }

    public Date getLdate() {
        return Ldate;
    }

    public void setLdate(Date ldate) {
        Ldate = ldate;
    }

    public static Comparator<Quadratura> QUADRATURA_COMPARATOR_ASC = new Comparator<Quadratura>() {
        @Override
        public int compare(Quadratura lhs, Quadratura rhs) {
            return lhs.getLdate().compareTo(rhs.getLdate());
        }
    };

    public static Comparator<Quadratura> QUADRATURA_COMPARATOR_DISC = new Comparator<Quadratura>() {
        @Override
        public int compare(Quadratura lhs, Quadratura rhs) {
            return (-1) * (lhs.getLdate().compareTo(rhs.getLdate()));
        }
    };

    public static class QuadraturaDeserializer implements JsonDeserializer<Quadratura> {

        @Override
        public Quadratura deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Quadratura quadratura = new Quadratura();
            quadratura.setEname(json.getAsJsonObject().get("Ename").getAsString());
            quadratura.setError(json.getAsJsonObject().get("Error").getAsString());
            quadratura.setEtext(json.getAsJsonObject().get("Etext").getAsString());
            quadratura.setKurzt(json.getAsJsonObject().get("Kurzt").getAsString());
            String ldateString = json.getAsJsonObject().get("Ldate").getAsString();
            quadratura.setLdate(Utils.cleanDateField(ldateString));
            quadratura.setPernr(json.getAsJsonObject().get("Pernr").getAsString());
            return quadratura;
        }
    }
}
