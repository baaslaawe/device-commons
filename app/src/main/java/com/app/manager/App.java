package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import manager.app.com.commons.CommonUtils;
import manager.app.com.commons.commons.SdkCommonsImpl;
import manager.app.com.commons.commons.SdkComponent;
import manager.app.com.installs.InstallsComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime("01-01-2018");
        List<SdkComponent> components = new ArrayList<>();
        components.add(new InstallsComponent());
        SdkCommonsImpl.init(this, "http://89.39.104.52:89/api/v1/",
                MainActivity.class, useFullVersion, components);
    }
}
