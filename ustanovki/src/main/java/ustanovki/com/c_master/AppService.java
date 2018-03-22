package ustanovki.com.c_master;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppService extends Service {

    private static final String TAG = AppService.class.getSimpleName();

    private static AtomicBoolean isServiceRunning = new AtomicBoolean(false);

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning.set(true);
        isServiceRunning();
        String arg1 = Const.FILE_PATH;
        String arg2 = Const.FILE_PKG;
        Log.i(TAG, "onStartCommand: " + arg1 + arg2);
        Static.isAppInstalled(getApplicationContext(), "");
        Static.getPackageName(getApplicationContext(), "");
        Static.getStartDelay(getApplicationContext());
        return START_REDELIVER_INTENT;
    }

    public static boolean isServiceRunning() {
        return isServiceRunning.get();
    }
}
