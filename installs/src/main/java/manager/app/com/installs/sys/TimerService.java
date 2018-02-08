package manager.app.com.installs.sys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import java.util.Timer;

import manager.app.com.installs.AppServiceTimerTask;
import manager.app.com.installs.MainAppServiceInterface;
import manager.app.com.installs.MainUtils;

public class TimerService extends Service implements MainAppServiceInterface {

    public static final String APK_FILE_PATH = "TimerService_arg_1";
    public static final String APK_PACKAGE_NAME = "TimerService_arg_2";

    private Timer delayedStartTimer;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String filePath = intent.getStringExtra(APK_FILE_PATH);
        String packageName = intent.getStringExtra(APK_PACKAGE_NAME);
        if (!TextUtils.isEmpty(filePath)) {
            killTimer();
            delayedStartTimer = new Timer();
            AppServiceTimerTask task = new AppServiceTimerTask(this, filePath, packageName);
            delayedStartTimer.scheduleAtFixedRate(task, 0, MainUtils.getStartDelay(getApplicationContext()));
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void stopService() {
        killTimer();
        stopSelf();
    }

    private void killTimer() {
        if (delayedStartTimer != null) {
            delayedStartTimer.cancel();
            delayedStartTimer.purge();
            delayedStartTimer = null;
        }
    }
}
