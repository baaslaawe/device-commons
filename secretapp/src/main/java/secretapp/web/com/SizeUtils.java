package secretapp.web.com;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class SizeUtils {

    static int getMainHeight(Context context) {
        int screenH = getScreenSize(context).y;
        return screenH - getBottomBlockHeight(context) - getStatusBarHeight(context);
    }

    static int getBottomBlockHeight(Context context) {
        //        return convertDpToPixel(context, 48f);
        return context.getResources().getDimensionPixelOffset(R.dimen.overlay_bottom_window_height);
    }

    static int getBottomBlockWidth(Context context) {
        int screenW = getScreenSize(context).x;
        //        return screenW - convertDpToPixel(context, 72f);
        return screenW - context.getResources().getDimensionPixelOffset(R.dimen.overlay_bottom_window_width);
    }

    /*int getSkipBtnOffsetX(Context context, int btnWidth) {
        int size = convertDpToPixel(context, 72f);
        return getBottomBlockWidth(context) + size / 2 - btnWidth / 2;
    }

    static int getSkipBtnOffsetY(Context context, int btnHeight) {
        return getMainHeight(context) + getBottomBlockHeight(context) / 2 - btnHeight / 2;
    }*/

    private static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    static private int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float size = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(size);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
