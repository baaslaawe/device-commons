package manager.app.com.commons.system;

import com.google.firebase.iid.FirebaseInstanceIdService;

import manager.app.com.commons.commons.SdkCommonsImpl;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        SdkCommonsImpl.get().refreshDeviceInfo();
    }
}
