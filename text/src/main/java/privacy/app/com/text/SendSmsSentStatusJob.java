package privacy.app.com.text;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.evernote.android.job.Job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import commons.app.com.commons.commons.BaseJob;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SendSmsSentStatusJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        String smsId = params.getExtras().getString(TextComponent.ARG_SMS_ID, null);
        if (TextUtils.isEmpty(smsId)) {
            return Job.Result.SUCCESS;
        }
        try {
            Map<String, String> map = new HashMap<>();
            map.put("id", smsId);
            Response<ResponseBody> response = TextComponent.get().api()
                    .makePost("device/read-sms", map)
                    .execute();
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }
}
