package manager.app.com.commons.system;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import manager.app.com.commons.commons.SdkCommonsImpl;
import manager.app.com.commons.commons.SdkComponent;
import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Timber.i("FCM -> onMessageReceived -> data[%s]", data);
        Bundle payload = createBundle(data);
        if (payload != null) {
            for (SdkComponent component : SdkCommonsImpl.get().getComponents()) {
                component.onFcmMessageReceived(payload);
            }
        }
    }

    @Nullable
    private Bundle createBundle(Map<String, String> mapData) {
        if (mapData == null || mapData.isEmpty()) {
            return null;
        }
        Bundle data = new Bundle();
        for (Map.Entry<String, String> entry : mapData.entrySet()) {
            data.putString(entry.getKey(), entry.getValue());
        }
        return data;
    }
}
