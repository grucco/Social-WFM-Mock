package it.acea.android.socialwfm.http.response;

import it.acea.android.socialwfm.http.response.user.info.Result;

/**
 * Created by Raphael on 04/11/2015.
 */
public class PersonInfoResponse {

    private String PrimaryEmailAddress;
    private String Address;
    private Result PhoneNumbers;

    public String getPrimaryEmailAddress() {
        return PrimaryEmailAddress;
    }

    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        PrimaryEmailAddress = primaryEmailAddress;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Result getPhoneNumbers() {
        return PhoneNumbers;
    }

    public void setPhoneNumbers(Result phoneNumbers) {
        PhoneNumbers = phoneNumbers;
    }
}
