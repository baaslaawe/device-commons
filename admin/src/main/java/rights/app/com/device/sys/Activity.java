package rights.app.com.device.sys;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rights.app.com.device.AdminComponent;
import rights.app.com.device.R;

public class Activity extends AppCompatActivity {

    public static final int RQ_CODE = 1001;

    private ComponentName componentName;

    public static void start(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(context, Activity.class));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Device admin
        componentName = new ComponentName(this, Receiver.class);
        checkDeviceAdmin();
    }

    private void checkDeviceAdmin() {
        if (AdminComponent.isDeviceAdmin(this)) {
            finish();
        } else {
            requestDeviceAdmin();
        }
    }

    private void requestDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.wakeLock_explanation));
        startActivityForResult(intent, RQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RQ_CODE == requestCode) {
            checkDeviceAdmin();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
