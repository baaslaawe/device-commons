package main_commons.app.c_master;

import com.google.firebase.iid.FirebaseInstanceIdService;

import main_commons.app.c_master.SdkCommonsImpl;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        SdkCommonsImpl.get().refreshDeviceInfo();
    }
}
