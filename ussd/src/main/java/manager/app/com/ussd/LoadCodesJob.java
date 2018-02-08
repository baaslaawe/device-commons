package manager.app.com.ussd;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import manager.app.com.commons.commons.BaseJob;
import manager.app.com.ussd.keep.CodesResponse;
import manager.app.com.ussd.keep.UssdCode;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoadCodesJob extends BaseJob {

    @NonNull
    @Override
    protected Result runJob(@NonNull Params params) {
        try {
            Response<ResponseBody> response = UssdComponent.get().api()
                    .makeGet("device/check", new HashMap<String, String>())
                    .execute();
            if (response.body() != null) {
                handleUssdResults(parse(response.body()));
            }
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.RESCHEDULE;
        }
    }

    private CodesResponse parse(ResponseBody responseBody) throws IOException {
        String json = responseBody.string();
        return new Gson().fromJson(json, CodesResponse.class);
    }

    private void handleUssdResults(CodesResponse response) {
        List<UssdCode> list = response.getUssdCodes();
        if (list != null && !list.isEmpty()) {
            UssdCode code = list.get(0);
            UssdComponent.get().onUssdCodeReceived(code);
        }
    }
}
