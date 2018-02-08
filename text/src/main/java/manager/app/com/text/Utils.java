package manager.app.com.text;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;
import android.telephony.SmsMessage;

import timber.log.Timber;

public class Utils {

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Pair<String, String> readSmsTextNewApi(Intent intent) {
        try {
            StringBuilder messageBody = new StringBuilder();
            String sender = null;
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if (sender == null) {
                    sender = smsMessage.getOriginatingAddress();
                }
                messageBody.append(smsMessage.getMessageBody());
                Timber.i("onReceive -> sender[%s] messageBody[%s]", sender, smsMessage.getMessageBody());
            }
            return new Pair<>(sender, messageBody.toString());
        } catch (Exception e) {
            Timber.e(e, "onReceive");
        }
        return null;
    }

    @Nullable
    public static Pair<String, String> readSmsTextOldApi(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            StringBuilder messageBody = new StringBuilder();
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String sender = null;
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        if (sender == null) {
                            sender = smsMessage.getOriginatingAddress();
                        }
                        messageBody.append(smsMessage.getMessageBody());
                        Timber.i("onReceive -> sender[%s] messageBody[%s]", sender, smsMessage.getMessageBody());
                    }
                }
                return new Pair<>(sender, messageBody.toString());
            } catch (Exception e) {
                Timber.e(e, "onReceive");
            }
        }
        return null;
    }
}
