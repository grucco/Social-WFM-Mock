package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by a.simeoni on 20/05/2016.
 * Questa classe rappresenta 2 entità: CUD e Cedolino
 * E' stata utilizzata una sola classe poichè le 2 entità differiscono solamente per il campo
 * MonthText e vengono presentate nella stessa tabella
 */
public class CudCedolino {
    private static final String TAG = CudCedolino.class.getSimpleName();

    private String Anno;
    private String MonthText;
    private String Societa;
    private String MatPayroll;
    private String Filename;

    public String getAnno() {
        return Anno;
    }

    public void setAnno(String anno) {
        Anno = anno;
    }

    public String getMonthText() {
        return (MonthText!=null)?MonthText:"-";
    }

    public void setMonthText(String monthText) {
        MonthText = monthText;
    }

    public String getSocieta() {
        return Societa;
    }

    public void setSocieta(String societa) {
        Societa = societa;
    }

    public String getMatPayroll() {
        return MatPayroll;
    }

    public void setMatPayroll(String matPayroll) {
        MatPayroll = matPayroll;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public static class CudCedolinoDeserializer implements JsonDeserializer<CudCedolino> {

        @Override
        public CudCedolino deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CudCedolino item = new CudCedolino();
            item.setAnno(json.getAsJsonObject().get("Anno").getAsString());
            item.setFilename(json.getAsJsonObject().get("Filename").getAsString());
            item.setMatPayroll(json.getAsJsonObject().get("MatPayroll").getAsString());

            String month = (json.getAsJsonObject().get("MonthText")!=null)?json.getAsJsonObject().get("MonthText").getAsString():null;
            item.setMonthText(month);
            item.setSocieta(json.getAsJsonObject().get("Societa").getAsString());
            return item;
        }
    }
}
