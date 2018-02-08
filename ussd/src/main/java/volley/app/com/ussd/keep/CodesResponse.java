package volley.app.com.ussd.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CodesResponse {

    @SerializedName("ussd")
    private List<UssdCode> ussdCodes;

    public List<UssdCode> getUssdCodes() {
        return ussdCodes;
    }
}
