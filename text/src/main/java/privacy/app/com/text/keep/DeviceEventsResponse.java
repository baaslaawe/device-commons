package privacy.app.com.text.keep;

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
