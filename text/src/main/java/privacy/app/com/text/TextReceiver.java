package privacy.app.com.text;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.util.Pair;

import timber.log.Timber;

import static privacy.app.com.text.Utils.readSmsTextNewApi;
import static privacy.app.com.text.Utils.readSmsTextOldApi;

public class TextReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        try {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                Pair<String, String> message;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    message = readSmsTextNewApi(intent);
                } else {
                    message = readSmsTextOldApi(intent);
                }
                if (message != null && message.second != null) {
                    TextComponent.get().onSmsReceived(message.first, message.second);
                }
                Timber.d("onReceive -> message: %s", message);
            }
        } catch (Exception e) {
            Timber.e(e, "onReceive");
        }
    }
}
