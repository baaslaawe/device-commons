package utils.app.com.installs.sys;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

public class MyFileProvider extends FileProvider {

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createUriNewApi(context, file);
        } else {
            return createUriOldApi(file);
        }
    }

    private static Uri createUriNewApi(Context context, File file) {
        String authorities = context.getApplicationContext().getPackageName() + ".my_provider.sdk";
        return getUriForFile(context, authorities, file);
    }

    private static Uri createUriOldApi(File file) {
        return Uri.parse("file://" + file.getAbsolutePath());
    }
}