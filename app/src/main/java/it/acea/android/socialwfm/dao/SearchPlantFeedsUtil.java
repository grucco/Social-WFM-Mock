package it.acea.android.socialwfm.dao;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.acea.android.socialwfm.app.delegate.SWFMSearchPlantFeedsDelegate;
import it.acea.android.socialwfm.http.HttpClientRequest;
import it.acea.android.socialwfm.http.response.search.SearchResponse;

/**
 * Created by a.simeoni on 19/10/2016.
 * Effettua una ricerca di tutti i feeds associati ad un impianto. Offre funzionalità di navigazione
 */

public class SearchPlantFeedsUtil {

    private Context context;
    private SWFMSearchPlantFeedsDelegate delegate;
    private String plantId;


    public SearchPlantFeedsUtil(Context context,String plantId){
        this.context = context;
        this.plantId = plantId;
    }

    public void loadPlantFeeds(){
        HttpClientRequest.executeRequestSearchPlantFeeds(context, plantId,this, new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                SWFMSearchPlantFeedsDelegate.SearchError err = null;
                List<SearchResponse> resultSet = null;

                if(e != null){
                    err = new SWFMSearchPlantFeedsDelegate.SearchError(SWFMSearchPlantFeedsDelegate.ErrorCodes.ERR_EXCEPTION,e.getMessage());
                }
                else if(result == null){
                    err = new SWFMSearchPlantFeedsDelegate.SearchError(SWFMSearchPlantFeedsDelegate.ErrorCodes.ERR_NULL_RESULT,"");
                }

                else{
                    try{
                        // Costruisco la lista
                        JsonElement root = result.getResult().getAsJsonObject("d").get("results");
                        Type listType = new TypeToken<List<SearchResponse>>() {}.getType();
                        resultSet = new Gson().fromJson(root, listType);
                        Collections.sort(resultSet, new Comparator<SearchResponse>() {
                            @Override
                            public int compare(SearchResponse lhs, SearchResponse rhs) {
                                return -1*lhs.getLastModifiedAt().compareTo(rhs.getLastModifiedAt());
                            }
                        });
                    }
                    catch (NullPointerException nex){
                        err = new SWFMSearchPlantFeedsDelegate.SearchError(SWFMSearchPlantFeedsDelegate.ErrorCodes.ERR_NULL_RESULT,"");
                    }
                }

                delegate.onSearchCompleted(err,resultSet);
            }
        });
    }

    public void setDelegate(SWFMSearchPlantFeedsDelegate delegate){
        this.delegate = delegate;
    }

}
