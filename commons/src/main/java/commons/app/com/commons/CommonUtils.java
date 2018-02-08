package commons.app.com.commons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
}
