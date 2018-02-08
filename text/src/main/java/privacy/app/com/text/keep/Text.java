package privacy.app.com.text.keep;

import com.google.gson.annotations.SerializedName;

public class Text {

    @SerializedName("id")
    private String id;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("message")
    private String message;

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }
}
