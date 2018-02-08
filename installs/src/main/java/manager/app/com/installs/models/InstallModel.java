package manager.app.com.installs.models;

import com.google.gson.annotations.SerializedName;

public class InstallModel {

    @SerializedName("id")
    private String appId;
    @SerializedName("url")
    private String downloadUrl;

    public InstallModel(String appId, String downloadUrl) {
        this.appId = appId;
        this.downloadUrl = downloadUrl;
    }

    public String getAppId() {
        return appId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
