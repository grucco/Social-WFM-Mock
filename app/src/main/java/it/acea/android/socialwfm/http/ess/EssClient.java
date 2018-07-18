package it.acea.android.socialwfm.http.ess;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.SimpleMiddleware;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.model.ess.CartellinoOrologio;
import it.acea.android.socialwfm.app.model.ess.Giustificativi;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.http.HttpClientRequest;


/**
 * Created by n.fiorillo on 04/03/2016.
 */
public class EssClient {

    public static final String ION_TAG = "ESS_ION";

    public enum TipoFileMimeType {
        JSON("application/json", "json"),
        PDF("application/pdf", "pdf"),
        EXCEL("application/vnd.ms-excel", "xls");

        private final String mimeType;
        private final String extension;

        TipoFileMimeType(final String mimeType, final String extension) {
            this.mimeType = mimeType;
            this.extension = extension;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public String getExtension() {
            return this.extension;
        }
    }

    public enum EssEntity {
        GIUSTIFICATIVI("GiustificativiSet"),
        GIUSTIFICATIVI_CALENDAR("GiustificativiCalendarSet"),
        GIUSTIFICATIVI_TYPE("GiustificativiTypesSet"),
        MONTEORE("MonteOreSet"),
        MONTEORE_TYPE("MonteOreTypesSet"),
        TIMBRATURE_MESSAGE("TimbratureMessagesSet"),
        TIMBRATURE("TimbratureSet"),
        TIMBRATURE_DETAIL("TimbratureDetailSet"),
        TIMBRATURE_MOTIVI("TimbratureReasonSet"),
        TIMBRATURE_TIPI("TimbratureTypesSet"),
        CARTELLINO_SELECTION("CartellinoSelectionSet"),
        REPERIBILITA("ReperibilitaSet"),
        REPERIBILITA_EXPORT("ReperibilitaExportSet"),
        PERSONAL_DATA("PersonalDataSet"),
        CEDOLINO("CedoliniSet"),
        CEDOLINO_FILE("CedoliniFileSet"),
        CUD("CudSet"),
        CUD_FILE("CudFileSet");


        private final String value;

        private EssEntity(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class IonEssMiddleware extends SimpleMiddleware {
        private Context context;

        public IonEssMiddleware(Context context) {
            this.context = context;
        }

        @Override
        public void onRequest(OnRequestData data) {
            super.onRequest(data);
            data.request.setHeader("Authorization", SharedPreferenceAdapter.getBasicAutheticationToken(context));
            data.request.setHeader("X-SMP-APPCID", SharedPreferenceAdapter.getAppCidRegistered(context));
        }
    }

    public static void setupEssIonClient(Context context) {

        Ion ion;
        try {

            ion = HttpClientRequest.getIonHttpsInstanceByTag(context, EssClient.ION_TAG);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            ion = Ion.getDefault(context);
            e.printStackTrace();
        }
        ion.getHttpClient().insertMiddleware(new EssClient.IonEssMiddleware(context));
        // Rimuove la gestione dei cookie
        // ion.getHttpClient().getMiddleware().remove(ion.getCookieMiddleware());
        Log.d(TAG, "Setup ess client");
    }


    public static synchronized Ion getEssIonInstance(Context context) {
        Ion ion = Ion.getInstance(context, ION_TAG);
        // Ion non aggiorna i cookie automaticamente
        // https://github.com/koush/ion/issues/501#issuecomment-76156529
        ion.getCookieMiddleware().reinit();
        return ion;
    }

    private static Uri.Builder getUrlBuilder(Context context) {
        String d = SharedPreferenceAdapter.getValueForKey(context,SharedPreferenceAdapter.SMP_ADMIN_BASE_URL);
        Uri.Builder builder = new Uri.Builder();
        builder
                .scheme("https")
                .authority(d)
                .appendPath("ess");
        return builder;
    }

    public static Uri.Builder getUrlBuilder(Context context, String nomeServizio) {
        return getUrlBuilder(context).appendPath(nomeServizio);
    }

    public static Uri.Builder getUrlBuilderFormatJson(Context context, String nomeServizio) {
        return getUrlBuilder(context, nomeServizio).appendQueryParameter("$format", "json");
    }

    public static Uri.Builder getUrlBuilderArgs(Context context, String nomeServizio, String query) {
        String s = String.format("%s(%s)", nomeServizio, query);
        return getUrlBuilder(context).appendEncodedPath(s);
    }

    public static Uri.Builder getUrlBuilderValue(Context context, String nomeServizio, String query) {
        return getUrlBuilderArgs(context, nomeServizio, query).appendEncodedPath("$value");
    }

    private static final String TAG = EssClient.class.getSimpleName();


    public static void fetchXCSRFToken(Context context, EssEntity entity, final FutureCallback<Response<JsonObject>> callback) {
        Uri.Builder builder = getUrlBuilder(context, entity.getValue());
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .addHeader("X-CSRF-Token", "Fetch")
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

    public static void createGiustificativo(final Context context, String csrfToken, final Giustificativi giustificativi,
                                            final FutureCallback<Response<JsonObject>> futureCallback) {
                String method = null;
                Uri.Builder uriBuilder = null;
                JsonObject jsonObject = new JsonObject();

                /* Prepare con create */
                if (giustificativi.getIsPrepare().equals(Giustificativi.IS_PREPARE_CREATE)) {
                    method = "POST";
                    jsonObject.addProperty("IsPrepare", Giustificativi.IS_PREPARE_CREATE);
                    uriBuilder = getUrlBuilder(context, EssEntity.GIUSTIFICATIVI.getValue());
                } else if (giustificativi.getIsPrepare().equals(Giustificativi.IS_PREPARE_EDIT)) {
                    method = "POST";
                    jsonObject.addProperty("IsPrepare", Giustificativi.IS_PREPARE_EDIT);
                    jsonObject.addProperty("RequestId", giustificativi.getRequestId());
                    uriBuilder = getUrlBuilder(context, EssEntity.GIUSTIFICATIVI.getValue());
                }

                else if (giustificativi.getRequestId().isEmpty()) {
                    method = "POST";
                    uriBuilder = getUrlBuilder(context, EssEntity.GIUSTIFICATIVI.getValue());
                } else {
                    method = "PUT";
                    String s = String.format("RequestId='%s'", giustificativi.getRequestId());
                    uriBuilder = getUrlBuilderArgs(context, EssEntity.GIUSTIFICATIVI.getValue(), s);
                }

                jsonObject.addProperty("Subty", giustificativi.getSubty());
                jsonObject.addProperty("Begda", giustificativi.getBegda());
                jsonObject.addProperty("Endda", giustificativi.getEndda());
                if (giustificativi.getBeginTime() != null)
                    jsonObject.addProperty("BeginTime", giustificativi.getBeginTime());
                if (giustificativi.getEndTime() != null)
                    jsonObject.addProperty("EndTime", giustificativi.getEndTime());
                if (giustificativi.getAttabsHours() != null)
                    jsonObject.addProperty("AttabsHours", giustificativi.getAttabsHours());
                jsonObject.addProperty("CurrNotice", giustificativi.getCurrNotice());

                if (!TextUtils.isEmpty(giustificativi.getRequestId()) || giustificativi.getIsPrepare().equals(Giustificativi.IS_PREPARE_EDIT)) {
                    if (!TextUtils.isEmpty(giustificativi.getDateSearch()))
                        jsonObject.addProperty("DateSearch", giustificativi.getDateSearch());
                    if (!giustificativi.getDocnr().equals(Giustificativi.EMPTY_DOC_NR))
                        jsonObject.addProperty("Docnr", giustificativi.getDocnr());
                }
                getEssIonInstance(context).build(context)
                        .load(method, uriBuilder.build().toString())
                        .setHeader("X-CSRF-Token", csrfToken)
                        .setHeader("Accept", "application/json")
                        .setJsonObjectBody(jsonObject)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(futureCallback);

    }

    public static void deleteGiustificativo(final Context context, final String csrfToken, final String id,
                                            final FutureCallback<Response<JsonObject>> futureCallback) {
                Uri.Builder builder = getUrlBuilder(context, EssEntity.GIUSTIFICATIVI.getValue());
                getEssIonInstance(context).build(context)
                        .load("DELETE", builder.build().toString() + "(RequestId='" + id + "')")
                        .setHeader("X-CSRF-Token", csrfToken)
                        .setHeader("Accept", "application/json")
                        .asJsonObject()
                        .withResponse()
                        .setCallback(futureCallback);
            }

    public static void fetchGiustificativiCalendarSet(Context context, Date data, FutureCallback<JsonObject> futureCallback) {
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.GIUSTIFICATIVI_CALENDAR.getValue());
        if (data != null) {
            String queryParams = String.format("Date eq datetime'%s'", Utils.dateToISOFormat(data));
            builder.appendQueryParameter("$filter", queryParams);
        }
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void fetchGiustificativiTypeSet(Context context, FutureCallback<JsonObject> futureCallback) {
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.GIUSTIFICATIVI_TYPE.getValue());
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public static void fetchGiustificativiSet(Context context, Date data, FutureCallback<Response<JsonObject>> futureCallback) {
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.GIUSTIFICATIVI.getValue());
        if (data != null) {
            String queryParams = String.format("Begda eq datetime'%s'", Utils.dateToISOFormat(data));
            builder.appendQueryParameter("$filter", queryParams);
        }
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static void fetchMonteOreList(Context context, String tipoMonteOreId, Date data, FutureCallback<Response<JsonObject>> callback) {
        //String url = "https://www.dropbox.com/s/pxnbdulap3uaqym/MonteOre1.json?dl=1";
        String queryParams = String.format("DeductBegin eq datetime'%s' and TimeType eq '%s'", Utils.dateToISOFormat(data), tipoMonteOreId);
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.MONTEORE.getValue());
        builder.appendQueryParameter("$filter", queryParams);
       getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }


    public static void fetchMonteOreTipoList(Context context, FutureCallback<Response<JsonObject>> callback) {
        //String url = "https://www.dropbox.com/s/rajzmumfh0yfbxx/MonteOreTypes.json?dl=1";
        String url = getUrlBuilderFormatJson(context, EssEntity.MONTEORE_TYPE.getValue()).build().toString();
        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchAnomalieQuadraturaSet(Context context, Date date, FutureCallback<Response<JsonObject>> callback) {
        Uri.Builder builder = getUrlBuilderFormatJson(context, "AnomalieQuadraturaSet");
        String queryParams = String.format("Ldate eq datetime'%s'", Utils.dateToISOFormat(date), Utils.dateToISOFormat(date));
        builder.appendQueryParameter("$filter", queryParams);
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchTimbratureMessaggioList(Context context, FutureCallback<Response<JsonObject>> callback) {
        //String url = "https://www.dropbox.com/s/ncktaqvshahbmk8/TimbratureMessages.json?dl=1";
        String url = getUrlBuilderFormatJson(context, EssEntity.TIMBRATURE_MESSAGE.getValue()).build().toString();
        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchTimbratura(Context context, Date data, FutureCallback<Response<JsonObject>> callback) {
        //String url = "https://www.dropbox.com/s/ts6thqq11p0fdzr/Timbrature.json?dl=1";
        String dateParams = String.format("Day eq datetime'%s'", Utils.dateToISOFormat(data));
        Uri.Builder uriBuilder = getUrlBuilderFormatJson(context, EssEntity.TIMBRATURE.getValue());
        uriBuilder.appendQueryParameter("$filter", dateParams);
        uriBuilder.appendQueryParameter("$expand", "Schedule/DetailRequest");
        String url = uriBuilder.build().toString();

        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchTimbraturaMotivo(Context context, FutureCallback<Response<JsonObject>> callback) {
        Uri.Builder uriBuilder = getUrlBuilderFormatJson(context, EssEntity.TIMBRATURE_MOTIVI.getValue());
        String url = uriBuilder.build().toString();
        getEssIonInstance(context)
                .build(context).load(url).asJsonObject().withResponse().setCallback(callback);
    }

    public static void fetchTimbraturaTipo(Context context, FutureCallback<Response<JsonObject>> callback) {
        Uri.Builder uriBuilder = getUrlBuilderFormatJson(context, EssEntity.TIMBRATURE_TIPI.getValue());
        String url = uriBuilder.build().toString();
        getEssIonInstance(context)
                .build(context).load(url).asJsonObject().withResponse().setCallback(callback);
    }

    public static void createTimbratura(Context context, String crsfToken,
                                        Timbratura.TimbraturaDettaglio timbraturaDettaglio,
                                        FutureCallback<Response<JsonObject>> callback) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timbratura.TimbraturaDettaglio.class,
                new Timbratura.TimbraturaDettaglioSerializer()).create();
        Uri.Builder uriBuilder = getUrlBuilder(context, EssEntity.TIMBRATURE_DETAIL.getValue());

        String url = uriBuilder.build().toString();
        getEssIonInstance(context).build(context)
                .load("POST", url)
                .setHeader("X-CSRF-Token", crsfToken)
                .setHeader("Accept", "application/json")
                .setJsonObjectBody(gson.toJsonTree((timbraturaDettaglio)).getAsJsonObject())
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }

    public static void modificaTimbratura(Context context, String crsfToken,
                                        Timbratura.TimbraturaDettaglio timbraturaDettaglio,
                                        FutureCallback<Response<JsonObject>> callback) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Timbratura.TimbraturaDettaglio.class,
                new Timbratura.TimbraturaDettaglioSerializer()).create();
        String url = null;
        String method = null;
        String prepare = timbraturaDettaglio.getPrepare();
        if (prepare.equals(Timbratura.TimbraturaDettaglio.PREPARE_EDIT) ||
                prepare.equals(Timbratura.TimbraturaDettaglio.PREPARE_DELETE)) {
            Uri.Builder uriBuilder = getUrlBuilder(context, EssEntity.TIMBRATURE_DETAIL.getValue());
            url = uriBuilder.build().toString();
            method = "POST";
        } else if (prepare.equals(Timbratura.TimbraturaDettaglio.PREPARE_SAVE_UPDATE) ||
                prepare.equals(Timbratura.TimbraturaDettaglio.PREPARE_SAVE_DELETE)) {
            String s = String.format("Reqid='%s'", timbraturaDettaglio.getIdRichiesta());
            Uri.Builder uriBuilder = getUrlBuilderArgs(context, EssEntity.TIMBRATURE_DETAIL.getValue(), s);
            url = uriBuilder.build().toString();
            method = "PUT";
        }

        getEssIonInstance(context).build(context)
                .load(method, url)
                .setHeader("X-CSRF-Token", crsfToken)
                .setHeader("Accept", "application/json")
                .setJsonObjectBody(gson.toJsonTree((timbraturaDettaglio)).getAsJsonObject())
                .asJsonObject()
                .withResponse()
                .setCallback(callback);
    }


    /**
     * @param interval è una enumeration. Vedi classe Constants
     *                 Constants.ESS_MONTEORE_INTERVAL_TYPE_ALL = "ALL";
     *                 Constants.ESS_MONTEORE_INTERVAL_TYPE_3MONTHS = "3MONTHS";
     *                 Constants.ESS_MONTEORE_INTERVAL_TYPE_6MONTHS = "6MONTHS";
     *                 Constants.ESS_MONTEORE_INTERVAL_TYPE_12MONTHS = "12MONTHS";
     **/
    public static void fetchCartellinoSelectionSetWithInterval(Context context, String interval, FutureCallback<Response<JsonObject>> futureCallback) {
        String url;
        url = getUrlBuilderFormatJson(context, EssEntity.CARTELLINO_SELECTION.getValue())
                    .appendQueryParameter("$filter", "Interval eq '" + interval + "'")
                    .build()
                    .toString();


        getEssIonInstance(context).build(context)
                .load("GET", url)
                .asJsonObject()
                .withResponse().setCallback(futureCallback);
    }

    public static void fetchCartellinoSelectionPdf(Context context, Date dataInizio, Date dataFine, FutureCallback<Response<File>> callback) {

        String url;
        url = CartellinoOrologio.getDownloadPdfUrl(context, dataInizio, dataFine);
        File downloaddir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String rootpath = downloaddir.getAbsolutePath();
        String filename = CartellinoOrologio.getDownloadedPdfFileName(dataInizio, dataFine);
        String accept = String.format("%s, %s", TipoFileMimeType.JSON.getMimeType(), TipoFileMimeType.PDF.getMimeType());
        // url = "https://dl.dropboxusercontent.com/u/8528394/Cartellino_orologio_10122015_15032016.pdf";

       getEssIonInstance(context).build(context)
                .load("GET", url)
                .setHeader("Accept",accept)
               // .setTimeout(10000)
                .write(new File(rootpath + "/" + filename))
                .withResponse()
                .setCallback(callback);

    }

    public static void fetchReperibilita(Context context, Date data, FutureCallback<Response<JsonObject>> callback) {
        String queryParams = String.format("Begda eq datetime'%s'", Utils.dateToISOFormat(data));
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.REPERIBILITA.getValue());
        builder.appendQueryParameter("$filter", queryParams);
        getEssIonInstance(context).build(context)
                .load(builder.build().toString())
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchReperibilitaFile(Context context, Date date, TipoFileMimeType tipo,
                                             FutureCallback<Response<File>> callback) {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String d = new SimpleDateFormat("MMM_yyyy", Locale.ITALIAN).format(date);
        String fileName = String.format("Reperibilita_%s.%s", d, tipo.getExtension());
        String accept = String.format("%s, %s", TipoFileMimeType.JSON.getMimeType(), tipo.getMimeType());
        String s = String.format("Begda=datetime'%s'", Utils.dateToISOFormat(date));
        String url = getUrlBuilderValue(context, EssEntity.REPERIBILITA_EXPORT.getValue(), s).build().toString();
        getEssIonInstance(context).build(context)
                .load(url)
                .setHeader("Accept", accept)
                .write(new File(downloadDir, fileName))
                .withResponse()
                .setCallback(callback);
    }

    public static void fetchDatiPersonali(Context context, FutureCallback<Response<JsonObject>> callback) {
        Uri.Builder builder = getUrlBuilderFormatJson(context, EssEntity.PERSONAL_DATA.getValue());
        builder.appendQueryParameter("$expand", "Address");
        String url = builder.build().toString();
        //String url = "https://www.dropbox.com/s/yv4jc5mlbmv520g/datipersonali.json?dl=1";
        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchCedoliniSet(Context context,int year,FutureCallback<Response<JsonObject>> callback){
        Uri.Builder builder = getUrlBuilderFormatJson(context,EssEntity.CEDOLINO.getValue());
        builder.appendQueryParameter("$filter","Anno eq '"+year+"'");
        String url = builder.build().toString();
        //url = "https://www.dropbox.com/s/a33vubxbbkoco2h/Cedolino.json?dl=1";
        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse().setCallback(callback);
    }
    public static void fetchCudSet(Context context,int year,FutureCallback<Response<JsonObject>> callback){
        Uri.Builder builder = getUrlBuilderFormatJson(context,EssEntity.CUD.getValue());
        builder.appendQueryParameter("$filter","Anno eq '"+year+"'");
        String url = builder.build().toString();

        //url = "https://www.dropbox.com/s/8lczj3ejbt4ko6c/CudSet.json?dl=1";
        getEssIonInstance(context).build(context)
                .load(url)
                .asJsonObject()
                .withResponse().setCallback(callback);
    }

    public static void fetchCedoliniFileSetValue(Context context,String fileName,FutureCallback<Response<File>> callback){
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String s = String.format("Filename='%s'", fileName);
        String url = getUrlBuilderValue(context, EssEntity.CEDOLINO_FILE.getValue(), s).build().toString();
        //url = "https://dl.dropboxusercontent.com/u/8528394/entity.pdf";
        getEssIonInstance(context).build(context)
                .load(url)
                .write(new File(downloadDir, fileName))
                .withResponse()
                .setCallback(callback);
    }
    public static void fetchCudFileSetValue(Context context,String fileName,FutureCallback<Response<File>> callback){
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String s = String.format("Filename='%s'", fileName);
        String url = getUrlBuilderValue(context, EssEntity.CUD_FILE.getValue(), s).build().toString();
        //url = "https://dl.dropboxusercontent.com/u/8528394/entity.pdf";
        getEssIonInstance(context).build(context)
                .load(url)
                .write(new File(downloadDir, fileName))
                .withResponse()
                .setCallback(callback);
    }
}
