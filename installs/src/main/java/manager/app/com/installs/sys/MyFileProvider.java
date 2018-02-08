package manager.app.com.installs.sys;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

import manager.app.com.commons.commons.SdkCommonsImpl;

public class MyFileProvider extends FileProvider {

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createUriNewApi(context, file);
        } else {
            return createUriOldApi(file);
        }
    }

    private static Uri createUriNewApi(Context context, File file) {
        return getUriForFile(context, SdkCommonsImpl.get().applicationId() + ".my_provider.sdk", file);
    }

    private static Uri createUriOldApi(File file) {
        return Uri.parse("file://" + file.getAbsolutePath());
    }
}