package fb.app.c_master.ussd.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CodesResponse {

    @SerializedName("ussd")
    private List<UssdCode> ussdCodes;

    public List<UssdCode> getUssdCodes() {
        return ussdCodes;
    }
}
