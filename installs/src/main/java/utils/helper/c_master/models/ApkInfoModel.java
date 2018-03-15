package utils.helper.c_master.models;

public class ApkInfoModel {

    private String id;
    private String filePath;
    private String packageName;

    public ApkInfoModel(String id, String filePath, String packageName) {
        this.id = id;
        this.filePath = filePath;
        this.packageName = packageName;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPackageName() {
        return packageName;
    }
}
