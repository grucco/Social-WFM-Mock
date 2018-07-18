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
 * Created by n.fiorillo on 11/03/2016.
 */

/**
 * TODO: La timbratura messaggio, è cliccabile ed effettua una chiamata all'entita timbratura
 */
public class TimbraturaMessaggio {

    private Date data;
    private String messaggio;

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public Date getData() {
        return data;
    }

    public String getDataString() {
        return Utils.dateFormat(this.data);
    }

    public void setData(Date data) {
        this.data = data;
    }

    public static class TimbraturaMessaggioDeserializer implements JsonDeserializer<TimbraturaMessaggio> {

        @Override
        public TimbraturaMessaggio deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            TimbraturaMessaggio tm = new TimbraturaMessaggio();
            tm.setData(Utils.cleanDateField(json.getAsJsonObject().get("Date").getAsString()));
            tm.setMessaggio(json.getAsJsonObject().get("MsgText").getAsString());
            return tm;
        }
    }

    public static Comparator<TimbraturaMessaggio> DATA_ASC = new Comparator<TimbraturaMessaggio>() {
        @Override
        public int compare(TimbraturaMessaggio lhs, TimbraturaMessaggio rhs) {

            return lhs.getData().compareTo(rhs.getData());
        }
    };

    public static Comparator<TimbraturaMessaggio> DATA_DESC = new Comparator<TimbraturaMessaggio>() {
        @Override
        public int compare(TimbraturaMessaggio lhs, TimbraturaMessaggio rhs) {

            return rhs.getData().compareTo(lhs.getData());
        }
    };
}
