package manager.app.com.ussd.keep;

public class UssdCode {

    private String id;
    private String code;

    public UssdCode(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
