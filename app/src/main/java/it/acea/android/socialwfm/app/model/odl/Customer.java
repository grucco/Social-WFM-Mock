package it.acea.android.socialwfm.app.model.odl;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by fabio on 18/11/15.
 */
@RealmClass
public class Customer extends RealmObject {

    @PrimaryKey
    private String bp;
    private String fullName;
    private String tariftype;
    private String condcontract;
    private String ruoloutenza;
    private String condpagamento;
    private String detcosti;
    private String tiputenza;
    private String street;
    private String housenum;
    private String region;
    private String postcode;
    private String esigspecial;
    private String nome;
    private String cognome;
    private String mobphone;
    private String cellphone;
    private String fax;
    private String email;
    private String role;
    private String bpstatus;
    private String city;
    private String impscad;
    private String utencoin;
    private String impmor;
    private String dispAgg;
    private String fullAddress;

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getTariftype() {
        return tariftype;
    }

    public void setTariftype(String tariftype) {
        this.tariftype = tariftype;
    }

    public String getCondcontract() {
        return condcontract;
    }

    public void setCondcontract(String condcontract) {
        this.condcontract = condcontract;
    }

    public String getRuoloutenza() {
        return ruoloutenza;
    }

    public void setRuoloutenza(String ruoloutenza) {
        this.ruoloutenza = ruoloutenza;
    }

    public String getCondpagamento() {
        return condpagamento;
    }

    public void setCondpagamento(String condpagamento) {
        this.condpagamento = condpagamento;
    }

    public String getDetcosti() {
        return detcosti;
    }

    public void setDetcosti(String detcosti) {
        this.detcosti = detcosti;
    }

    public String getTiputenza() {
        return tiputenza;
    }

    public void setTiputenza(String tiputenza) {
        this.tiputenza = tiputenza;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHousenum() {
        return housenum;
    }

    public void setHousenum(String housenum) {
        this.housenum = housenum;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEsigspecial() {
        return esigspecial;
    }

    public void setEsigspecial(String esigspecial) {
        this.esigspecial = esigspecial;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getMobphone() {
        return mobphone;
    }

    public void setMobphone(String mobphone) {
        this.mobphone = mobphone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBpstatus() {
        return bpstatus;
    }

    public void setBpstatus(String bpstatus) {
        this.bpstatus = bpstatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImpscad() {
        return impscad;
    }

    public void setImpscad(String impscad) {
        this.impscad = impscad;
    }

    public String getUtencoin() {
        return utencoin;
    }

    public void setUtencoin(String utencoin) {
        this.utencoin = utencoin;
    }

    public String getImpmor() {
        return impmor;
    }

    public void setImpmor(String impmor) {
        this.impmor = impmor;
    }

    public String getDispAgg() {
        return dispAgg;
    }

    public void setDispAgg(String dispAgg) {
        this.dispAgg = dispAgg;
    }
};