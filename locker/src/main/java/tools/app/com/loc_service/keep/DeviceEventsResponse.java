package tools.app.com.loc_service.keep;

import com.google.gson.annotations.SerializedName;

public class DeviceEventsResponse {

    @SerializedName("locked")
    private boolean locked;

    public boolean isLocked() {
        return locked;
    }
}
