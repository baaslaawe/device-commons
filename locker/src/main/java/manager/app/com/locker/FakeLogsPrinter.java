package manager.app.com.locker;

import java.util.Random;

public class FakeLogsPrinter {

    private static final FakeLogsPrinter instance = new FakeLogsPrinter();

    public static FakeLogsPrinter get() {
        return instance;
    }

    private StringBuilder logsBuilder = new StringBuilder();

    public String generateLog() {
        logsBuilder.append(logs[random.nextInt(logs.length - 1)]);
        logsBuilder.append("\n");
        return logsBuilder.toString();
    }

    public void reset() {
        logsBuilder = new StringBuilder();
    }

    private final static Random random = new Random();

    private static final String[] logs = new String[]{
            "Asset path '/system/framework/com.android.location.provider.jar' does not exist or contains no resources.",
            "Asset path '/system/framework/com.android.media.remotedisplay.jar' does not exist or contains no resources.",
            "Asset path '/system/framework/com.google.android.maps.jar' does not exist or contains no resources.",
            "ActvtMtdtPrvdr: Failed to find activity ag for plugin ipa. Creating default host activity.",
            "ConfigurationChimeraPro: Got null configs for com.google.android.gms.common_auth",
            "Conscrypt: Could not set socket write timeout:",
            "ErrorReporter: reportError [type: 211, code: 2031624]: Error code: 2031624",
            "art: Rejecting re-init on previously-failed class java.lang.Class<com.google.android.gms.org.conscrypt.DelegatingExtendedSSLSession>",
            "linker: libconscrypt_gmscore_jni.so: unused DT entry: type 0xf arg 0x1dc",
            "ResourceType: Found multiple library tables, ignoring...",
            "ProxyAndroidLoggerBackend: Too many Flogger logs received before configuration. Dropping old logs.",
            "ConnectivityService: handleRegisterNetworkRequest checking NetworkAgentInfo [WIFI () - 152]",
            "FA: Processing queued up service tasks: 2",
            "ActivityManager: Killing 28688:com.google.android.play.games.ui/u0a144 (adj 15): empty #26",
            "ActivityManager: getRunningAppProcesses: caller 10239 does not hold REAL_GET_TASKS; limiting output",
            "ConnectivityService: apparently satisfied.  currentScore=100",
            "Adreno-EGL: <qeglDrvAPI_eglInitialize:410>: EGL 1.4 QUALCOMM build",
            "Local Patches: NONE",
            "Build Date: 05/17/15 Sun",
            "Local Branch: mybranch10089422",
            " Remote Branch: quic/LA.BF.1.1.1_rb1.22",
            "OpenGL ES Shader Compiler Version: E031.25.03.06",
            "Reconstruct Branch: AU_LINUX_ANDROID_LA.BF.1.1.1_RB1.05.01.00.042.030 + 6151be1 +  NOTHING",
            "art: Rejecting re-init on previously-failed class java.lang.Class<com.android.webview.chromium.WebViewContentsClientAdapter$WebResourceErrorImpl>",
            "BluetoothAdapter: 1068432639: getState() :  mService = null. Returning STATE_OFF",
            "WifiService: New client listening to asynchronous messages",
            "art: Explicit concurrent mark sweep GC freed 119610(5MB) AllocSpace objects, 24(354KB) LOS objects, 28% free, 39MB/55MB, paused 1.662ms total 122.075ms",
            "ActivityManager: getRunningAppProcesses: caller 10196 does not hold REAL_GET_TASKS; limiting output",
    };
}
