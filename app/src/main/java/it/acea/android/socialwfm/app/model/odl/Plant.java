package it.acea.android.socialwfm.app.model.odl;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by fabio on 18/11/15.
 */
@RealmClass
public class Plant extends RealmObject {

    @PrimaryKey
    private String sedetec;
    private String anlage;
    private String ruoloutenza;
    private String descappar;
    private String scala;
    private String piano;
    private String interno;
    private String defsedetec;
    private String localita;
    private String municipio;
    private String cap;
    private String via;
    private String civico;
    private String provincia;
    private String ubicazione;
    private String categoria;
    private String profilo;
    private String longitude;
    private String latitude;
    private String precisione;

    private boolean fanghi;
    private boolean cloro;
    private boolean chimic;
    private boolean quadEl;
    private boolean elmecc;
    private boolean videosorv;
    private boolean misto;

    private String addressComplete;

    private String settIndRif;
    private String settIndFath;
    private String defsedetecFat;


    public boolean isMisto() {
        return misto;
    }

    public void setMisto(boolean misto) {
        this.misto = misto;
    }

    public String getSettIndRif() {
        return settIndRif;
    }

    public void setSettIndRif(String settIndRif) {
        this.settIndRif = settIndRif;
    }

    public String getSettIndFath() {
        return settIndFath;
    }

    public void setSettIndFath(String settIndFath) {
        this.settIndFath = settIndFath;
    }

    public String getDefsedetecFat() {
        return defsedetecFat;
    }

    public void setDefsedetecFat(String defsedetecFat) {
        this.defsedetecFat = defsedetecFat;
    }

    public String getAnlage() {
        return anlage;
    }

    public void setAnlage(String anlage) {
        this.anlage = anlage;
    }

    public String getSedetec() {
        return sedetec;
    }

    public void setSedetec(String sedetec) {
        this.sedetec = sedetec;
    }

    public String getRuoloutenza() {
        return ruoloutenza;
    }

    public void setRuoloutenza(String ruoloutenza) {
        this.ruoloutenza = ruoloutenza;
    }

    public String getDescappar() {
        return descappar;
    }

    public void setDescappar(String descappar) {
        this.descappar = descappar;
    }

    public String getScala() {
        return scala;
    }

    public void setScala(String scala) {
        this.scala = scala;
    }

    public String getPiano() {
        return piano;
    }

    public void setPiano(String piano) {
        this.piano = piano;
    }

    public String getInterno() {
        return interno;
    }

    public void setInterno(String interno) {
        this.interno = interno;
    }

    public String getDefsedetec() {
        return defsedetec;
    }

    public void setDefsedetec(String defsedetec) {
        this.defsedetec = defsedetec;
    }

    public String getLocalita() {
        return localita;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getUbicazione() {
        return ubicazione;
    }

    public void setUbicazione(String ubicazione) {
        this.ubicazione = ubicazione;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProfilo() {
        return profilo;
    }

    public void setProfilo(String profilo) {
        this.profilo = profilo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPrecisione() {
        return precisione;
    }

    public void setPrecisione(String precisione) {
        this.precisione = precisione;
    }

    public boolean isFanghi() {
        return fanghi;
    }

    public void setFanghi(boolean fanghi) {
        this.fanghi = fanghi;
    }

    public boolean isCloro() {
        return cloro;
    }

    public void setCloro(boolean cloro) {
        this.cloro = cloro;
    }

    public boolean isChimic() {
        return chimic;
    }

    public void setChimic(boolean chimic) {
        this.chimic = chimic;
    }

    public boolean isQuadEl() {
        return quadEl;
    }

    public void setQuadEl(boolean quadEl) {
        this.quadEl = quadEl;
    }

    public boolean isElmecc() {
        return elmecc;
    }

    public void setElmecc(boolean elmecc) {
        this.elmecc = elmecc;
    }

    public boolean isVideosorv() {
        return videosorv;
    }

    public void setVideosorv(boolean videosorv) {
        this.videosorv = videosorv;
    }

    public String getAddressComplete() {
        return addressComplete;
    }

    public void setAddressComplete(String addressComplete) {
        this.addressComplete = addressComplete;
    }
};