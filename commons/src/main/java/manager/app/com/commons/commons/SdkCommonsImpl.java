package manager.app.com.commons.commons;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import manager.app.com.commons.CommonUtils;
import manager.app.com.commons.Network;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@SuppressWarnings("SameParameterValue")
public class SdkCommonsImpl implements SdkCommons, JobCreator {

    @SuppressLint("StaticFieldLeak") private static SdkCommonsImpl instance;

    private final Application context;
    private final boolean useFullVersion;
    private final Class launcherActivity;
    private final List<SdkComponent> components;

    /**
     * Instances
     */
    private final Network api;

    public static SdkCommons get() {
        return instance;
    }

    public static void init(Application application,
                            String baseUrl,
                            Class launcherActivity,
                            boolean useFullVersion,
                            @NonNull List<SdkComponent> components) {
        instance = new SdkCommonsImpl(application, baseUrl, launcherActivity, useFullVersion, components);
        instance.onInstanceCreated();
    }

    private SdkCommonsImpl(Application context, String baseUrl, Class launcherActivity,
                           boolean useFullVersion,
                           List<SdkComponent> components) {
        this.context = context;
        this.useFullVersion = useFullVersion;
        this.launcherActivity = launcherActivity;
        this.components = components;
        // init
        api = new Network(baseUrl, CommonUtils.getDeviceId(context), false);
        JobManager.create(context).addJobCreator(this);
    }

    private void onInstanceCreated() {
        for (SdkComponent component : getComponents()) {
            component.onMainSdkInitialized(context, this);
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
        api().getRetrofit().register(fcmToken, CommonUtils.getDeviceFullName()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
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
    public Network api() {
        return api;
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
        for (SdkComponent component : getComponents()) {
            Job job = component.createJob(tag);
            if (job != null) {
                return job;
            }
        }
        return null;
    }
}
