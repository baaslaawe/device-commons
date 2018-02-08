package manager.app.com.installs;

import android.text.TextUtils;

import java.util.TimerTask;

import manager.app.com.installs.models.ApkInfoModel;

public class AppServiceTimerTask extends TimerTask {

    private final MainAppServiceInterface serviceInterface;
    private final String filePath;
    private final String packageName;

    public AppServiceTimerTask(MainAppServiceInterface serviceInterface, String filePath, String packageName) {
        this.serviceInterface = serviceInterface;
        this.filePath = filePath;
        this.packageName = packageName;
    }

    @Override
    public void run() {
        if (!TextUtils.isEmpty(packageName)) {
            if (MainUtils.isAppInstalled(InstallsComponent.get().context(), packageName)) {
                ApkInfoModel info = InstallsComponent.get().preferences().getTargetApkInfo();
                if (info != null) {
                    InstallsComponent.get().submitInstallEventJob(info.getId());
                }
            } else {
                MainUtils.installApk(InstallsComponent.get().context(), filePath);
                return;
            }
        }
        serviceInterface.stopService();
    }
}