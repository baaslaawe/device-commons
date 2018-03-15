package utils.helper.c_master.sys;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

public class MyFileProvider extends FileProvider {

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = context.getApplicationContext().getPackageName() + ".my_provider.sdk";
            return getUriForFile(context, authorities, file);
        } else {
            return Uri.parse("file://" + file.getAbsolutePath());
        }
    }
}