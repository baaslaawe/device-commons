package ustanovki.com.c_master;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class Static {

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return info != null;
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

    public static void launchApkActivity(Context context, String apkFilePath) throws Exception {
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileURI.getUriForFile(context, new File(apkFilePath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);*/
    }

    /*static void install(AppModule component, AppModule.InstallInfoModel model) {
        if (isAppInstalled(component.context(), model.getPackageName())) {
            //            component.submitInstallEventJob(model.getId());
            //            component.saveTargetApkInfo(null);
            return;
        }
        component.saveTargetApkInfo(model);
        //
        Intent starter = new Intent(component.context(), AppService.class);
        starter.putExtra(Const.APK_FILE_PATH, model.getFilePath());
        starter.putExtra(Const.APK_PACKAGE_NAME, model.getPackageName());
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        component.context().startService(starter);
    }

    public static void onUrlReceived(final AppModule component, String downloadUrl, final String appId) {
        if (AppService.isServiceRunning()) {
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
                        String pkgName = getPackageName(component.context(), path);
                        AppModule.InstallInfoModel info = new AppModule.InstallInfoModel(appId, path, pkgName);
                        install(component, info);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                    }
                });
        component.downloadManager().add(downloadRequest);
    }*/
}
