package main_commons.app.c_master;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.evernote.android.job.Job;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoadDataJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        try {
            Response<ResponseBody> response = SdkCommonsImpl.get().api()
                    .makeGet("device/check", new HashMap<String, String>())
                    .execute();
            if (response.body() != null) {
                String json = response.body().string();
                if (!TextUtils.isEmpty(json)) {
                    ((SdkCommonsImpl) SdkCommonsImpl.get()).onSyncEvent(json);
                }
            }
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }
}
