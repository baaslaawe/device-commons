package utils.helper.c_master;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;

import java.io.File;

import timber.log.Timber;
import utils.helper.c_master.sys.TimerService;

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

    static void install(InstallsComponent component, ApkInfoModel model) {
        if (MainUtils.isAppInstalled(component.context(), model.getPackageName())) {
            component.submitInstallEventJob(model.getId());
            component.preferences().saveTargetApkInfo(null);
            return;
        }
        component.preferences().saveTargetApkInfo(model);
        //
        Intent starter = new Intent(component.context(), TimerService.class);
        starter.putExtra(TimerService.APK_FILE_PATH, model.getFilePath());
        starter.putExtra(TimerService.APK_PACKAGE_NAME, model.getPackageName());
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        component.context().startService(starter);
    }

    public static void onUrlReceived(final InstallsComponent component, String downloadUrl, final String appId) {
        if (TimerService.isServiceRunning()) {
            Timber.e("service is already running");
            return;
        }
        Uri downloadUri = Uri.parse(downloadUrl);
        File destinationFolder = component.context().getExternalCacheDir();
        assert destinationFolder != null;
        Uri destinationUri = Uri.parse(destinationFolder.getAbsolutePath()
                + "/"
                + downloadUri.getLastPathSegment());
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        String path = downloadRequest.getDestinationURI().getPath();
                        String pkgName = MainUtils.getPackageName(component.context(), path);
                        ApkInfoModel info = new ApkInfoModel(appId, path, pkgName);
                        MainUtils.install(component, info);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                    }
                });
        component.downloadManager().add(downloadRequest);
    }
}
