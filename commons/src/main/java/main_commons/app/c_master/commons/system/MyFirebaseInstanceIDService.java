package main_commons.app.c_master.commons.system;

import com.google.firebase.iid.FirebaseInstanceIdService;

import main_commons.app.c_master.commons.commons.SdkCommonsImpl;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        SdkCommonsImpl.get().refreshDeviceInfo();
    }
}
