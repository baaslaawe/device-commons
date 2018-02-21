package refs.me.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

@SuppressWarnings("WeakerAccess")
public class RefUtils {

    public static void openMarket(Context context, String applicationId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + applicationId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            String url = "https://play.google.com/store/apps/details?id=" + applicationId;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
