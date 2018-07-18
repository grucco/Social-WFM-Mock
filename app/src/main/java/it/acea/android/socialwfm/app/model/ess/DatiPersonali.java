package it.acea.android.socialwfm.app.model.ess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import it.acea.android.socialwfm.Utils;

/**
 * Created by n.fiorillo on 11/05/2016.
 */
public class DatiPersonali {

    private int numeroDiFigli;
    private Date dataDiNascita;
    private String lingua;
    private Date inizioStatoCivile;
    private String luogoDiNascita;
    private String cognome;
    private String titolo;
    private String paeseDiOrigine;
    private String cognomeAcquisito;
    private String provincia;
    private String nome;
    private String sesso;
    private String cittadinanza;
    private String statoCivile;
    private String gruppo;
    private String codiceFiscale;
    private List<Indirizzo> indirizzi;

    public int getNumeroDiFigli() {
        return numeroDiFigli;
    }

    public void setNumeroDiFigli(int numeroDiFigli) {
        this.numeroDiFigli = numeroDiFigli;
    }

    public Date getDataDiNascita() {
        return dataDiNascita;
    }

    public String getDataDiNascitaText() {
        return (dataDiNascita != null) ?  Utils.dateFormat(this.dataDiNascita) : "";
    }

    public void setDataDiNascita(Date dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public Date getInizioStatoCivile() {
        return inizioStatoCivile;
    }

    public String getInizioStatoCivileText() {
        return (inizioStatoCivile != null) ?  Utils.dateFormat(this.inizioStatoCivile) : "";

    }

    public void setInizioStatoCivile(Date inizioStatoCivile) {
        this.inizioStatoCivile = inizioStatoCivile;
    }

    public String getLuogoDiNascita() {
        return luogoDiNascita;
    }

    public void setLuogoDiNascita(String luogoDiNascita) {
        this.luogoDiNascita = luogoDiNascita;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getPaeseDiOrigine() {
        return paeseDiOrigine;
    }

    public void setPaeseDiOrigine(String paeseDiOrigine) {
        this.paeseDiOrigine = paeseDiOrigine;
    }

    public String getCognomeAcquisito() {
        return cognomeAcquisito;
    }

    public void setCognomeAcquisito(String cognomeAcquisito) {
        this.cognomeAcquisito = cognomeAcquisito;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getCittadinanza() {
        return cittadinanza;
    }

    public void setCittadinanza(String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    public String getStatoCivile() {
        return statoCivile;
    }

    public void setStatoCivile(String statoCivile) {
        this.statoCivile = statoCivile;
    }

    public String getGruppo() {
        return gruppo;
    }

    public void setGruppo(String gruppo) {
        this.gruppo = gruppo;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public List<Indirizzo> getIndirizzi() {
        return indirizzi;
    }

    public void setIndirizzi(List<Indirizzo> indirizzi) {
        this.indirizzi = indirizzi;
    }

    public static class DatiPersonaliDeserializer implements JsonDeserializer<DatiPersonali> {

        @Override
        public DatiPersonali deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            DatiPersonali dp = new DatiPersonali();
            dp.setNumeroDiFigli(Integer.parseInt(json.getAsJsonObject().get("Anzkd").getAsString()));
            String d = json.getAsJsonObject().get("Birthdate").getAsString();
            dp.setDataDiNascita(Utils.cleanDateField(d));
            dp.setLingua(json.getAsJsonObject().get("Language").getAsString());
            JsonElement a = json.getAsJsonObject().get("Famdt");
            if (!a.isJsonNull()) dp.setInizioStatoCivile(Utils.cleanDateField(a.getAsString()));
            dp.setLuogoDiNascita(json.getAsJsonObject().get("Gbort").getAsString());
            dp.setCognome(json.getAsJsonObject().get("Nachn").getAsString());
            dp.setTitolo(json.getAsJsonObject().get("Title").getAsString());
            dp.setPaeseDiOrigine(json.getAsJsonObject().get("Country").getAsString());
            dp.setCognomeAcquisito(json.getAsJsonObject().get("Name2").getAsString());
            dp.setProvincia(json.getAsJsonObject().get("Region").getAsString());
            dp.setNome(json.getAsJsonObject().get("Vorna").getAsString());
            dp.setSesso(json.getAsJsonObject().get("Sex").getAsString());
            dp.setCittadinanza(json.getAsJsonObject().get("Cittadinanza").getAsString());
            dp.setStatoCivile(json.getAsJsonObject().get("Statocivile").getAsString());
            dp.setGruppo(json.getAsJsonObject().get("Gruppo").getAsString());
            dp.setCodiceFiscale(json.getAsJsonObject().get("Perid").getAsString());

            JsonElement indirizzi = json.getAsJsonObject().get("Address").getAsJsonObject().get("results");
            List<Indirizzo> i = context.deserialize(indirizzi, new TypeToken<List<Indirizzo>>() {}.getType());
            dp.setIndirizzi(i);
            return dp;
        }
    }

    public static class Indirizzo {
        private String indirizzo;
        private String citta;
        private String cap;
        private String numeroDiTelefono;
        private String provinciaSigla;
        private String numeroCivico;
        private String tipologiaVia;
        private String palazzina;
        private String scala;
        private String regione;
        private boolean domicilioIsResidenza;
        private String intero;
        private String tipologiaIndirizzo;
        private String provincia;
        private String paese;
        private String domicilioIsResidenzaDescrizione;

        public String getIndirizzo() {
            return indirizzo;
        }

        public void setIndirizzo(String indirizzo) {
            this.indirizzo = indirizzo;
        }

        public String getCitta() {
            return citta;
        }

        public void setCitta(String citta) {
            this.citta = citta;
        }

        public String getCap() {
            return cap;
        }

        public void setCap(String cap) {
            this.cap = cap;
        }

        public String getNumeroDiTelefono() {
            return numeroDiTelefono;
        }

        public void setNumeroDiTelefono(String numeroDiTelefono) {
            this.numeroDiTelefono = numeroDiTelefono;
        }

        public String getProvinciaSigla() {
            return provinciaSigla;
        }

        public void setProvinciaSigla(String provinciaSigla) {
            this.provinciaSigla = provinciaSigla;
        }

        public String getNumeroCivico() {
            return numeroCivico;
        }

        public void setNumeroCivico(String numeroCivico) {
            this.numeroCivico = numeroCivico;
        }

        public String getTipologiaVia() {
            return tipologiaVia;
        }

        public void setTipologiaVia(String tipologiaVia) {
            this.tipologiaVia = tipologiaVia;
        }

        public String getPalazzina() {
            return palazzina;
        }

        public void setPalazzina(String palazzina) {
            this.palazzina = palazzina;
        }

        public String getScala() {
            return scala;
        }

        public void setScala(String scala) {
            this.scala = scala;
        }

        public String getRegione() {
            return regione;
        }

        public void setRegione(String regione) {
            this.regione = regione;
        }

        public boolean isDomicilioIsResidenza() {
            return domicilioIsResidenza;
        }

        public void setDomicilioIsResidenza(boolean domicilioIsResidenza) {
            this.domicilioIsResidenza = domicilioIsResidenza;
        }

        public String getIntero() {
            return intero;
        }

        public void setIntero(String intero) {
            this.intero = intero;
        }

        public String getTipologiaIndirizzo() {
            return tipologiaIndirizzo;
        }

        public void setTipologiaIndirizzo(String tipologiaIndirizzo) {
            this.tipologiaIndirizzo = tipologiaIndirizzo;
        }

        public String getProvincia() {
            return provincia;
        }

        public void setProvincia(String provincia) {
            this.provincia = provincia;
        }

        public String getPaese() {
            return paese;
        }

        public void setPaese(String paese) {
            this.paese = paese;
        }

        public String getDomicilioIsResidenzaDescrizione() {
            return domicilioIsResidenzaDescrizione;
        }

        public void setDomicilioIsResidenzaDescrizione(String domicilioIsResidenzaDescrizione) {
            this.domicilioIsResidenzaDescrizione = domicilioIsResidenzaDescrizione;
        }
    }

    public static class InidizzoDeserializer implements JsonDeserializer<Indirizzo> {

        @Override
        public Indirizzo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Indirizzo i = new Indirizzo();
            i.setIndirizzo(json.getAsJsonObject().get("Stras").getAsString());
            i.setCitta(json.getAsJsonObject().get("Ort01").getAsString());
            i.setCap(json.getAsJsonObject().get("Pstlz").getAsString());
            i.setNumeroDiTelefono(json.getAsJsonObject().get("Telnr").getAsString());
            i.setProvinciaSigla(json.getAsJsonObject().get("State").getAsString());
            i.setNumeroCivico(json.getAsJsonObject().get("Hsnmr").getAsString());
            i.setTipologiaVia(json.getAsJsonObject().get("ZazhdTpvia").getAsString());
            i.setPalazzina(json.getAsJsonObject().get("ZazhdPalaz").getAsString());
            i.setScala(json.getAsJsonObject().get("ZazhdScala").getAsString());
            i.setRegione(json.getAsJsonObject().get("ZazhdRegion").getAsString());
            i.setRegione(json.getAsJsonObject().get("ZazhdRegion").getAsString());
            boolean a = json.getAsJsonObject().get("ZazhdDomres").getAsString().toUpperCase().contains("S");
            i.setDomicilioIsResidenza(a);
            i.setIntero(json.getAsJsonObject().get("ZazhdInterno").getAsString());
            i.setTipologiaIndirizzo(json.getAsJsonObject().get("AddrType").getAsString());
            i.setProvincia(json.getAsJsonObject().get("Region").getAsString());
            i.setPaese(json.getAsJsonObject().get("Country").getAsString());
            i.setDomicilioIsResidenzaDescrizione(json.getAsJsonObject().get("DomicilioIsResidenza").getAsString());
            return i;
        }
    }
}
