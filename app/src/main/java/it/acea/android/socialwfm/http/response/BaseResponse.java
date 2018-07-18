package it.acea.android.socialwfm.http.response;

import java.io.Serializable;

/**
 * Created by fabio on 07/01/16.
 * {
 * "error": {
 * "code": "005056A509B11EE1B9A8FEC11C21D78E",
 * "message": {
 * "lang": "en",
 * "value": "Resource not found for the segment ''Impianto'."
 * },
 * "innererror": {
 * "transactionid": "5683924CCD323AF8E1000000AC104E41",
 * "timestamp": "20160107091930.8540340",
 * "Error_Resolution": {
 * "SAP_Transaction": "Run transaction /IWFND/ERROR_LOG on SAP NW Gateway hub system and search for entries with the timestamp above for more details",
 * "SAP_Note": "See SAP Note 1797736 for error analysis (https://service.sap.com/sap/support/notes/1797736)"
 * }
 * }
 * }
 * }
 */
public class BaseResponse implements Serializable {

    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Error {
        public String code;
        public Message message;
    }

    public class Message {
        public String lang;
        public String value;
    }
}
