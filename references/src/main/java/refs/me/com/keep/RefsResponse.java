package refs.me.com.keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RefsResponse {

    @SerializedName("refs")
    private List<Reference> data;

    public RefsResponse(List<Reference> data) {
        this.data = data;
    }

    public List<Reference> getData() {
        return data;
    }
}
