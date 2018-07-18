package it.acea.android.socialwfm.http.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Response;

import it.acea.android.socialwfm.app.model.JamError;
import it.acea.android.socialwfm.http.response.feed.CheckError;

/**
 * Created by nicola on 25/10/16.
 */

public class CheckErrorInJamResponse {


    public static void check(Exception e, Object result) throws Exception {
        if (e != null) throw e;
        if (result == null) throw new Exception("Data error");
    }


    public static void check(Exception e, JsonObject result) throws Exception {
        check(e, (Object) result);
        CheckError error = new Gson().fromJson(result, CheckError.class);
        if (error.getError() != null) throw new JamError(error.getError());
    }

    public static void check(Exception e, Response<?> response) throws Exception {
        if (response.getHeaders().code() < 200 || response.getHeaders().code() > 299) throw new Exception("Connection error");
        if (response.getResult() instanceof JsonObject) {
            check(e, (JsonObject) response.getResult());}
        else {
            check(e, response.getResult());
        }
    }
}