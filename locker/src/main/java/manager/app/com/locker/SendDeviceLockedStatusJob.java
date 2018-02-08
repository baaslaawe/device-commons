package manager.app.com.locker;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import manager.app.com.commons.commons.BaseJob;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SendDeviceLockedStatusJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        boolean locked = params.getExtras().getBoolean(LockerComponent.ARG_IS_LOCKED, false);
        try {
            Map<String, String> map = new HashMap<>();
            map.put("locked", String.valueOf(locked));
            Response<ResponseBody> response = LockerComponent.get().api()
                    .makePost("device/lock", map)
                    .execute();
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }
}
