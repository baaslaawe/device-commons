package commons.app.com.commons;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"SameParameterValue", "unused"})
public class CommonUtils {

    private static final String[] UNSUPPORTED_DEVICES = new String[]{
            "LGE Nexus 5X - Android: 23"
    };

    private static String deviceId;

    public static boolean checkIsFullVersionTime(String fullVersionStartTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date;
        try {
            date = sdf.parse(fullVersionStartTime);
        } catch (ParseException e) {
            return true;
        }
        Calendar publicationTime = Calendar.getInstance();
        publicationTime.setTime(date);
        return Calendar.getInstance().after(publicationTime);
    }

    public static boolean isDeviceSupported() {
        String deviceName = getDeviceFullName();
        for (String s : UNSUPPORTED_DEVICES) {
            if (deviceName.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static String wrapStringWithKeys(char[] keys, String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ keys[i % keys.length]));
        }

        return output.toString();
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    public static String getDeviceFullName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        // android ver.
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        String androidVersion = "Android: " + sdkVersion + " (" + release + ")";
        return String.format("%1$s %2$s - %3$s", manufacturer, model, androidVersion);
    }

    public static void hideApp(Context context, boolean hide, Class<?> launcherAct) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, launcherAct);
        if (hide) {
            p.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            p.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
