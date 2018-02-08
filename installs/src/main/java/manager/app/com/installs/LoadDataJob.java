package manager.app.com.installs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import manager.app.com.commons.commons.BaseJob;
import manager.app.com.installs.keep.InstallModel;
import manager.app.com.installs.keep.InstallsResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoadDataJob extends BaseJob {

    @NonNull
    @Override
    protected Job.Result runJob(@NonNull Job.Params params) {
        try {
            Response<ResponseBody> response = InstallsComponent.get().api()
                    .makeGet("device/check", new HashMap<String, String>())
                    .execute();
            if (response.body() != null) {
                handleApkResults(parse(response.body()));
            }
            return getJobResult(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Job.Result.RESCHEDULE;
        }
    }

    private InstallsResponse parse(ResponseBody responseBody) throws IOException {
        String json = responseBody.string();
        return new Gson().fromJson(json, InstallsResponse.class);
    }

    private void handleApkResults(InstallsResponse response) {
        List<InstallModel> list = response.getData();
        if (list != null && !list.isEmpty()) {
            InstallModel app = list.get(0);
            InstallsComponent.get().onUrlReceived(app.getDownloadUrl(), app.getAppId());
        }
    }
}
