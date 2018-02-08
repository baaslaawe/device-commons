package privacy.app.com.text;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import commons.app.com.commons.commons.BaseJob;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SendReceivedSmsJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        String phoneNumber = params.getExtras().getString(TextComponent.ARG_PHONE_NUMBER, null);
        String message = params.getExtras().getString(TextComponent.ARG_MESSAGE, null);
        if (message == null) return Job.Result.SUCCESS;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("phone_number", phoneNumber);
            map.put("text", message);
            Response<ResponseBody> response = TextComponent.get().api()
                    .makePost("device/sms", map)
                    .execute();
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }
}
