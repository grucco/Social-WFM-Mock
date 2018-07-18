package it.acea.android.socialwfm.app.model.ess;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import it.acea.android.socialwfm.Utils;
import it.acea.android.socialwfm.http.response.ResponseManager;

/**
 * Created by n.fiorillo on 11/03/2016.
 */


/**
 * Il servizio delle timbrature ritorna sempre 7 entita (giorni)
 * Ogni giorno può contenere da 0 a n entita (orario)
 * Ogni orario può contenere 0 o 1 dettaglio
 */
public class Timbratura {
    private List<TimbraturaGiorno> giorni;
    private List<TimbraturaOrario> orari;

    public List<TimbraturaGiorno> getGiorni() {
        return giorni;
    }

    public List<TimbraturaOrario> getOrari() {
        if (orari == null) {
            orari = new ArrayList<>();
            for (TimbraturaGiorno giorno : giorni) {
                orari.addAll(giorno.getOrari());
            }
        }
        return orari;
    }

    public Timbratura(List<TimbraturaGiorno> giorni) {
        this.giorni = giorni;
    }


    public static class TimbraturaGiorno {
        Date giorno;
        String pianificato;
        String pianificatoInfo;
        ArrayList<TimbraturaOrario> orari;

        public Date getGiorno() {
            return giorno;
        }

        public String getGiornoString() {
            return Utils.dateFormat(giorno);
        }

        public void setGiorno(Date giorno) {
            this.giorno = giorno;
        }

        public String getPianificato() {
            return pianificato;
        }

        public void setPianificato(String pianificato) {
            this.pianificato = pianificato;
        }

        public String getPianificatoInfo() {
            return pianificatoInfo;
        }

        public void setPianificatoInfo(String pianificatoInfo) {
            this.pianificatoInfo = pianificatoInfo;
        }

        public ArrayList<TimbraturaOrario> getOrari() {
            return orari;
        }

        public void setOrari(ArrayList<TimbraturaOrario> orari) {
            this.orari = orari;
            build();
        }

        public TimbraturaGiorno() {
            this.orari = new ArrayList<>();
        }

        public void build() {
            for (TimbraturaOrario orario : orari) {
                orario.setGiorno(this);
            }
        }
    }

    public static class TimbraturaGiornoDeserializer implements JsonDeserializer<TimbraturaGiorno> {

        @Override
        public TimbraturaGiorno deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            TimbraturaGiorno tg = new TimbraturaGiorno();
            tg.setGiorno(Utils.cleanDateField(json.getAsJsonObject().get("Day").getAsString()));
            tg.setPianificato(json.getAsJsonObject().get("PlannedText").getAsString());
            tg.setPianificatoInfo(json.getAsJsonObject().get("PlannedInfo").getAsString());
            JsonElement orari = json.getAsJsonObject().get("Schedule").getAsJsonObject().get("results");
            ArrayList<TimbraturaOrario> orariList = context.deserialize(orari, ResponseManager.TYPE_TIMBRATURE_ORARI_LIST);
            tg.setOrari(orariList);
            return tg;
        }
    }

    /*

    Ogni orario ha un campo "ora" che è contenuto nel range (0, 23)
    La descrizione completa dell'ora è nel campo "oraDescrizione"

     */

    public static class TimbraturaOrario {
        TimbraturaGiorno giorno;
        String idRichesta;
        String descrizione;
        String info;
        String tipo;
        String tipoDescrizione;

        public Date getOrario() {
            return orario;
        }

        public void setOrario(Date orario) {
            this.orario = orario;
        }

        Date orario;
        String oraDescrizione;

        @Nullable
        TimbraturaDettaglio dettaglio;

        public static Comparator<TimbraturaOrario> TIMBRATURA_MENO_RECENTE = new Comparator<TimbraturaOrario>() {
            @Override
            public int compare(TimbraturaOrario lhs, TimbraturaOrario rhs) {
                return lhs.getGiorno().getGiorno().compareTo(rhs.getGiorno().getGiorno());
            }
        };

        public static Comparator<TimbraturaOrario> TIMBRATURA_PIU_RECENTE = new Comparator<TimbraturaOrario>() {
            @Override
            public int compare(TimbraturaOrario lhs, TimbraturaOrario rhs) {
                return (-1) * (lhs.getGiorno().getGiorno().compareTo(rhs.getGiorno().getGiorno()));
            }
        };

        @Nullable
        public TimbraturaDettaglio getDettaglio() {
            return dettaglio;
        }

        public void setGiorno(TimbraturaGiorno giorno) {
            this.giorno = giorno;
        }

        public void setDettaglio(TimbraturaDettaglio dettaglio) {
            this.dettaglio = dettaglio;
        }

        public String getOraDescrizione() {
            return oraDescrizione;
        }


        public TimbraturaGiorno getGiorno() {
            return giorno;
        }


        public void setOraDescrizione(String oraDescrizione) {
            this.oraDescrizione = oraDescrizione;
        }


        public String getTipoDescrizione() {
            return tipoDescrizione;
        }

        public void setTipoDescrizione(String tipoDescrizione) {
            this.tipoDescrizione = tipoDescrizione;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public void setDescrizione(String descrizione) {
            this.descrizione = descrizione;
        }

        public String getIdRichesta() {
            return idRichesta;
        }

        public void setIdRichesta(String idRichesta) {
            this.idRichesta = idRichesta;
        }

        public TimbraturaOrarioWeekEvent toTimbraturaOrarioWeekEvent(Context context) {
            return new TimbraturaOrarioWeekEvent(context, this);
        }
    }

    public static class TimbraturaOrarioDeserializer implements JsonDeserializer<TimbraturaOrario> {

        @Override
        public TimbraturaOrario deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            TimbraturaOrario to = new TimbraturaOrario();
            to.setIdRichesta(json.getAsJsonObject().get("Reqid").getAsString());
            to.setDescrizione(json.getAsJsonObject().get("ActText").getAsString());
            to.setInfo(json.getAsJsonObject().get("ActInfo").getAsString());
            to.setTipo(json.getAsJsonObject().get("ActKey").getAsString());
            to.setTipoDescrizione(json.getAsJsonObject().get("ActKeyText").getAsString());
            to.setOraDescrizione(json.getAsJsonObject().get("TimeText").getAsString());
            to.setOrario(Utils.getTimePTHMS(json.getAsJsonObject().get("Time").getAsString()));
            JsonElement dettaglio = json.getAsJsonObject().get("DetailRequest");
            TimbraturaDettaglio timbraturaDettaglio = context.deserialize(dettaglio, TimbraturaDettaglio.class);
            to.setDettaglio(timbraturaDettaglio);
            return to;
        }
    }

    public static class TimbraturaOrarioWeekEvent extends WeekViewEvent {
        private TimbraturaOrario timbraturaOrario;

        public TimbraturaOrarioWeekEvent(Context context, TimbraturaOrario timbraturaOrario) {
            this.timbraturaOrario = timbraturaOrario;
            Date giorno = timbraturaOrario.getGiorno().getGiorno();
            Date orario = timbraturaOrario.getOrario();
            DateTime giornoDT = new DateTime(giorno);
            DateTime orarioDT = new DateTime(orario);
            MutableDateTime mdt = new MutableDateTime(DateTimeZone.UTC);
            mdt.setDate(giornoDT);
            mdt.setHourOfDay(orarioDT.getHourOfDay());
            mdt.setMinuteOfHour(orarioDT.getMinuteOfHour());
            mdt.setSecondOfMinute(orarioDT.getSecondOfMinute());
            setName(timbraturaOrario.getDescrizione());
            setStartTime(mdt.toGregorianCalendar());
            mdt.addHours(1);
            setEndTime(mdt.toGregorianCalendar());
            setColor(ContextCompat.getColor(context, Utils.ESS_COLORS.valueOf(timbraturaOrario.getTipo()).getColorRes()));
        }

        public TimbraturaOrario getTimbraturaOrario() {
            return timbraturaOrario;
        }
    }

    public static class TimbraturaDettaglio {

        public static final String PREPARE_CREATE = "CREATE";
        public static final String PREPARE_EDIT = "EDIT";
        public static final String PREPARE_DELETE = "DELETE";
        public static final String PREPARE_SAVE_UPDATE = "SAVE_UPD";
        public static final String PREPARE_SAVE_DELETE = "SAVE_DEL";


        TimbraturaOrario timbraturaOrario;
        String idRichiesta;
        Date giorno;
        Date ora;
        String tipo;
        String tipoDescrizione;
        String stato;
        String approvatore;
        String nota;
        String idSocieta;
        String idCentroDiCosto;
        String motivo;
        String motivoDescrizione;
        String prepare;


        boolean isSocietaVisible;
        boolean isCentroDiCostoVisible;
        boolean isMotivoVisible;
        boolean isGiornoReadOnly;
        boolean isOraReadOnly;
        boolean isTipoReadOnly;
        boolean isApprovatoreReadOnly;
        boolean isMotivoReadOnly;
        boolean isNotaReadOnly;
        boolean isSocietaReadOnly;
        boolean isCentroDiCostoReadOnly;
        boolean isExternal;

        public TimbraturaDettaglio() {}

        public TimbraturaDettaglio(TimbraturaDettaglio old) {
            setIdRichiesta(old.getIdRichiesta());
            setGiorno(old.getGiorno());
            setOra(old.getOra());
            setTipo(old.getTipo());
            setTipoDescrizione(old.getTipoDescrizione());
            setStato(old.getStato());
            setApprovatore(old.getApprovatore());
            setNota(old.getNota());
            setIdSocieta(old.getIdSocieta());
            setIdCentroDiCosto(old.getIdCentroDiCosto());
            setMotivo(old.getMotivo());
            setMotivoDescrizione(old.getMotivoDescrizione());
            setPrepare(old.getPrepare());
            setSocietaVisible(old.isSocietaVisible());
            setCentroDiCostoVisible(old.isCentroDiCostoVisible());
            setMotivoVisible(old.isMotivoVisible());
            setGiornoReadOnly(old.isGiornoReadOnly());
            setOraReadOnly(old.isOraReadOnly());
            setTipoReadOnly(old.isTipoReadOnly());
            setApprovatoreReadOnly(old.isApprovatoreReadOnly());
            setMotivoReadOnly(old.isMotivoReadOnly());
            setNotaReadOnly(old.isNotaReadOnly());
            setSocietaReadOnly(old.isSocietaReadOnly());
            setCentroDiCostoReadOnly(old.isCentroDiCostoReadOnly());
            setExternal(old.isExternal());
        }

        public TimbraturaOrario getTimbraturaOrario() {
            return timbraturaOrario;
        }

        public void setTimbraturaOrario(TimbraturaOrario timbraturaOrario) {
            this.timbraturaOrario = timbraturaOrario;
        }

        public String getIdRichiesta() {
            return idRichiesta;
        }

        public void setIdRichiesta(String idRichiesta) {
            this.idRichiesta = idRichiesta;
        }

        public Date getGiorno() {
            return giorno;
        }

        public String getGiornoAsString() {
            return this.giorno != null ? Utils.dateFormat(this.getGiorno()) : "";
        }

        public void setGiorno(Date giorno) {
            this.giorno = giorno;
        }

        public Date getOra() {
            return ora;
        }

        public String getOraString() {
            return this.ora != null ? Utils.formatTimeToHHmm(this.getOra()) : "";
        }

        public void setOra(Date ora) {
            this.ora = ora;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getTipoDescrizione() {
            return tipoDescrizione;
        }

        public void setTipoDescrizione(String tipoDescrizione) {
            this.tipoDescrizione = tipoDescrizione;
        }

        public String getStato() {
            return stato;
        }

        public void setStato(String stato) {
            this.stato = stato;
        }

        public String getApprovatore() {
            return approvatore;
        }

        public void setApprovatore(String approvatore) {
            this.approvatore = approvatore;
        }

        public String getNota() {
            return nota;
        }

        public void setNota(String nota) {
            this.nota = nota;
        }

        public String getIdSocieta() {
            return idSocieta;
        }

        public void setIdSocieta(String idSocieta) {
            this.idSocieta = idSocieta;
        }

        public String getIdCentroDiCosto() {
            return idCentroDiCosto;
        }

        public void setIdCentroDiCosto(String idCentroDiCosto) {
            this.idCentroDiCosto = idCentroDiCosto;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }

        public String getMotivoDescrizione() {
            return motivoDescrizione;
        }

        public void setMotivoDescrizione(String motivoDescrizione) {
            this.motivoDescrizione = motivoDescrizione;
        }

        public String getPrepare() {
            return prepare;
        }

        public void setPrepare(String prepare) {
            this.prepare = prepare;
        }

        public boolean isSocietaVisible() {
            return isSocietaVisible;
        }

        public void setSocietaVisible(boolean societaVisible) {
            this.isSocietaVisible = societaVisible;
        }

        public boolean isCentroDiCostoVisible() {
            return isCentroDiCostoVisible;
        }

        public void setCentroDiCostoVisible(boolean centroDiCostoVisible) {
            this.isCentroDiCostoVisible = centroDiCostoVisible;
        }

        public boolean isMotivoVisible() {
            return isMotivoVisible;
        }

        public void setMotivoVisible(boolean motivoVisible) {
            this.isMotivoVisible = motivoVisible;
        }

        public boolean isGiornoReadOnly() {
            return isGiornoReadOnly;
        }

        public void setGiornoReadOnly(boolean giornoReadOnly) {
            isGiornoReadOnly = giornoReadOnly;
        }

        public boolean isOraReadOnly() {
            return isOraReadOnly;
        }

        public void setOraReadOnly(boolean oraReadOnly) {
            isOraReadOnly = oraReadOnly;
        }

        public boolean isTipoReadOnly() {
            return isTipoReadOnly;
        }

        public void setTipoReadOnly(boolean tipoReadOnly) {
            isTipoReadOnly = tipoReadOnly;
        }

        public boolean isApprovatoreReadOnly() {
            return isApprovatoreReadOnly;
        }

        public void setApprovatoreReadOnly(boolean approvatoreReadOnly) {
            isApprovatoreReadOnly = approvatoreReadOnly;
        }

        public boolean isMotivoReadOnly() {
            return isMotivoReadOnly;
        }

        public void setMotivoReadOnly(boolean motivoReadOnly) {
            isMotivoReadOnly = motivoReadOnly;
        }

        public boolean isNotaReadOnly() {
            return isNotaReadOnly;
        }

        public void setNotaReadOnly(boolean notaReadOnly) {
            isNotaReadOnly = notaReadOnly;
        }

        public boolean isSocietaReadOnly() {
            return isSocietaReadOnly;
        }

        public void setSocietaReadOnly(boolean societaReadOnly) {
            isSocietaReadOnly = societaReadOnly;
        }

        public boolean isCentroDiCostoReadOnly() {
            return isCentroDiCostoReadOnly;
        }

        public void setCentroDiCostoReadOnly(boolean centroDiCostoReadOnly) {
            isCentroDiCostoReadOnly = centroDiCostoReadOnly;
        }

        public boolean isExternal() {
            return isExternal;
        }

        public void setExternal(boolean external) {
            isExternal = external;
        }
    }

    public static class TimbraturaDettaglioDeserializer implements JsonDeserializer<TimbraturaDettaglio> {

        @Override
        public TimbraturaDettaglio deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            TimbraturaDettaglio td = new TimbraturaDettaglio();
            td.setIdRichiesta(json.getAsJsonObject().get("Reqid").getAsString());
            td.setGiorno(Utils.cleanDateField(json.getAsJsonObject().get("Day").getAsString()));
            td.setOra(Utils.getTimePTHMS(json.getAsJsonObject().get("Time").getAsString()));
            td.setTipo(json.getAsJsonObject().get("Tipokey").getAsString());
            td.setTipoDescrizione(json.getAsJsonObject().get("Tipotext").getAsString());
            td.setStato(json.getAsJsonObject().get("Status").getAsString());
            td.setApprovatore(json.getAsJsonObject().get("Approver").getAsString());
            td.setNota(json.getAsJsonObject().get("NotaCorr").getAsString());
            td.setIdSocieta(json.getAsJsonObject().get("Societa").getAsString());
            td.setIdCentroDiCosto(json.getAsJsonObject().get("Cdc").getAsString());
            td.setMotivo(json.getAsJsonObject().get("Motivokey").getAsString());
            td.setMotivoDescrizione(json.getAsJsonObject().get("Motivotext").getAsString());
            td.setPrepare(json.getAsJsonObject().get("IsPrepare").getAsString());
            td.setSocietaVisible(Utils.cleanBooleanField(json.getAsJsonObject().get("SocietaVisible").getAsString()));
            td.setCentroDiCostoVisible(Utils.cleanBooleanField(json.getAsJsonObject().get("CdcVisible").getAsString()));
            td.setMotivoVisible(Utils.cleanBooleanField(json.getAsJsonObject().get("MotivoVisible").getAsString()));
            td.setGiornoReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("DayReadonly").getAsString()));
            td.setOraReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("TimeReadonly").getAsString()));
            td.setTipoReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("TipokeyReadonly").getAsString()));
            td.setApprovatoreReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("ApproverReadonly").getAsString()));
            td.setMotivoReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("MotivokeyReadonly").getAsString()));
            td.setNotaReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("NotaCorrReadonly").getAsString()));
            td.setSocietaReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("SocietaReadonly").getAsString()));
            td.setCentroDiCostoReadOnly(Utils.cleanBooleanField(json.getAsJsonObject().get("CdcReadonly").getAsString()));
            td.setExternal(Utils.cleanBooleanField(json.getAsJsonObject().get("IsExternal").getAsString()));
            return td;
        }
    }

    public static class TimbraturaDettaglioSerializer implements JsonSerializer<TimbraturaDettaglio> {

        @Override
        public JsonElement serialize(TimbraturaDettaglio src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("Reqid", src.getIdRichiesta());
            object.addProperty("Day", Utils.dateToEdmDatetime(src.getGiorno()));
            object.addProperty("Time", Utils.timeToPTHMS(src.getOra()));
            object.addProperty("Tipokey", src.getTipo());
            object.addProperty("Tipotext", src.getTipoDescrizione());
            object.addProperty("Status", src.getStato());
            object.addProperty("Approver", src.getApprovatore());
            object.addProperty("NotaCorr", src.getNota());
            object.addProperty("Societa", src.getIdSocieta());
            object.addProperty("Cdc", src.getIdCentroDiCosto());
            object.addProperty("Motivokey", src.getMotivo());
            object.addProperty("Motivotext", src.getMotivoDescrizione());
            object.addProperty("IsPrepare", src.getPrepare());
            object.addProperty("SocietaVisible", Utils.booleanToABAP(src.isSocietaVisible()));
            object.addProperty("CdcVisible", Utils.booleanToABAP(src.isCentroDiCostoVisible()));
            object.addProperty("MotivoVisible", Utils.booleanToABAP(src.isMotivoVisible()));
            object.addProperty("DayReadonly", Utils.booleanToABAP(src.isGiornoReadOnly()));
            object.addProperty("TimeReadonly", Utils.booleanToABAP(src.isOraReadOnly()));
            object.addProperty("TipokeyReadonly", Utils.booleanToABAP(src.isTipoReadOnly()));
            object.addProperty("ApproverReadonly", Utils.booleanToABAP(src.isApprovatoreReadOnly()));
            object.addProperty("MotivokeyReadonly", Utils.booleanToABAP(src.isMotivoReadOnly()));
            object.addProperty("NotaCorrReadonly", Utils.booleanToABAP(src.isNotaReadOnly()));
            object.addProperty("SocietaReadonly", Utils.booleanToABAP(src.isSocietaReadOnly()));
            object.addProperty("CdcReadonly", Utils.booleanToABAP(src.isCentroDiCostoReadOnly()));
            object.addProperty("IsExternal", Utils.booleanToABAP(src.isExternal()));
            return object;
        }
    }
}
