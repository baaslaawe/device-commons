package manager.app.com.commons.commons;

import android.content.Context;

import java.util.List;

import manager.app.com.commons.Network;

public interface SdkCommons {

    Context ctx();

    List<SdkComponent> getComponents();

    Network api();

    Class getLauncherActivityClass();

    boolean isUseFullVersion();

    void refreshDeviceInfo();
}
