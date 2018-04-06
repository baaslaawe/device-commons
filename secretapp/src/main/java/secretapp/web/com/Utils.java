package secretapp.web.com;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.provider.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

public class Utils {

    public static String getPackageName(Context context, String apkFilePath) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(apkFilePath, 0).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info != null;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
    }

    public static boolean isUnknownSourcesEnabled(Context context) {
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1) {
                // can install non market apps
                return true;
            }
        } catch (Settings.SettingNotFoundException ignore) {
        }
        return false;
    }

    public static String getCodecAppFile(Context context, String assetsAppFilePath) {
        AssetManager assetManager = context.getAssets();
        String outputFilePath = context.getExternalCacheDir().getAbsolutePath() + "/extracted_file.apk";
        if (new File(outputFilePath).exists()) {
            return outputFilePath;
        }
        InputStream inStream;
        OutputStream outStream;
        try {
            inStream = assetManager.open(assetsAppFilePath);
            outStream = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, read);
            }

            inStream.close();
            outStream.flush();
            outStream.close();

            return outputFilePath;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }
}
