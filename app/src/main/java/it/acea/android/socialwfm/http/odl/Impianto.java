package it.acea.android.socialwfm.http.odl;

import android.text.TextUtils;

import java.io.Serializable;

public class Impianto implements Serializable {

    private String Anlage;
    private String Sedetec;
    private String Ruoloutenza;
    private String Descappar;
    private String Scala;
    private String Piano;
    private String Interno;
    private String Defsedetec;
    private String Localita;
    private String Municipio;
    private String Cap;
    private String Via;
    private String Civico;
    private String Provincia;
    private String Ubicazione;
    private String Categoria;
    private String Profilo;
    private String Longitude;
    private String Latitude;
    private String Precisione;

    private String IsFanghi;
    private String IsCloro;
    private String IsChimic;
    private String IsQuadEl;
    private String IsElmecc;
    private String IsVideosorv;
    private String IsMisto;

    private String SettIndRif;
    private String SettIndFath;
    private String DefsedetecFat;


    public String getIsMisto() {
        return IsMisto;
    }

    public void setIsMisto(String isMisto) {
        IsMisto = isMisto;
    }

    public String getSettIndRif() {
        return SettIndRif;
    }

    public void setSettIndRif(String settIndRif) {
        SettIndRif = settIndRif;
    }

    public String getSettIndFath() {
        return SettIndFath;
    }

    public void setSettIndFath(String settIndFath) {
        SettIndFath = settIndFath;
    }

    public String getDefsedetecFat() {
        return DefsedetecFat;
    }

    public void setDefsedetecFat(String defsedetecFat) {
        DefsedetecFat = defsedetecFat;
    }

    public String getIsFanghi() {
        return IsFanghi;
    }

    public void setIsFanghi(String isFanghi) {
        IsFanghi = isFanghi;
    }

    public String getIsCloro() {
        return IsCloro;
    }

    public void setIsCloro(String isCloro) {
        IsCloro = isCloro;
    }

    public String getIsChimic() {
        return IsChimic;
    }

    public void setIsChimic(String isChimic) {
        IsChimic = isChimic;
    }

    public String getIsQuadEl() {
        return IsQuadEl;
    }

    public void setIsQuadEl(String isQuadEl) {
        IsQuadEl = isQuadEl;
    }

    public String getIsElmecc() {
        return IsElmecc;
    }

    public void setIsElmecc(String isElmecc) {
        IsElmecc = isElmecc;
    }

    public String getIsVideosorv() {
        return IsVideosorv;
    }

    public void setIsVideosorv(String isVideosorv) {
        IsVideosorv = isVideosorv;
    }

    public String getAnlage() {
        return Anlage;
    }

    public void setAnlage(String anlage) {
        Anlage = anlage;
    }

    public String getSedetec() {
        return Sedetec;
    }

    public void setSedetec(String sedetec) {
        Sedetec = sedetec;
    }

    public String getRuoloutenza() {
        return Ruoloutenza;
    }

    public void setRuoloutenza(String ruoloutenza) {
        Ruoloutenza = ruoloutenza;
    }

    public String getDescappar() {
        return Descappar;
    }

    public void setDescappar(String descappar) {
        Descappar = descappar;
    }

    public String getScala() {
        return Scala;
    }

    public void setScala(String scala) {
        Scala = scala;
    }

    public String getPiano() {
        return Piano;
    }

    public void setPiano(String piano) {
        Piano = piano;
    }

    public String getInterno() {
        return Interno;
    }

    public void setInterno(String interno) {
        Interno = interno;
    }

    public String getDefsedetec() {
        return Defsedetec;
    }

    public void setDefsedetec(String defsedetec) {
        Defsedetec = defsedetec;
    }

    public String getLocalita() {
        return Localita;
    }

    public void setLocalita(String localita) {
        Localita = localita;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
    }

    public String getCap() {
        return Cap;
    }

    public void setCap(String cap) {
        Cap = cap;
    }

    public String getVia() {
        return Via;
    }

    public void setVia(String via) {
        Via = via;
    }

    public String getCivico() {
        return Civico;
    }

    public void setCivico(String civico) {
        Civico = civico;
    }

    public String getProvincia() {
        return Provincia;
    }

    public void setProvincia(String provincia) {
        Provincia = provincia;
    }

    public String getUbicazione() {
        return Ubicazione;
    }

    public void setUbicazione(String ubicazione) {
        Ubicazione = ubicazione;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getProfilo() {
        return Profilo;
    }

    public void setProfilo(String profilo) {
        Profilo = profilo;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getPrecisione() {
        return Precisione;
    }

    public void setPrecisione(String precisione) {
        Precisione = precisione;
    }

    /**
     * @return
     */
    public String getAddressComplete() {
        String address = "";
        try {
            address += TextUtils.isEmpty(getVia()) ? "" : (getVia() + " ");
            address += TextUtils.isEmpty(getCivico()) ? "" : (getCivico() + ", ");
            address += TextUtils.isEmpty(getLocalita()) ? "" : (getLocalita() + " ");
            address += TextUtils.isEmpty(getProvincia()) ? "" : (getProvincia() + " ");
            address += TextUtils.isEmpty(getCap()) ? "" : getCap();
        } catch (Exception _ex) {
            address = null;
        }
        return address;
    }

    public boolean isGeoLocationComplete() {
        return ((!TextUtils.isEmpty(getLatitude()) &&
                !getLatitude().startsWith("*")) && (!TextUtils.isEmpty(getLongitude())
                && !getLongitude().startsWith("*")));
    }


};