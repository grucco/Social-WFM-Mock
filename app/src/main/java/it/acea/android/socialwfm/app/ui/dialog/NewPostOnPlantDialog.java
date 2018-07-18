package it.acea.android.socialwfm.app.ui.dialog;

import android.app.Activity;

/**
 * Created by a.simeoni on 21/10/2016.
 */

public class NewPostOnPlantDialog extends NewPostDialog {

    private String taggedText;
    private String taggedActivityDesc="";

    public NewPostOnPlantDialog(Activity activity, TYPE mType, String mId, String plantId, String activityDesc) {
        super(activity, mType, mId);
        if(activityDesc!=null){
            this.taggedActivityDesc = "#"+activityDesc.replace(" ","_").replace(".","_").toLowerCase();
        }
        this.taggedText = "#"+plantId.toLowerCase().replace(".","_");
    }

    protected String getPostText(){
        return this.text.getText().toString() + '\n'+this.taggedText + " "+this.taggedActivityDesc;
    }
}
