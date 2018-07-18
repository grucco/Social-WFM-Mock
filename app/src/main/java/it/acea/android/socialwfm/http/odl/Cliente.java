package it.acea.android.socialwfm.http.odl;

import android.text.TextUtils;

import java.io.Serializable;

public class Cliente implements Serializable {

    private String Bp;

    private String Tariftype;
    private String Condcontract;
    private String Ruoloutenza;
    private String Condpagamento;
    private String Detcosti;
    private String Tiputenza;
    private String Street;
    private String Housenum;
    private String Region;
    private String Postcode;
    private String Esigspecial;
    private String Nome;
    private String Cognome;
    private String Mobphone;
    private String Cellphone;
    private String Fax;
    private String Email;
    private String Role;
    private String Bpstatus;
    private String City;
    private String Impscad;
    private String Utencoin;
    private String Impmor;
    private DispAgg DispAgg;

    public it.acea.android.socialwfm.http.odl.DispAgg getDispAgg() {
        return DispAgg;
    }

    public void setDispAgg(it.acea.android.socialwfm.http.odl.DispAgg dispAgg) {
        DispAgg = dispAgg;
    }

    public String getTariftype() {
        return Tariftype;
    }

    public void setTariftype(String tariftype) {
        Tariftype = tariftype;
    }

    public String getCondcontract() {
        return Condcontract;
    }

    public void setCondcontract(String condcontract) {
        Condcontract = condcontract;
    }

    public String getImpmor() {
        return Impmor;
    }

    public void setImpmor(String impmor) {
        Impmor = impmor;
    }

    public String getBp() {
        return TextUtils.isEmpty(Bp) ? "--" : Bp;
    }

    public void setBp(String bp) {
        Bp = bp;
    }

    public String getRuoloutenza() {
        return Ruoloutenza;
    }

    public void setRuoloutenza(String ruoloutenza) {
        Ruoloutenza = ruoloutenza;
    }

    public String getCondpagamento() {
        return Condpagamento;
    }

    public void setCondpagamento(String condpagamento) {
        Condpagamento = condpagamento;
    }

    public String getDetcosti() {
        return Detcosti;
    }

    public void setDetcosti(String detcosti) {
        Detcosti = detcosti;
    }

    public String getTiputenza() {
        return Tiputenza;
    }

    public void setTiputenza(String tiputenza) {
        Tiputenza = tiputenza;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getHousenum() {
        return Housenum;
    }

    public void setHousenum(String housenum) {
        Housenum = housenum;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getEsigspecial() {
        return Esigspecial;
    }

    public void setEsigspecial(String esigspecial) {
        Esigspecial = esigspecial;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String cognome) {
        Cognome = cognome;
    }

    public String getMobphone() {
        return Mobphone;
    }

    public void setMobphone(String mobphone) {
        Mobphone = mobphone;
    }

    public String getCellphone() {
        return Cellphone;
    }

    public void setCellphone(String cellphone) {
        Cellphone = cellphone;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String fax) {
        Fax = fax;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getBpstatus() {
        return Bpstatus;
    }

    public void setBpstatus(String bpstatus) {
        Bpstatus = bpstatus;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getImpscad() {
        return Impscad;
    }

    public void setImpscad(String impscad) {
        Impscad = impscad;
    }

    public String getUtencoin() {
        return Utencoin;
    }

    public void setUtencoin(String utencoin) {
        Utencoin = utencoin;
    }

    /**
     * @return
     */
    public String getFullAddress() {
        String address = "";

        address += (TextUtils.isEmpty(getStreet())) ? "" : (getStreet() + " ");
        address += (TextUtils.isEmpty(getHousenum())) ? "" : (getHousenum() + ", ");
        address += (TextUtils.isEmpty(getPostcode())) ? "" : getPostcode();

        return address;
    }

    public String getFullName() {
        String fullname = "";

        fullname += (TextUtils.isEmpty(getNome())) ? "" : (getNome() + " ");
        fullname += (TextUtils.isEmpty(getCognome())) ? "" : getCognome();


        return fullname;
    }
};