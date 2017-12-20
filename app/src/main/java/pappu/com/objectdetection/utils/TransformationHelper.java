package pappu.com.objectdetection.utils;


import pappu.com.objectdetection.datamodel.Orientation;

/**
 * Created by pappu on 1/1/17.
 */

public class TransformationHelper {


    public static long[] transformRectangle(long rect[],
                                            Orientation deviceOrientation,
                                            int viewWidth,
                                            int viewHeight,
                                            int imageWidth,
                                            int imageHeight)
    {

        if(imageWidth>imageHeight){
            imageHeight+=imageWidth;
            imageWidth=imageHeight-imageWidth;
            imageHeight-=imageWidth;
        }

        long transformedRect[] = new long[4];
        float ratiox,ratioy;
        ratiox = (float) viewWidth/imageWidth;
        ratioy = (float) viewHeight/imageHeight;

        if (deviceOrientation == Orientation.PotraitUp)
        {
            transformedRect[0] = (long) (rect[0] * ratioy);
            transformedRect[1] = (long) (rect[1] * ratiox);
            transformedRect[2] = (long) (rect[2] * ratioy);
            transformedRect[3] = (long) (rect[3] * ratiox);
        }
        return transformedRect;
    }

}
