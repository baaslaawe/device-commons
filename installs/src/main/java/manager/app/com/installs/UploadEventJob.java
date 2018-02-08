package manager.app.com.installs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import manager.app.com.commons.commons.BaseJob;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class UploadEventJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        String apkId = params.getExtras().getString(InstallsComponent.ARG_APP_ID, null);
        if (apkId == null) return Job.Result.SUCCESS;
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put("apk_id", apkId);
            fields.put("installed", String.valueOf(true));
            Response<ResponseBody> response = InstallsComponent.get().api()
                    .makePost("device/install", fields)
                    .execute();
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }
}
