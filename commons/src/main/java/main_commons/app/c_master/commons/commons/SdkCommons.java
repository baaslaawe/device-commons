package main_commons.app.c_master.commons.commons;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import main_commons.app.c_master.keep.NetworkApi;

public interface SdkCommons {

    Context ctx();

    String applicationId();

    NetworkApi api();

    Gson gson();

    Class getLauncherActivityClass();

    boolean isUseFullVersion();

    boolean isCheckDeviceIp();

    void refreshDeviceInfo();

    List<SdkComponent> getComponents();
}
