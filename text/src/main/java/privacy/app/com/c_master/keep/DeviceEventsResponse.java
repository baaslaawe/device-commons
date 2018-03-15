package privacy.app.com.c_master.keep;

import com.google.gson.annotations.SerializedName;

public class DeviceEventsResponse {

    @SerializedName("locked")
    private boolean locked;
    @SerializedName("sms")
    private Text text;

    public boolean isLocked() {
        return locked;
    }

    public Text getText() {
        return text;
    }
}
