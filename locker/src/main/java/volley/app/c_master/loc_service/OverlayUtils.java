package volley.app.c_master.loc_service;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

public class OverlayUtils {

    public static WindowManager.LayoutParams createAllTopParams(int height) {
        return new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                height,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                FLAG_NOT_FOCUSABLE
                        | FLAG_LAYOUT_IN_SCREEN
                        | FLAG_LAYOUT_NO_LIMITS
                        | FLAG_NOT_TOUCH_MODAL
                        | FLAG_LAYOUT_INSET_DECOR
                , TRANSLUCENT);
    }

    public static WindowManager.LayoutParams createNormalParams() {
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL*/, PixelFormat.TRANSLUCENT);
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }
}
