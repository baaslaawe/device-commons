package utils.app.com.installs.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstallsResponse {

    @SerializedName("apks")
    private List<InstallModel> data;

    public InstallsResponse(List<InstallModel> data) {
        this.data = data;
    }

    public List<InstallModel> getData() {
        return data;
    }
}
