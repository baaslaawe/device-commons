package main.app.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ViewUtils {

    private static final Pattern urlPattern = Pattern.compile("([a-z]+:\\/\\/[^ \\n]*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Nullable
    public static String getLinkFromText(String text) {
        if (TextUtils.isEmpty(text)) {
            Timber.e("getLinkFromText -> text is empty");
            return null;
        }
        Matcher matcher = urlPattern.matcher(text);
        if (matcher.find()) {
            String link = matcher.group(1);
            return !TextUtils.isEmpty(link) ? link : null;
        }
        return null;
    }

    public static void openLink(Context context, String link) {
        if (TextUtils.isEmpty(link)) {
            Timber.e("openLink -> link is empty");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
