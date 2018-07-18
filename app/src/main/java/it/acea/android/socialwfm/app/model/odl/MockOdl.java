package it.acea.android.socialwfm.app.model.odl;

/**
 * Created by g.rucco on 15/06/2018.
 */

public class MockOdl {

    private String customKey;

    private String aufnr;
    private String vornr;
    private String pernr;
    private String odltype;
    private String taskdesc;
    private String Datetime;
    private String user;
    private boolean app;
    private boolean team;
    private boolean preposto;

    private String deltaStatus;
    private String status;

    private Customer customer;
    private Plant plant;

    public MockOdl(Odl data) {
        this.customKey = data.getCustomKey();
        this.aufnr = data.getAufnr();
        this.app = data.isApp();
        this.customer = data.getCustomer();
        this.Datetime = data.getDatetime();
        this.deltaStatus = data.getDeltaStatus();
        this.pernr = data.getPernr();
        this.vornr = data.getVornr();
        this.odltype = data.getOdltype();
        this.taskdesc = data.getTaskdesc();
        this.user = data.getUser();
        this.team = data.isTeam();
        this.preposto = data.isPreposto();
        this.status = data.getStatus();
        this.plant = data.getPlant();
    }

    public String getCustomKey() {
        return customKey;
    }

    public void setCustomKey(String customKey) {
        this.customKey = customKey;
    }

    public String getAufnr() {
        return aufnr;
    }

    public void setAufnr(String aufnr) {
        this.aufnr = aufnr;
    }

    public String getVornr() {
        return vornr;
    }

    public void setVornr(String vornr) {
        this.vornr = vornr;
    }

    public String getPernr() {
        return pernr;
    }

    public void setPernr(String pernr) {
        this.pernr = pernr;
    }

    public String getOdltype() {
        return odltype;
    }

    public void setOdltype(String odltype) {
        this.odltype = odltype;
    }

    public String getTaskdesc() {
        return taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        this.taskdesc = taskdesc;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isApp() {
        return app;
    }

    public void setApp(boolean app) {
        this.app = app;
    }

    public boolean isTeam() {
        return team;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

    public boolean isPreposto() {
        return preposto;
    }

    public void setPreposto(boolean preposto) {
        this.preposto = preposto;
    }

    public String getDeltaStatus() {
        return deltaStatus;
    }

    public void setDeltaStatus(String deltaStatus) {
        this.deltaStatus = deltaStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }


}
