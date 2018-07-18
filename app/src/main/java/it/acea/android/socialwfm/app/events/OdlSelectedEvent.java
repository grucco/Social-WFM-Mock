package it.acea.android.socialwfm.app.events;

/**
 * Created by a.simeoni on 19/10/2016.
 */

public class OdlSelectedEvent {
    private String plantId;
    public OdlSelectedEvent(String plantId){
        this.plantId = plantId;
    }
    public String getPlantId(){
        return plantId;
    }
}
