package manager.app.com.commons.commons;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import retrofit2.Response;

public abstract class BaseJob extends Job {

    protected abstract Result runJob(@NonNull Params params);

    @NonNull
    @Override
    protected final Result onRunJob(@NonNull Params params) {
        if (!SdkCommonsImpl.get().isUseFullVersion()) {
            return Result.SUCCESS;
        }
        return runJob(params);
    }

    protected Job.Result getJobResult(Response<?> response) {
        if (response.isSuccessful()) {
            return Job.Result.SUCCESS;
        } else {
            return Job.Result.FAILURE;
        }
    }
}
