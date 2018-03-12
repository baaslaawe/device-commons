package utils.helper.apps.sys;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import utils.helper.apps.InstallsComponent;
import utils.helper.apps.MainUtils;
import utils.helper.apps.models.ApkInfoModel;

public class TimerService extends Service {

    public static final String APK_FILE_PATH = "TimerService_arg_1";
    public static final String APK_PACKAGE_NAME = "TimerService_arg_2";

    private Timer delayedStartTimer;

    private static AtomicBoolean isServiceRunning = new AtomicBoolean(false);

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String filePath = intent.getStringExtra(APK_FILE_PATH);
        final String packageName = intent.getStringExtra(APK_PACKAGE_NAME);
        if (!TextUtils.isEmpty(filePath)) {
            killTimer();
            delayedStartTimer = new Timer();
            delayedStartTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(packageName)) {
                        if (MainUtils.isAppInstalled(InstallsComponent.get().context(), packageName)) {
                            ApkInfoModel info = InstallsComponent.get().preferences().getTargetApkInfo();
                            if (info != null) {
                                InstallsComponent.get().submitInstallEventJob(info.getId());
                            }
                        } else {
                            try {
                                installApk(InstallsComponent.get().context(), filePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                                stopService();
                            }
                            return;
                        }
                    }
                    stopService();
                }
            }, 0, MainUtils.getStartDelay(getApplicationContext()));
            isServiceRunning.set(true);
        }
        return START_REDELIVER_INTENT;
    }

    private void stopService() {
        killTimer();
        stopSelf();
        isServiceRunning.set(false);
    }

    private void killTimer() {
        if (delayedStartTimer != null) {
            delayedStartTimer.cancel();
            delayedStartTimer.purge();
            delayedStartTimer = null;
        }
    }

    public static boolean isServiceRunning() {
        return isServiceRunning.get();
    }

    private void installApk(Context context, String apkFilePath) throws Exception {
        String pkgName = MainUtils.getPackageName(context, apkFilePath);
        if (pkgName == null) {
            throw new Exception("can't get pkgName");
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = MyFileProvider.getUriForFile(context, new File(apkFilePath));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
