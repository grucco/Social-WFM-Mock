package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by n.fiorillo on 02/03/2016.
 */
public class MonteOreTipo {

    private String id;
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String toString() {
        return getDescrizione();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class MonteOreTipoDeserializer implements JsonDeserializer<MonteOreTipo> {

        @Override
        public MonteOreTipo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MonteOreTipo mot = new MonteOreTipo();
            mot.setId(json.getAsJsonObject().get("Subty").getAsString());
            mot.setDescrizione(json.getAsJsonObject().get("Subtytext").getAsString());
            return mot;
        }
    }
}
