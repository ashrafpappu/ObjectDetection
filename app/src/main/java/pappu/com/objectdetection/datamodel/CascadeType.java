package pappu.com.objectdetection.datamodel;

/**
 * Created by Zakir Hossain on 11/21/17.
 */

public enum CascadeType {
    NONE(0),
    FACE_DETECTION(1),
    CAR_DETECTION(2),
    FULL_BODY_DETECTION(3),
    EYE_DETECTION(4);;

    private int id;
    CascadeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
