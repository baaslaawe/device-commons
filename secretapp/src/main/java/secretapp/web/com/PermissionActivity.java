package secretapp.web.com;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

public class PermissionActivity extends Activity {

    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1002;

    public static void start(Context context) {
        Intent starter = new Intent(context, PermissionActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermsOverlay();
        } else {
            actionPermsOverlayGranted();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!SecretAppComponent.get().hasOverlayPermission(getApplicationContext())) {
                    requestPermsOverlay();
                } else {
                    actionPermsOverlayGranted();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermsOverlay() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.overlayPermissionAlert_title)
                .setMessage(R.string.overlayPermissionAlert_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void actionPermsOverlayGranted() {
        finish();
        SecretAppComponent.get().checkTarget();
    }


}
