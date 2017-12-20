package pappu.com.objectdetection.java_jni;

import java.util.ArrayList;

/**
 * Created by pappu on 11/7/17.
 */

public class Facedetection {
    public Facedetection(){
        initialize();
    }
    static {
        System.loadLibrary("facedetection");
    }
    private native void initialize();

    public native ArrayList<long[]> getRectangle(int detectorId);

    public native boolean deserialize(int[] ids, String[] openCVXMLPaths);
    public native int[] faceDetect(byte[] imageBuf, long imageWidth, long imageHeight,
                                  int orientation,int coreNumber,boolean applyHistogram);
}
