package it.acea.android.socialwfm.http.odl;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import it.acea.android.socialwfm.app.SocialWFMApplication;

public class ODLBean implements Serializable, ClusterItem {

    private String Aufnr;
    private String Vornr;
    private String Pernr;
    private String Odltype;
    private String Taskdesc;
    private String Datetime;
    private String IsApp;
    private String IsTeam;
    private Cliente Cliente;
    private Impianto Impianto;
    private String User;

    private String IsPreposto;


    /**
     * "" : Inserito
     * (in questo caso se la chiamata contiene il comando expand vengono restituite anche le altre entità)
     * "I" : Inserito
     * (in questo caso se la chiamata contiene il comando expand vengono restituite anche le altre entità)
     * "U": Aggiornato
     * (in questo caso se la chiamata contiene il comando expand  viene restituito solo l’entità dell’ordine
     * e non del cliente e impianto dato che non servono)
     * "D": Completato e  pertanto da eliminare dall’app
     * (in questo caso se la chiamata contiene il comando expand  viene restituita solo la chiave
     * dell’ordine e l’operazione, inutile popolare il resto dei campi dato che serve solo
     * per tener traccia della rimozione sul client )
     */
    private String DeltaStatus;
    private String Status;

    public String getIsPreposto() {
        return IsPreposto;
    }

    public void setIsPreposto(String isPreposto) {
        IsPreposto = isPreposto;
    }

    public String getDeltaStatus() {
        if (TextUtils.isEmpty(DeltaStatus)) {
            setDeltaStatus(Odlstatus.
                    DELTA_STATUS_INSERTERD.
                    getValue());
        }
        return DeltaStatus;
    }

    public void setDeltaStatus(String deltaStatus) {
        DeltaStatus = deltaStatus;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAufnr() {
        return Aufnr;
    }

    public void setAufnr(String aufnr) {
        Aufnr = aufnr;
    }

    public String getVornr() {
        return Vornr;
    }

    public void setVornr(String vornr) {
        Vornr = vornr;
    }

    public String getPernr() {
        return Pernr;
    }

    public void setPernr(String pernr) {
        Pernr = pernr;
    }

    public String getOdltype() {
        return Odltype;
    }

    public void setOdltype(String odltype) {
        Odltype = odltype;
    }

    public String getTaskdesc() {
        return Taskdesc;
    }

    public void setTaskdesc(String taskdesc) {
        Taskdesc = taskdesc;
    }

    public String getOra() {
        return Datetime;
    }

    public void setOra(String ora) {
        Datetime = ora;
    }

    public String getIsApp() {
        return IsApp;
    }

    public void setIsApp(String isApp) {
        IsApp = isApp;
    }

    public String getIsTeam() {
        return IsTeam;
    }

    public void setIsTeam(String isTeam) {
        IsTeam = isTeam;
    }

    public it.acea.android.socialwfm.http.odl.Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(it.acea.android.socialwfm.http.odl.Cliente cliente) {
        Cliente = cliente;
    }

    public it.acea.android.socialwfm.http.odl.Impianto getImpianto() {
        return Impianto;
    }

    public void setImpianto(it.acea.android.socialwfm.http.odl.Impianto impianto) {
        Impianto = impianto;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getFormattedHour() {

        /*PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" and ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" and ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();

        Period period = new Period(72, 24, 12, 0);*/


        return "";
    }

    @Override
    public LatLng getPosition() {
        LatLng position = null;
        if (getImpianto().isGeoLocationComplete()) {
            position = new LatLng(Double.parseDouble(getImpianto().getLatitude()),
                    Double.parseDouble(getImpianto().getLongitude()));
        } else {
            /**
             * Reverse geocoding information
             */
            try {
                List<Address> lists = new Geocoder(SocialWFMApplication.instance,
                        Locale.getDefault()).getFromLocationName(
                        getImpianto().getAddressComplete(), 1);
                if (lists != null && !lists.isEmpty()) {
                    Address address = lists.get(0);
                    position = new LatLng(
                            address.getLatitude(),
                            address.getLongitude());
                }
            } catch (Exception _ex) {
                position = null;
            }
        }
        return position;
    }

    public enum Odlstatus {
        DELTA_STATUS_INSERTERD("I"),
        DELTA_STATUS_UPDATED("U"),
        DELTA_STATUS_DELETED("D");

        private final String status;

        Odlstatus(String status) {
            this.status = status;
        }

        public String getValue() {
            return status;
        }

        public static Odlstatus fromString(String text) {
            if (text != null) {
                for (Odlstatus b : Odlstatus.values()) {
                    if (text.equalsIgnoreCase(b.getValue())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }
};