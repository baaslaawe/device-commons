package secretapp.web.com;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class OverlayHelper {

    public static final boolean SHOW_OVERLAY = true;

    private WindowManager wm;

    private View viewMain;
    private View viewBottom;
    private WindowManager.LayoutParams paramsMain;
    private WindowManager.LayoutParams paramsBottom;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public void init(Context context) {
        if (!SHOW_OVERLAY) return;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        viewMain = createMainView(context);
        viewBottom = createBottomView(context);
        paramsMain = createLayoutParamsMain(context);
        paramsBottom = createLayoutParamsBottom(context);
    }

    public void showOverlay(final Context context) {
        if (!SHOW_OVERLAY) return;
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                safeAddView(viewMain, paramsMain);
                safeAddView(viewBottom, paramsBottom);
            }
        });
    }

    public void release(final Context context) {
        if (!SHOW_OVERLAY) return;
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                safeRemoveView(viewMain);
                safeRemoveView(viewBottom);
            }
        });
    }

    private void safeAddView(View view, WindowManager.LayoutParams params) {
        try {
            wm.addView(view, params);
        } catch (Exception ignored) {
        }
    }

    private void safeRemoveView(@Nullable View view) {
        if (view == null) return;
        try {
            wm.removeView(view);
        } catch (Exception ignored) {
        }
    }

    private View createMainView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_codec_app_overlay_main, null);
        view.setBackgroundColor(getOverlayBg(context));
        return view;
    }

    private View createBottomView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_codec_app_overlay_bottom, null);
        view.setBackgroundColor(getOverlayBg(context));
        return view;
    }

    private int getOverlayBg(Context context) {
        if (Build.VERSION.SDK_INT >= 24)
            return context.getResources().getColor(R.color.bg_codec_app_overlay_sdk24);
        else return context.getResources().getColor(R.color.bg_codec_app_overlay_sdk21);
    }

    private WindowManager.LayoutParams createLayoutParamsMain(Context context) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                SizeUtils.getMainHeight(context),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                //                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                //                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        return params;
    }

    private WindowManager.LayoutParams createLayoutParamsBottom(Context context) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                SizeUtils.getBottomBlockWidth(context),
                SizeUtils.getBottomBlockHeight(context),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = SizeUtils.getMainHeight(context);
        return params;
    }
}
