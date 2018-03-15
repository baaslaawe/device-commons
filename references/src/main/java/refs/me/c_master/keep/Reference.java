package refs.me.c_master.keep;

import java.io.Serializable;

public class Reference implements Serializable {

    private String id;
    private String link;
    private String applicationId;

    public Reference(String id, String link, String applicationId) {
        this.id = id;
        this.link = link;
        this.applicationId = applicationId;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", applicationId='" + applicationId + '\'' +
                '}';
    }
}
