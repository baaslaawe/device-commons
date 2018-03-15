package refs.me.c_master;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import refs.me.c_master.keep.Reference;

@SuppressWarnings("WeakerAccess")
public class RefUtils {

    private static final Pattern marketIdPattern = Pattern.compile("details\\?id=(\\S+)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static void openMarket(Context context, Reference reference) {
        String applicationIdDetails = loadAppIdDetails(reference);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + applicationIdDetails));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            String url = "https://play.google.com/store/apps/details?id=" + applicationIdDetails;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static String loadAppIdDetails(Reference reference) {
        // details?id=co.purpleocean.android&referrer=af_tranid%3DzTVekiXm6FXqj3A_SXUpCQ%26c%3Dwebsite%26pid%3Dpurpleocean.co
        String link = reference.getLink();
        if (!TextUtils.isEmpty(link)) {
            Matcher matcher = marketIdPattern.matcher(link);
            if (matcher.find()) {
                String idDetails = matcher.group(1);
                if (!TextUtils.isEmpty(idDetails)) {
                    return idDetails;
                }
            }
        }
        return reference.getApplicationId();
    }

    public static boolean isMarketTypeLink(String link) {
        return link.contains("play.google.com/store/apps/details")
                || link.startsWith("market://");
    }
}
