package it.acea.android.socialwfm.app.model.ess;

import android.content.Context;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;

/**
 * Created by a.simeoni on 03/03/2016.
 */
public class CartellinoOrologio {

    private static final String TAG = CartellinoOrologio.class.getSimpleName();
    private Date Begda; // Begin date
    private String Interval; //{ALL, 6MONTHS,12MONTHS,3MONTHS}
    private Date Endda; // End date
    private double Amount1;
    private double Amount2;
    private String Mode; // {PE,DT}

    public String getPdfUrl() {
        return PdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        PdfUrl = pdfUrl;
    }

    public static String getDownloadPdfUrlModel(Context context) {
        String url = "https://" + SharedPreferenceAdapter.getValueForKey(context,SharedPreferenceAdapter.SMP_ADMIN_BASE_URL) + "/ess/CartellinoSet";
        return url;
    }

    public static String getDownloadedPdfFileName(Date d1, Date d2) {
        return "Cartellino_orologio_" + Utils.dateFormatWithoutSlashes(d1) + "_" + Utils.dateFormatWithoutSlashes(d2) + ".pdf";
    }

    public static String getDownloadPdfUrl(Context context, Date datainizio, Date datafine) {
        String url = getDownloadPdfUrlModel(context);
        String tail = "(Begda=datetime'" + Utils.dateToISOFormat(datainizio) + "',Endda=datetime'" + Utils.dateToISOFormat(datafine) + "')/$value";
        url = url + tail;
        url = url.replaceAll(" ", "%20");
        return url;
    }

    private String PdfUrl;

    public Date getBegda() {
        return this.Begda;
    }

    public String getBegdaAsString() {
        return this.Begda != null ? Utils.dateFormat(this.Begda) : "";
    }

    public void setBegda(Date begda) {
        Begda = begda;
    }

    public String getInterval() {
        return Interval;
    }

    public void setInterval(String interval) {
        Interval = interval;
    }

    public Date getEndda() {
        return Endda;
    }

    public String getEnddaAsString() {
        return this.Endda != null ? Utils.dateFormat(this.Endda) : "";
    }

    public void setEndda(Date endda) {
        Endda = endda;
    }

    public double getAmount1() {
        return Amount1;
    }

    public void setAmount1(double amount1) {
        Amount1 = amount1;
    }

    public double getAmount2() {
        return Amount2;
    }

    public void setAmount2(double amount2) {
        Amount2 = amount2;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    @Override
    public String toString() {
        return getBegdaAsString() + " " + getEnddaAsString();
    }

    public static class CartellinoOrologioDeserializer implements JsonDeserializer<CartellinoOrologio> {

        @Override
        public CartellinoOrologio deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            CartellinoOrologio item = new CartellinoOrologio();
            item.setAmount1(json.getAsJsonObject().get("Amount1").getAsDouble());
            item.setAmount2(json.getAsJsonObject().get("Amount2").getAsDouble());
            String begStr = json.getAsJsonObject().get("Begda").getAsString();
            item.setBegda(Utils.cleanDateField(begStr));
            String endStr = json.getAsJsonObject().get("Endda").getAsString();
            item.setEndda(Utils.cleanDateField(endStr));
            item.setInterval(json.getAsJsonObject().get("Interval").getAsString());
            item.setMode(json.getAsJsonObject().get("Mode").getAsString());
            String pdfUrl = (json.getAsJsonObject().get("__metadata").getAsJsonObject().get("uri").getAsString());
            pdfUrl = pdfUrl.replace("CartellinoSelectionSet", "CartellinoSet");
            item.setPdfUrl(pdfUrl + "/$value");
            return item;
        }
    }
}
