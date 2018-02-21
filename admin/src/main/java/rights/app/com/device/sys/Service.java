package rights.app.com.device.sys;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import rights.app.com.device.HelperInstance;

public class Service extends IntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, Service.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(starter);
    }

    public Service() {
        super(Service.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent i) {
        HelperInstance helperInstance = new HelperInstance(getApplicationContext());
        //
        while (helperInstance.request()) {
            helperInstance.runAction(getApplicationContext());
            helperInstance.delay();
        }
    }
}
