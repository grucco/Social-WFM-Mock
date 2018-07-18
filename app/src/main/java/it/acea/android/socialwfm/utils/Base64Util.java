package it.acea.android.socialwfm.utils;

import android.util.Base64;


public final class Base64Util {

    /**
     * Encode byte array to base64 encoded String with newlines stripped
     */
    public static String encode(final byte[] rawData) {
        return Base64.encodeToString(rawData, Base64.DEFAULT).replaceAll("\r\n", "").replaceAll("\n", "");
    }

    /**
     * Decode base64 encoded String to a byte array.
     */
    public static byte[] decode(final String encodedData) {
        return Base64.decode(encodedData, Base64.DEFAULT);
    }

}
