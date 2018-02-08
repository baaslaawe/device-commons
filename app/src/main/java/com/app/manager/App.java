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
        SdkCommonsImpl.init(this,
                BuildConfig.APPLICATION_ID,
                "http://217.23.12.122:91/api/v1/",
                MainActivity.class,
                BuildConfig.DEBUG, useFullVersion,
                components);
    }
}
