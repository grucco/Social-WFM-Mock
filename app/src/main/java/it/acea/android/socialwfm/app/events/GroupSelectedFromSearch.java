package it.acea.android.socialwfm.app.events;

/**
 * Created by a.simeoni on 22/07/2016.
 */
public class GroupSelectedFromSearch {

    private String Id;

    public GroupSelectedFromSearch(String id){
        this.Id = id;
    }

    public String getId() {
        return Id;
    }
}
