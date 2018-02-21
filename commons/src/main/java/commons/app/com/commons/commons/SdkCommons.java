package commons.app.com.commons.commons;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import commons.app.com.keep.NetworkApi;

public interface SdkCommons {

    Context ctx();

    String applicationId();

    NetworkApi api();

    Gson gson();

    Class getLauncherActivityClass();

    boolean isUseFullVersion();

    void refreshDeviceInfo();

    List<SdkComponent> getComponents();
}
