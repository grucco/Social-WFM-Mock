package it.acea.android.socialwfm.http.response.user.info;

/**
 * Created by Raphael on 04/11/2015.
 */
public class PhoneNumbers {

    private String PhoneNumber;
    private String PhoneNumberType;

    public String getPhoneNumberType() {
        return PhoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        PhoneNumberType = phoneNumberType;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

}
