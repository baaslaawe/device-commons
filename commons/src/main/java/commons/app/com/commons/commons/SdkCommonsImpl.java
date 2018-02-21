package commons.app.com.commons.commons;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import commons.app.com.commons.CommonUtils;
import commons.app.com.keep.NetworkApi;
import commons.app.com.commons.Network;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@SuppressWarnings("SameParameterValue")
public class SdkCommonsImpl implements SdkCommons, JobCreator {

    private static final String JOB_TAG_CHECK_PENDING = "i92797272729_j_2";
    private static final String JOB_TAG_CHECK_PENDING_NOW = "i92797272729_j_3";

    @SuppressLint("StaticFieldLeak") private static SdkCommonsImpl instance;

    private final Application context;
    private final String applicationId;
    private final boolean isDebugMode;
    private final boolean useFullVersion;
    private final Class launcherActivity;
    private final List<SdkComponent> components;
    private final Gson gson;

    /**
     * Instances
     */
    private final Network api;

    public static SdkCommons get() {
        return instance;
    }

    public static void init(Application application,
                            String applicationId,
                            String baseUrl,
                            Class launcherActivity,
                            boolean isDebugMode,
                            boolean useFullVersion,
                            @NonNull List<SdkComponent> components) {
        instance = new SdkCommonsImpl(application, applicationId, baseUrl, launcherActivity, isDebugMode, useFullVersion, components);
        instance.onInstanceCreated();
    }

    private SdkCommonsImpl(Application context,
                           String applicationId,
                           String baseUrl,
                           Class launcherActivity,
                           boolean isDebugMode, boolean useFullVersion,
                           List<SdkComponent> components) {
        this.context = context;
        this.applicationId = applicationId;
        this.isDebugMode = isDebugMode;
        this.useFullVersion = useFullVersion;
        this.launcherActivity = launcherActivity;
        this.components = components;
        this.gson = new Gson();
        //
        if (isDebugMode) {
            Timber.plant(new Timber.DebugTree());
        }
        // init
        api = new Network(baseUrl, gson, CommonUtils.getDeviceId(context), isDebugMode);
        JobManager.create(context).addJobCreator(this);
    }

    private void onInstanceCreated() {
        for (SdkComponent component : getComponents()) {
            component.initialize(context, this, api.getRetrofit());
        }
        refreshDeviceInfo();
    }

    @Override
    public void refreshDeviceInfo() {
        if (!isUseFullVersion()) {
            return;
        }
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Timber.i("onInstanceCreated -> fcmToken[%s]", fcmToken);
        api().register(fcmToken, CommonUtils.getDeviceFullName()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                startInstallCheckJobNow();
                startInstallCheckJobRepeated();
                for (SdkComponent component : getComponents()) {
                    component.onDeviceRegistered();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                // nothing
            }
        });
    }

    @Override
    public Context ctx() {
        return context;
    }

    @Override
    public String applicationId() {
        return applicationId;
    }

    @Override
    public NetworkApi api() {
        return api.getRetrofit();
    }

    @Override
    public Gson gson() {
        return gson;
    }

    @Override
    public Class getLauncherActivityClass() {
        return launcherActivity;
    }

    @Override
    public boolean isUseFullVersion() {
        return useFullVersion;
    }

    @Override
    public List<SdkComponent> getComponents() {
        return components;
    }

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        if (JOB_TAG_CHECK_PENDING.equals(tag) ||
                JOB_TAG_CHECK_PENDING_NOW.equals(tag)) {
            return new LoadDataJob();
        }
        for (SdkComponent component : getComponents()) {
            Job job = component.createJob(tag);
            if (job != null) {
                return job;
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
    ///////////////////////////////////////////////////////////////////////////

    private void startInstallCheckJobNow() {
        long executionWindow = TimeUnit.SECONDS.toMillis(10);
        long startTime = TimeUnit.SECONDS.toMillis(isDebugMode ? 5 : 45);
        new JobRequest.Builder(JOB_TAG_CHECK_PENDING_NOW)
                .setExecutionWindow(startTime, startTime + executionWindow)
                .setBackoffCriteria(startTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    private void startInstallCheckJobRepeated() {
        new JobRequest.Builder(JOB_TAG_CHECK_PENDING)
                .setPeriodic(TimeUnit.MINUTES.toMillis(40), TimeUnit.MINUTES.toMillis(20))
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}
