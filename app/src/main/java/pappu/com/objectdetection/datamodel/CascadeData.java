package pappu.com.objectdetection.datamodel;

/**
 * Created by Zakir Hossain on 11/20/17.
 */

public class CascadeData {

    private String cascadeFileName;
    private int resourceId;
    private String cascadeFilePath;
    private CascadeType cascadeType = CascadeType.NONE;

    public CascadeData(String cascadeFileName, int resourceId, CascadeType cascadeType) {
        this.cascadeFileName = cascadeFileName;
        this.resourceId = resourceId;
        this.cascadeType = cascadeType;
    }

    public String getCascadeFileName() {
        return cascadeFileName;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getCascadeFilePath() {
        return cascadeFilePath;
    }

    public void setCascadeFilePath(String cascadeFilePath) {
        this.cascadeFilePath = cascadeFilePath;
    }

    public CascadeType getCascadeType() {
        return cascadeType;
    }
}
