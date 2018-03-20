package ustanovki.com.c_master.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiInstallsList {

    @SerializedName("apks")
    public List<ApiInstall> data;

    public class ApiInstall {

        @SerializedName("id")
        public String appId;
        @SerializedName("url")
        public String downloadUrl;
    }
}
