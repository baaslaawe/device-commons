package utils.helper.c_master;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class MainUtils {

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info != null;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
    }

    public static String getPackageName(Context context, String apkFilePath) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(apkFilePath, 0).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getStartDelay(Context context) {
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1) {
                // can install non market apps
                return 400;
            }
        } catch (Settings.SettingNotFoundException ignore) {
        }
        return 8000;
    }
}
