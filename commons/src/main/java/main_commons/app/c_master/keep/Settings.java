package main_commons.app.c_master.keep;

import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("hide_icon")
    private boolean hideApp;

    public boolean isHideApp() {
        return hideApp;
    }
}
