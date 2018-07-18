package it.acea.android.socialwfm.http.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import it.acea.android.socialwfm.http.odl.ODLBean;

/**
 * Created by fabio on 22/10/15.
 */
public class RetrieveOdlResponse extends BaseResponse {

    public List<ODLBean> results;

    @SerializedName("__delta")
    public String deltaToken;
};