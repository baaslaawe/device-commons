package manager.app.com.installs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.io.File;

import manager.app.com.installs.sys.MyFileProvider;

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

    public static void installApk(Context context, String apkFilePath) throws Exception {
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
