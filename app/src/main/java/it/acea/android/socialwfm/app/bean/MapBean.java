package it.acea.android.socialwfm.app.bean;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import it.acea.android.socialwfm.app.SocialWFMApplication;

/**
 * Created by fabio on 18/11/15.
 */
public class MapBean implements Serializable, ClusterItem {

    private String Aufnr;
    private String latitude;
    private String longitude;
    private String address;
    private LatLng myPosition;

    private int markerPosition;


    public int getMarkerPosition() {
        return markerPosition;
    }

    public void setMarkerPosition(int markerPosition) {
        this.markerPosition = markerPosition;
    }

    public String getAufnr() {
        return Aufnr;
    }

    public void setAufnr(String aufnr) {
        Aufnr = aufnr;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Inizializza la posizione del bean, con reverse geocoding se necessario
     * */
    /*public void initPosition(){
        LatLng position = new LatLng(0,0);//default
        if (isGeoLocationComplete()) {
            position = new LatLng(Double.parseDouble(getLatitude()),
                                  Double.parseDouble(getLongitude()));
        } else {

            try {
                List<Address> lists = new Geocoder(SocialWFMApplication.instance,
                        Locale.getDefault()).getFromLocationName(getAddress(), 1);
                if (lists != null && !lists.isEmpty()) {
                    Address address = lists.get(0);
                    position = new LatLng(
                            address.getLatitude(),
                            address.getLongitude());
                }
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
        }
        this.latitude  = position.latitude+"";
        this.longitude = position.longitude+"";
        this.myPosition = position;
    }*/

    private LatLng getLatLngFromAddress(){
        LatLng position = new LatLng(0,0);//default
        try {
            List<Address> lists = new Geocoder(SocialWFMApplication.instance,
                    Locale.getDefault()).getFromLocationName(getAddress(), 1);
            if (lists != null && !lists.isEmpty()) {
                Address address = lists.get(0);
                position = new LatLng(
                        address.getLatitude(),
                        address.getLongitude());
            }
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
        return position;
    }

    private String getAddressFromLatLng(){
        Geocoder geocoder;
        List<Address> yourAddresses;
        geocoder = new Geocoder(SocialWFMApplication.instance, Locale.getDefault());
        String result = "";
        try{
            yourAddresses= geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            if (yourAddresses.size() > 0)
            {
                String yourAddress = yourAddresses.get(0).getAddressLine(0);
                String yourCity = yourAddresses.get(0).getAddressLine(1);
                String yourCountry = yourAddresses.get(0).getAddressLine(2);
                result += yourAddress+" "+yourCity+" "+yourCountry;

                if(yourAddress != null && !yourAddress.trim().equals("null")){
                    result += yourAddress;
                }
                if(yourCity != null && !yourCity.trim().equals("null")){
                    result += yourCity;
                }
                if(yourCountry != null && !yourCity.trim().equals("null")){
                    result += yourCountry;
                }

                result = result.replaceAll("null","");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

       return result;
    }

    public void initPositionAndAddress(){

        if(TextUtils.isEmpty(getAddress())){
            // Non c'è l'indirizzo
            if(isGeoLocationComplete()){
                // Ma ho e coordinate per recuperarlo
                setAddress(getAddressFromLatLng());
            }
        }
        else {
            if(!isGeoLocationComplete()){
                // Non ho coordinate ma ho l'indirizzo per recuperarle
                LatLng position = getLatLngFromAddress();
                this.latitude  = position.latitude+"";
                this.longitude = position.longitude+"";
                this.myPosition = position;
            }
        }
    }

    @Override
    public LatLng getPosition() {
        LatLng latLng = new LatLng(0.0, 0.0);
        try{
            if(isGeoLocationComplete()){
                latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return latLng;
    }

    public boolean hasDefaultLatLong(){
        return getPosition().latitude==0 && getPosition().longitude==0;
    }

    public boolean isGeoLocationComplete() {
        return ((!TextUtils.isEmpty(getLatitude()) &&
                !getLatitude().startsWith("*")) && (!TextUtils.isEmpty(getLongitude())
                && !getLongitude().startsWith("*")) );
    }
};