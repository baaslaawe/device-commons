package main_commons.app.c_master;

import android.app.Application;

import java.util.List;

public class SdkBuilder {

    private final Application context;

    private String baseUrl;
    private boolean useFullVersion;
    private List<SdkComponent> components;
    private BuildInfo buildInfo;

    public SdkBuilder(Application context) {
        this.context = context;
    }

    public SdkBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public SdkBuilder setUseFullVersion(boolean useFullVersion) {
        this.useFullVersion = useFullVersion;
        return this;
    }

    public SdkBuilder setComponents(List<SdkComponent> components) {
        this.components = components;
        return this;
    }

    public SdkBuilder setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
        return this;
    }

    public void build() {
        SdkCommonsImpl sdkCommons = new SdkCommonsImpl(context,
                buildInfo.applicationId,
                baseUrl,
                buildInfo.launcherActivity,
                buildInfo.isDebugMode,
                useFullVersion,
                components);
        SdkCommonsImpl.initInstance(sdkCommons);
    }

    public static class BuildInfo {
        String applicationId;
        boolean isDebugMode;
        Class launcherActivity;

        public BuildInfo(String applicationId, boolean isDebugMode, Class launcherActivity) {
            this.applicationId = applicationId;
            this.isDebugMode = isDebugMode;
            this.launcherActivity = launcherActivity;
        }
    }
}
