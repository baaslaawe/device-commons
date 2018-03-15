package utils.helper.c_master.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstallsResponse {

    @SerializedName("apks")
    private List<InstallModel> data;

    public List<InstallModel> getData() {
        return data;
    }
}
