package pappu.com.objectdetection.datamodel;

import java.util.ArrayList;
import java.util.List;

import pappu.com.objectdetection.R;

/**
 * Created by Zakir Hossain on 11/20/17.
 */

public class CascadeDataCollection {
    private static int resource[] = {
//            R.raw.haarcascade_frontalface_alt2,
            R.raw.cars
//            R.raw.net_pedestrian_cascade_web_lbp
    };

    private static String xmlFileNames[] = {
//            "haarcascade_frontalface_alt2.xml",
            "cars.xml"
//            "net_pedestrian_cascade_web_lbp.xml"

    };

    private static CascadeType cascadeTypes[] = {
//            CascadeType.FACE_DETECTION,
            CascadeType.CAR_DETECTION
//            CascadeType.FULL_BODY_DETECTION
    };


    public static List<CascadeData> getCascadeDataList() {
        List<CascadeData> cascadeDataList = new ArrayList<>();
        for (int i = 0; i < xmlFileNames.length; i++) {
            CascadeData cascadeData = new CascadeData(xmlFileNames[i], resource[i], cascadeTypes[i]);
            cascadeDataList.add(cascadeData);
        }
        return cascadeDataList;
    }

}
