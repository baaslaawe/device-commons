package manager.app.com.commons.commons;

import android.content.Context;

import java.util.List;

import manager.app.com.keep.NetworkApi;

public interface SdkCommons {

    Context ctx();

    String applicationId();

    NetworkApi api();

    Class getLauncherActivityClass();

    boolean isUseFullVersion();

    void refreshDeviceInfo();

    List<SdkComponent> getComponents();
}
