package refs.me.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import java.util.HashMap;
import java.util.Map;

import commons.app.com.commons.commons.SdkComponent;
import okhttp3.ResponseBody;
import refs.me.com.keep.Reference;
import refs.me.com.keep.RefsResponse;
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
        if (response != null && response.getData() != null) {
            onReferenceReceived(response.getData().get(response.getData().size() - 1));
        }
    }

    @Override
    public void onNewAppAdded(String packageName) {
        Timber.i("onNewAppAdded -> packageName[%s]", packageName);
        if (refsController.isReferencedApp(packageName)) {
            sendRefAppInstalled(refsController.getActiveRefId());
            clearActiveReferences();
        }
    }

    @Override
    public void showReference(boolean marketLink, Reference reference) {
        Timber.i("showReference -> marketLink[%s], reference[%s]", marketLink, reference);
        if (marketLink) {
            RefUtils.openMarket(context(), reference.getApplicationId());
        } else {
            Intent intent = RefViewerActivity.createIntent(context(), reference);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context().startActivity(intent);
        }
    }

    public void onReferenceReceived(Reference reference) {
        Timber.i("onReferenceReceived -> reference[%s]", reference);
        refsController.showReference(reference);

    }

    public void clearActiveReferences() {
        refsController.removeActiveReference();
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
    ///////////////////////////////////////////////////////////////////////////

    private void sendRefAppInstalled(String refId) {
        Timber.i("sendRefAppInstalled -> refId[%s]", refId);
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
