package it.acea.android.socialwfm.app.delegate;

import java.util.List;

import it.acea.android.socialwfm.http.response.search.SearchResponse;

/**
 * Created by a.simeoni on 19/10/2016.
 */

public interface SWFMSearchPlantFeedsDelegate {

    public enum ErrorCodes{
        ERR_EXCEPTION,ERR_NULL_RESULT,ERR_API;
    };

    public class SearchError{
        private ErrorCodes code;
        private String message = "";
        public SearchError(ErrorCodes errorCode, String message){
            this.code = errorCode;
            this.message = message;
        }
        public String getMessage(){
            return this.message;
        }
        public ErrorCodes getCode(){return code;}
    };

    public void onSearchCompleted(SearchError error, List<SearchResponse> resultSet);
}
