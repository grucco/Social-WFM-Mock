package it.acea.android.socialwfm.http.response;


import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.acea.android.socialwfm.app.SharedPreferenceAdapter;
import it.acea.android.socialwfm.app.SocialWFMApplication;
import it.acea.android.socialwfm.app.events.EventPasswordExpired;
import it.acea.android.socialwfm.app.model.ess.CartellinoOrologio;
import it.acea.android.socialwfm.app.model.ess.CudCedolino;
import it.acea.android.socialwfm.app.model.ess.DatiPersonali;
import it.acea.android.socialwfm.app.model.ess.MonteOre;
import it.acea.android.socialwfm.app.model.ess.MonteOreTipo;
import it.acea.android.socialwfm.app.model.ess.Quadratura;
import it.acea.android.socialwfm.app.model.ess.Reperibilita;
import it.acea.android.socialwfm.app.model.ess.Timbratura;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMessaggio;
import it.acea.android.socialwfm.app.model.ess.TimbraturaMotivo;
import it.acea.android.socialwfm.app.model.ess.TimbraturaTipo;

class SWFMOdataServiceException extends Exception{
    String uri;
    String content;
    String username;
    int code;
    SWFMOdataServiceException(String username, int code, String uri, String content){
        this.username = username;
        this.code = code;
        this.uri = uri;
        this.content = content;
    }

    SWFMOdataServiceException(Throwable throwable, String username, int code, String uri, String content){
        super(throwable);
        this.username = username;
        this.code = code;
        this.uri = uri;
        this.content = content;
    }

    public String getMessage(){
        return  "USER: "+username+"\n"+
                "REQUEST_URI: "+ uri+"\n"+
                "RESPONSE_CODE: HTTP-"+ code+"\n"+
                "RESPONSE_CONTENT: "+content;
    }
}

public class ResponseManager {



    private static final String TAG = ResponseManager.class.getSimpleName();
    private Response<JsonObject> result;
    private Exception exception;
    private ArrayList<ErrorDetail> errorDetails;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(MonteOre.class, new MonteOre.MonteOreDeserializer())
            .registerTypeAdapter(Quadratura.class, new Quadratura.QuadraturaDeserializer())
            .registerTypeAdapter(MonteOreTipo.class, new MonteOreTipo.MonteOreTipoDeserializer())
            .registerTypeAdapter(CartellinoOrologio.class, new CartellinoOrologio.CartellinoOrologioDeserializer())
            .registerTypeAdapter(TimbraturaMessaggio.class, new TimbraturaMessaggio.TimbraturaMessaggioDeserializer())
            .registerTypeAdapter(Timbratura.TimbraturaGiorno.class, new Timbratura.TimbraturaGiornoDeserializer())
            .registerTypeAdapter(Timbratura.TimbraturaOrario.class, new Timbratura.TimbraturaOrarioDeserializer())
            .registerTypeAdapter(Timbratura.TimbraturaDettaglio.class, new Timbratura.TimbraturaDettaglioDeserializer())
            .registerTypeAdapter(Timbratura.TimbraturaDettaglio.class, new Timbratura.TimbraturaDettaglioSerializer())
            .registerTypeAdapter(Reperibilita.class, new Reperibilita.ReperibilitaDeserializer())
            .registerTypeAdapter(DatiPersonali.class, new DatiPersonali.DatiPersonaliDeserializer())
            .registerTypeAdapter(DatiPersonali.Indirizzo.class, new DatiPersonali.InidizzoDeserializer())
            .registerTypeAdapter(CudCedolino.class,new CudCedolino.CudCedolinoDeserializer())
            .create();

    public ResponseManager(Exception e, Response<JsonObject> result) throws Exception {

        Context context = SocialWFMApplication.getContext();
        logCrashliticsIfNecessary(context,result);

        /*this.exception = e;
        this.result = result;
        if (e != null) throw e;
        if (result == null) throw new Exception("No result error");

        int resultCode = result.getHeaders().code();

        if(resultCode == 401){
            EventBus.getDefault().post(new EventPasswordExpired());
            throw new Exception("Error");
        }

        if (resultCode == 400) {
            buildErrorList();
            throw new DataErrorExpection(getErrorDetails());
        }
        if (resultCode > 299 || resultCode < 200) {
            throw new Exception("Error");
        }*/
    }


    /*
    * TODO: http://benjiweber.co.uk/blog/2015/02/20/work-around-java-same-erasure-errors-with-lambdas/
    */
    public ResponseManager(Exception e, Response<File> response, boolean f) throws Exception {

        Context context = SocialWFMApplication.getContext();
        logCrashliticsIfNecessary(context,response);

        this.exception = e;
        if (e != null) throw e;

        int resultCode = response.getHeaders().code();

        if(resultCode == 401){
            EventBus.getDefault().post(new EventPasswordExpired());
            throw new Exception("Error");
        }

        if (response.getHeaders().code() == 400) {
            buildErrorList();
            throw new DataErrorExpection(getErrorDetails());
        }

        if (resultCode > 299 || resultCode < 200) {
            throw new Exception("Error");
        }

        if (response.getResult() == null) throw new Exception("No result error");
    }

    /**
     * Riporta un errore a crashlitics in base al codice di ritorno di una chiamata HTTP
     * Per problemi far riferimento
     * http://stackoverflow.com/questions/24336444/trouble-with-logging-my-data-with-crashlytics
     **/
    public static void logCrashliticsIfNecessary(Context context, Response response){
        String username = SharedPreferenceAdapter.getActualSWFMUsername(context);
        String uri;

        if (response == null) {
            //Crashlytics.logException(new SWFMOdataServiceException(username, 0, "None", "Result is null"));
            return;
        }
        int httpCode = response.getHeaders().code();


        if(response.getException() != null || (httpCode > 299 || httpCode < 200)){
            uri = response.getRequest().getUri().toString();
            String content = String.valueOf(response.getResult());
            Throwable cause;
            try{
                cause = response.getException().getCause();
            }
            catch (NullPointerException nex){
                cause = null;
            }
            Crashlytics.logException(new SWFMOdataServiceException(cause,username,httpCode,uri,content));
        }
    }

    private void buildErrorList() {
        JsonElement e = result.getResult().getAsJsonObject("error").getAsJsonObject("innererror").get("errordetails");
        errorDetails = GSON.fromJson(e, TYPE_ERROR_DETAIL_LIST);
    }

    public ArrayList<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    public <T> T getData(Type type) {
        JsonElement e = result.getResult().getAsJsonObject("d").get("results");
        return (T) GSON.fromJson(e, type);
    }

    public <T> T getDataD(Type type) {
        JsonElement e = result.getResult().getAsJsonObject("d");
        return (T) GSON.fromJson(e, type);
    }

    public static String getXcsfrToken(Exception e, Response<JsonObject> response) throws Exception {
        if (e != null) throw e;
        String xcsrfToken = response.getHeaders().getHeaders().get("x-csrf-token");
        if (xcsrfToken == null || xcsrfToken.equals("")) {
            throw new Exception("Nessun token");
        }
        return xcsrfToken;
    }

    public static String getXCSRFToken(Exception e, Response<JsonObject> response) throws Exception {
        new ResponseManager(e, response);
        if (response.getHeaders().code() != 200) throw new Exception("Error fetch CSRF Token");
        return response.getHeaders().getHeaders().get("x-csrf-token");
    }

    public static class ErrorDetail {
        public String code;
        public String message;

        @Override
        public String toString() {
            return message;
        }
    }

    public static class DataErrorExpection extends Exception {
        private ArrayList<ErrorDetail> errorDetails;

        public DataErrorExpection(ArrayList<ErrorDetail> errorDetails) {
            this.errorDetails = errorDetails;
        }

        public ArrayList<ErrorDetail> getErrorDetails() {
            return errorDetails;
        }
    }

    public static List<MonteOre> getListaMonteOre(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_MONTE_ORE_LIST);
    }

    public static List<Quadratura> getListaAnomalieQuadratura(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_ANOMALIE_QUADRATURA_LIST);
    }

    public static List<MonteOreTipo> getListaMonteOreTipo(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_MONTE_ORE_TIPO_LIST);
    }

    public static List<CartellinoOrologio> getListaCartellinoOrologio(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_CARTELLINO_OROLOGIO_LIST);
    }

    public static List<TimbraturaMessaggio> getListaTimbraturaMessaggio(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_TIMBRATURE_MESSAGGIO_LIST);
    }

    public static List<TimbraturaMotivo> getListaTimbraturaMotivo(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_TIMBRATURE_MOTIVI_LIST);
    }

    public static List<TimbraturaTipo> getListaTimbraturaTipo(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_TIMBRATURE_TIPI_LIST);
    }

    public static List<CudCedolino> getListaCudCedolino(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_CUD_CEDOLINO);
    }

    public static Timbratura.TimbraturaDettaglio createTimbratura(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getDataD(TYPE_TIMBRATURE_DETTAGLIO);
    }

    public static List<Reperibilita> getListaReperibilita(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_REPERIBILITA);
    }

    public static File getReperibilitaExportFile(Exception e, Response<File> result) throws Exception, DataErrorExpection {
        new ResponseManager(e, result, true);
        return result.getResult();
    }

    public static File getPDFCartellinoOrologio(Exception ex, Response<File> result) throws Exception, DataErrorExpection {
        ResponseManager manager = new ResponseManager(ex, result, true);
        return result.getResult();
    }

    public static File getPDFCudCedolino(Exception ex, Response<File> result) throws Exception, DataErrorExpection {
        ResponseManager manager = new ResponseManager(ex, result, true);
        return result.getResult();
    }

    public static Timbratura getTimbratura(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        List<Timbratura.TimbraturaGiorno> lista = new ResponseManager(e, result).getData(TYPE_TIMBRATURE_GIORNI_LIST);
        return new Timbratura(lista);
    }

    public static List<DatiPersonali> getDatiPersonali(Exception e, Response<JsonObject> result) throws Exception, DataErrorExpection {
        return new ResponseManager(e, result).getData(TYPE_DATI_PERSONALI);
    }

    /**
     * Type per i modelli
     **/
    public static final Type TYPE_ERROR_DETAIL_LIST = new TypeToken<List<ResponseManager.ErrorDetail>>() {
    }.getType();
    public static final Type TYPE_MONTE_ORE_LIST = new TypeToken<List<MonteOre>>() {
    }.getType();
    public static final Type TYPE_MONTE_ORE_TIPO_LIST = new TypeToken<List<MonteOreTipo>>() {
    }.getType();
    public static final Type TYPE_CARTELLINO_OROLOGIO_LIST = new TypeToken<List<CartellinoOrologio>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_MESSAGGIO_LIST = new TypeToken<List<TimbraturaMessaggio>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_GIORNI_LIST = new TypeToken<List<Timbratura.TimbraturaGiorno>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_ORARI_LIST = new TypeToken<List<Timbratura.TimbraturaOrario>>() {
    }.getType();
    public static final Type TYPE_ANOMALIE_QUADRATURA_LIST = new TypeToken<List<Quadratura>>() {
    }.getType();
    public static final Type TYPE_REPERIBILITA = new TypeToken<List<Reperibilita>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_MOTIVI_LIST = new TypeToken<List<TimbraturaMotivo>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_TIPI_LIST = new TypeToken<List<TimbraturaTipo>>() {
    }.getType();
    public static final Type TYPE_TIMBRATURE_DETTAGLIO = new TypeToken<Timbratura.TimbraturaDettaglio>() {
    }.getType();
    public static final Type TYPE_DATI_PERSONALI = new TypeToken<List<DatiPersonali>>() {
    }.getType();
    public static final Type TYPE_CUD_CEDOLINO = new TypeToken<List<CudCedolino>>() {
    }.getType();

}
